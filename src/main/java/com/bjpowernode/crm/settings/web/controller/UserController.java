package com.bjpowernode.crm.settings.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");
        String path = request.getServletPath();

        if ("/settings/user/login.do".equals(path)) {

            login(request, response);

        } else if ("/settings/user/xxx.do".equals(path)) {

            //xxx(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到验证登录操作");
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文形式转换成MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收ip地址
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        //业务层开发,统一使用代理类接口对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());


        try {
            User user = us.login(loginAct, loginPwd, ip);
            request.getSession().setAttribute("user", user);
            //如果业务执行到此处,说明业务层没有为controller抛出任何异常,表示登录成功
            /*
            {"success":true}
             */
            PrintJson.printJsonFlag(response,true);//将布尔值解析为json串

        } catch (Exception e) {
            e.printStackTrace();
            //一旦程序执行了catch块信息,说明业务层验证登录失败,为controller抛出了异常,表示登录失败
            /*
            {"success":false,"msg":?}
             */
            String msg = e.getMessage();
            /*
            作为控制器,应该向ajax提供多项信息,
            可以有两种手段处理,一种是将多项信息打包为Map,将Map解析为json串
            一种是创建一个为前端展现值的对象 Vo
                        private boolean success;
                        private String msg;

              如果对于展现的信息还会大量使用,我们创建一个Vo类,使用方便
              如果对于展现的信息只有在这个需求中使用,使用map就可以
             */

            Map<String,Object> map= new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }
    }
}
