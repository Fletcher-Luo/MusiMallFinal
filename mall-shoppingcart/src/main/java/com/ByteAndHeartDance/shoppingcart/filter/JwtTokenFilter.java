package com.ByteAndHeartDance.shoppingcart.filter;


import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.helper.TokenHelper;
import com.ByteAndHeartDance.utils.SpringUtil;
import com.ByteAndHeartDance.utils.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

public class JwtTokenFilter extends GenericFilterBean {

    public final static String FILTER_ERROR = "filterError";
    public final static String FILTER_ERROR_PATH = "/throw-error";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String token = TokenUtil.getTokenForAuthorization(httpServletRequest);

        if (Objects.isNull(token)) {
            // 无token，可能是swagger，直接放行。（因为在网关已经校验了）
            // 如果做拦截，会导致swagger无法访问
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }
        TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);

        // 有token，解析token
        if (Objects.nonNull(tokenHelper)) {
            try {
                String username = tokenHelper.getUsernameFromToken(token);

                if (StringUtils.hasLength(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = tokenHelper.getUserDetailsFromUsername(username);
                    if (Objects.nonNull(userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(httpServletRequest, servletResponse);
            } catch (BusinessException e) {
                handleException((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, e);
            }
        } else {
            filterChain.doFilter(httpServletRequest, servletResponse);
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 BusinessException e) throws ServletException, IOException {
        request.setAttribute(FILTER_ERROR, e);
        request.getRequestDispatcher(FILTER_ERROR_PATH).forward(request, response);
    }
}
