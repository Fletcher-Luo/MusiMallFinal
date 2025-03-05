package com.ByteAndHeartDance.utils;

import com.ByteAndHeartDance.constant.NumberConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import java.util.List;

public abstract class TokenUtil {

	private static final String AUTHORIZATION_PREFIX = "Basic";
	private static final String AUTHORIZATION_SEPARATE = "@";
	public static final String AUTHORIZATION = "Authorization";

	private TokenUtil() {
	}

	/**
	 * 从Header中获取 Authorization
	 *
	 * @param request 请求
	 * @return Authorization
	 */
	public static String getAuthorization(HttpServletRequest request) {
		return request.getHeader(AUTHORIZATION);
	}

	/**
	 * 从authorization中解析token
	 * <p>
	 * authorization字符串是下面这样的：
	 * Basic eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXNhbiIsImV4cCI6MTcwNTAzOTA3N30.DZV6CZYGla74CZaXU1sqnX9R_x5YxfTM-DWObURn3Uhr1E88XsOxOz8F_MDfh8AaVFm87zlGXAENC8soZNz0Qw
	 *
	 * @param request 用户请求
	 * @return token
	 */
	public static String getTokenForAuthorization(HttpServletRequest request) {
		String authorization = request.getHeader(AUTHORIZATION);
		return parserToken(authorization);
	}

	/**
	 * 从authorization中解析token
	 * <p>
	 * authorization字符串是下面这样的：
	 * Basic@eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXNhbiIsImV4cCI6MTcwNTAzOTA3N30.DZV6CZYGla74CZaXU1sqnX9R_x5YxfTM-DWObURn3Uhr1E88XsOxOz8F_MDfh8AaVFm87zlGXAENC8soZNz0Qw
	 *
	 * @param request 用户请求
	 * @return token
	 */
	public static String getToken(ServerHttpRequest request) {
		List<String> params = request.getHeaders().get(AUTHORIZATION);
		String authorization = null;
		if (!CollectionUtils.isEmpty(params)) {
			authorization = params.get(0);
		}
		return parserToken(authorization);
	}

	private static String parserToken(String authorization) {
		if (!StringUtils.hasLength(authorization) || !authorization.contains(AUTHORIZATION_PREFIX) || !authorization.contains(AUTHORIZATION_SEPARATE)) {
			return null;
		}

		String[] values = authorization.split(AUTHORIZATION_SEPARATE);
		if (values.length != NumberConstant.NUMBER_2) {
			return null;
		}
		return values[1];
	}
}
