package com.usping.kdsn.util.filter;

import com.usping.kdsn.util.config.ConstantConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(filterName = "authCheck", urlPatterns = "/api/*")
public class AuthCheckFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);
    private final String OPTIONS = "OPTIONS";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("filter启动");
    }

    /**
     * 通过验证jjwt产生的token, 校验其中包含的信息
     * 用于验证用户token是否有效
     * 后期应该改为interceptor
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;


        if (OPTIONS.equals(req.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try{
            logger.info("获取token："+req.getHeader("Authorization"));
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(ConstantConfig.getTokenPass()))
                    .parseClaimsJws(req.getHeader("Authorization")).getBody();

        }catch (IllegalArgumentException | MalformedJwtException e) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.info("获取token异常, 禁止请求");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
