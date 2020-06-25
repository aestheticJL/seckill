package com.mmt.seckill.exception;

public class RespBean {
    Integer status;
    String message;
    Object obj;

    public RespBean(Integer status, String message, Object obj) {
        this.status = status;
        this.message = message;
        this.obj = obj;
    }

    public static RespBean ok(String msg){
        return new RespBean(200, msg, null);
    }

    public static RespBean ok(String msg, Object obj){
        return new RespBean(200, msg, obj);
    }

    public static RespBean error(String msg){
        return new RespBean(500, msg, null);
    }

    public static RespBean error(String msg, Object obj){
        return new RespBean(500, msg, obj);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return obj;
    }

    public void setObject(Object obj) {
        this.obj = obj;
    }
}
