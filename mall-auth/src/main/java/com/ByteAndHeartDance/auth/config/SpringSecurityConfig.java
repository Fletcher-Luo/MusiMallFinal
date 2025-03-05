package com.ByteAndHeartDance.auth.config;

import com.ByteAndHeartDance.annotation.NoLogin;
import com.ByteAndHeartDance.auth.filter.JwtTokenFilter;
import com.ByteAndHeartDance.auth.service.user.UserDetailsServiceImpl;
import com.ByteAndHeartDance.auth.utils.NoLoginMap;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Sparrow
 * @Description: SpringSecurity配置类
 * @DateTime: 2025/1/21 16:11
 **/
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
	public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setUserDetailsService(applicationContext.getBean(UserDetailsServiceImpl.class));
		return daoAuthenticationProvider;
	}
	@Bean
	public AuthenticationManager authenticationManager( DaoAuthenticationProvider daoAuthenticationProvider) {
		List list = new ArrayList();
//		authenticationProviders.add(smsAuthenticationProvider);
		list.add(daoAuthenticationProvider);
		ProviderManager authenticationManager = new ProviderManager(list);
		return authenticationManager;
	}


	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		initNoLogin(applicationContext);
		return httpSecurity
				// 禁用 CSRF
				.csrf(csrf -> csrf.disable())
				// 授权异常
				.exceptionHandling(exception -> exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
				.headers(frameOptions -> frameOptions.disable())

				// 不创建会话
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request -> {
					request.requestMatchers(HttpMethod.GET,
									"/*.html",
									"/*/*.html",
									"/*/*.css",
									"/*/*.js",
									"/websocket/*",
									"/job/*",
									"/init/*"
							).permitAll()
							.requestMatchers("/swagger-ui.html").permitAll()
							.requestMatchers("/swagger-ui/*").permitAll()
							.requestMatchers("/swagger-resources/*").permitAll()
							.requestMatchers("/webjars/*").permitAll()
							.requestMatchers("/*/api-docs/**").permitAll()
							.requestMatchers("/avatar/*").permitAll()
							.requestMatchers("/druid/*").permitAll()
							.requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()
							.requestMatchers(NoLoginMap.getNoLoginUrlSet().toArray(new String[0])).permitAll()
							.anyRequest().authenticated();
				})
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	private void initNoLogin(ApplicationContext applicationContext) {
		Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
		if (MapUtils.isEmpty(handlerMethodMap)) {
			return;
		}
		Set<String> noLoginUrls = new HashSet<>();
		for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
			HandlerMethod handlerMethod = infoEntry.getValue();
			//获取加上noLogin注解的url
			NoLogin noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
			if (null != noLogin && null != infoEntry.getKey().getPatternsCondition()) {
				noLoginUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
			}
		}
		NoLoginMap.initSet(noLoginUrls);
	}

}
