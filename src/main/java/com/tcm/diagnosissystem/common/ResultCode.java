package com.tcm.diagnosissystem.common;


public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),


    USERNAME_EXISTS(1001, "用户名已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用");
    private final int code;
    private final String message;
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
