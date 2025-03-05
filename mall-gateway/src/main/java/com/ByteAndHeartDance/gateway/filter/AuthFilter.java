package com.ByteAndHeartDance.gateway.filter;

import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.helper.TokenHelper;
import com.ByteAndHeartDance.utils.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.base.Throwables;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import com.alibaba.nacos.shaded.com.google.common.base.Joiner;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ByteAndHeartDance.utils.TokenUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private static Joiner joiner = Joiner.on("");

    @Value("${gateway.filter.noAuth:}")
    private String noAuth;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        // 网关不进行拦截的URI配置，常见如验证码、Login接口
        if (isNoAuth(uri.getPath())) {
            return handleResponse(chain, exchange);
        }

        String token = TokenUtil.getToken(request);
        if (StringUtils.hasLength(token)) {
            TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);

            if (Objects.nonNull(tokenHelper)) {
                try {
                    String username = tokenHelper.getUsernameFromToken(token);
                    if (StringUtils.hasLength(username)) {
                        UserDetails userDetails = tokenHelper.getUserDetailsFromUsername(username);
                        List<String> elPermissions = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
						// 检查权限：是否包含 admin 或 user 角色
						boolean hasPermission = elPermissions.stream().anyMatch(perm -> "admin".equals(perm) || "user".equals(perm));
						if (hasPermission) {
							return handleResponse(chain, exchange);
						} else {
							throw new BusinessException("权限不足，需 admin 或 user 角色");
						}
					}
                    throw new BusinessException("请先登录");
                } catch (BusinessException e) {
                    throw new BusinessException("请先登录");
                }
            }
        }

        return handleResponse(chain, exchange);
    }

    private Mono<Void> handleResponse(GatewayFilterChain chain, ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                if (getStatusCode().equals(HttpStatus.OK) && body instanceof Flux) {
                    // 获取ContentType，判断是否返回JSON格式数据
                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                    if (StringUtils.hasLength(originalResponseContentType) && originalResponseContentType.contains(APPLICATION_JSON_VALUE)) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        //（返回数据内如果字符串过大，默认会切割）解决返回体分段传输
                        return super.writeWith(fluxBody.buffer().handle((dataBuffers, sink) -> {
                            List<String> list = new ArrayList<>();
                            dataBuffers.forEach(dataBuffer -> {
                                try {
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    DataBufferUtils.release(dataBuffer);
                                    list.add(new String(content, "utf-8"));
                                } catch (Exception e) {
                                    log.info("加载Response字节流异常，失败原因：{}", Throwables.getStackTraceAsString(e));
                                }
                            });
                            String responseData = joiner.join(list);
                            log.info("requestURI:{},responseData：{}", exchange.getRequest().getURI(), responseData);

                            String apiResult = getApiResult(responseData);
                            byte[] uppedContent = new String(apiResult.getBytes(), Charset.forName("UTF-8")).getBytes();
                            originalResponse.getHeaders().setContentLength(uppedContent.length);
                            sink.next(bufferFactory.wrap(uppedContent));
                        }));
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());

    }

    private String getApiResult(String responseData) {
        StringBuilder apiResultBuilder = new StringBuilder("{\"code\":200,\"message\":null,\"data\":");
        apiResultBuilder.append(responseData);
        apiResultBuilder.append("}");
        return apiResultBuilder.toString();
    }

    @NotNull
    private Mono<Void> getVoidMono(ServerWebExchange serverWebExchange, Exception e) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        byte[] bytes = JSONObject.toJSONString(e.getMessage()).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = serverWebExchange.getResponse().bufferFactory().wrap(bytes);
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * URI是否以什么打头
     *
     * @param requestUri
     * @return
     */
    private boolean isNoAuth(String requestUri) {
        boolean flag = false;
        if (StringUtils.hasLength(noAuth)) {
            for (String url : noAuth.split(",")) {
                if (requestUri.startsWith(url)) {
                    return true;
                }
            }
        }

        return flag;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}