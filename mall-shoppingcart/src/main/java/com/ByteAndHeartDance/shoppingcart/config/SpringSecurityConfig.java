package com.ByteAndHeartDance.shoppingcart.config;

import com.ByteAndHeartDance.shoppingcart.filter.JwtTokenFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: Sparrow @Description: SpringSecurity配置类 @DateTime: 2025/1/21 16:11
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig implements ApplicationContextAware {
  private ApplicationContext applicationContext;
  private JwtTokenFilter jwtTokenFilter;

  @Bean
  public JwtTokenFilter jwtTokenFilter() {
    jwtTokenFilter = new JwtTokenFilter();
    return jwtTokenFilter;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // 密码加密方式
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    return httpSecurity
        // 禁用 CSRF
        .csrf(csrf -> csrf.disable())
        // 授权异常
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
        .headers(frameOptions -> frameOptions.disable())

        // 不创建会话
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            request -> {
              request
                  .requestMatchers(
                      HttpMethod.GET,
                      "/*.html",
                      "/*/*.html",
                      "/*/*.css",
                      "/*/*.js",
                      "/websocket/*",
                      "/job/*",
                      "/init/*")
                  .permitAll()
                  .requestMatchers("/swagger-ui.html")
                  .permitAll()
                  .requestMatchers("/swagger-ui/*")
                  .permitAll()
                  .requestMatchers("/swagger-resources/*")
                  .permitAll()
                  .requestMatchers("/webjars/*")
                  .permitAll()
                  .requestMatchers("/*/api-docs/**")
                  .permitAll()
                  .requestMatchers("/avatar/*")
                  .permitAll()
                  .requestMatchers("/druid/*")
                  .permitAll()
                  .requestMatchers(HttpMethod.OPTIONS, "/*")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
