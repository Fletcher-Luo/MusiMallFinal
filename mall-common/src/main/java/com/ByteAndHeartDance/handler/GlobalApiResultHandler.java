package com.ByteAndHeartDance.handler;

import cn.hutool.core.lang.UUID;
import com.ByteAndHeartDance.utils.ApiResult;
import com.ByteAndHeartDance.utils.ApiResultUtil;
import com.alibaba.excel.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


public class GlobalApiResultHandler implements ResponseBodyAdvice<Object> {

    public static final String URL_PREFIX = "/v1";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        String requestURI = request.getRequestURI();
        return matchUrl(requestURI);
    }

    private boolean matchUrl(String uri) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        return uri.contains(URL_PREFIX);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String traceId = String.format("%s - %s", httpServletRequest.getRequestURI(), UUID.fastUUID().toString(true));
            ((ApiResult<?>) body).setTraceId(traceId);
            return (ApiResult) body;
        }
        return ApiResultUtil.success(body);
    }
}