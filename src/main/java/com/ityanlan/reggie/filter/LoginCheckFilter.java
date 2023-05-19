package com.ityanlan.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ityanlan.reggie.common.BaseContext;
import com.ityanlan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录的过滤器
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
//    路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求{}",requestURI);
//        定义不需要处理的路径
        String[] url = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
//        2、判断本次请求是否需要处理
        boolean check = check(url, requestURI);
//        3、如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        4-1、后台管理判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            Long empId = (Long) request.getSession().getAttribute("employee");

//            local线程中存入id
            BaseContext.setCurrentId(empId);

            log.info("用户已经登录，id为{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        //        4-2、移动端用户判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user")!=null){
            Long userId = (Long) request.getSession().getAttribute("user");

//            local线程中存入id
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
//        5、如果未登录则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
