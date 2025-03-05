package com.ByteAndHeartDance.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");//允许跨域访问任何请求方式：post get put delete
//        config.addAllowedOrigin("*");//springboot2.4之前的使用
        config.addAllowedOriginPattern("*");//允许什么样的请求头(springboot2.4之后使用)
        config.addAllowedHeader("*");//允许那种请求来源
        config.setAllowCredentials(true);//设置是否允许cookie进行跨域
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
