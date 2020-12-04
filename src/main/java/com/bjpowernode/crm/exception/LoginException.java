package com.bjpowernode.crm.exception;

public class LoginException extends Exception {
    /**
     * 自定义登录异常
     * @param msg 异常信息
     */
    public LoginException(String msg){
        super(msg);
    }
}
