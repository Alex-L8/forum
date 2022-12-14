package com.lcx.config;

import com.lcx.util.ForumConstant;
import com.lcx.util.NiuKeUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Create by LCX on 9/4/2022 9:34 PM
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ForumConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like/**",
                        "/follow",
                        "/unFollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/highlight"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

        // 权限不够时的处理
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            // 没有登录
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                String xRequestedWith = request.getParameter("x-requested-with");
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(NiuKeUtil.getJSONString(403, "您还没有登录!"));
                } else {
                    response.setContentType(request.getContextPath()+"/login");
                }
            }
        }).accessDeniedHandler(new AccessDeniedHandler() {
            // 权限不足
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                String xRequestedWith = request.getParameter("x-requested-with");
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(NiuKeUtil.getJSONString(403, "您没有访问此功能的权限!"));
                } else {
                    response.setContentType(request.getContextPath()+"/denied");
                }
            }
        });

        // security底层默认会拦截/logout请求，并执行退出处理
        // 覆盖它默认的逻辑，才能执行我们的退出代码
        http.logout().logoutUrl("/securityLogout"); // 随便改一个，/logout就不会被security拦截
    }
}
