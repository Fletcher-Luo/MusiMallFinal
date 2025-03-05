package com.ByteAndHeartDance.order.feign;

import com.ByteAndHeartDance.utils.TokenUtil;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        //添加token
        requestTemplate.header(TokenUtil.AUTHORIZATION, TokenUtil.getAuthorization(request));
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(50000, 10000); // 连接超时时间 5000 毫秒，读取超时时间 10000 毫秒
    }
}
