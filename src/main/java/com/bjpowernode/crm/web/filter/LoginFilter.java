package com.bjpowernode.crm.web.filter;
/**
 * 防止用户恶意登录过滤器
 */

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        System.out.println("进入到验证有没有登录过的过滤器");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        //不应该拦截的资源
        if ("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)) {
            chain.doFilter(req, resp);
        } else {
            //如果user不等于null,说明登录过,放行
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user != null) {
                chain.doFilter(req, resp);
            } else {
                //重定向到登录页
            /*
            重定向路径怎么写?
            在实际项目开发中,对于路径的使用,应该都是用绝对路径
            关于转发和重定向的写法如下:
                转发:
                    使用的是一种特殊的绝对路径使用方式,这种绝对路径前面不加/项目名,这种路径也称为内部路径
                重定向:
                    使用的是传统绝对路径的写法,前面必须以/项目名开头,后面跟具体的资源路径
                    /crm/login.jsp
             */
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }


        }


    }

    public void init(FilterConfig config) throws ServletException {

    }

}
