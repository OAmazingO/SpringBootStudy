package com.yearn.springboot.ws;

import java.io.Serializable;

/**
 * web socket返回对象实体
 *
 * Created by Zhou_Bing on 2019/6/3.
 */
public class WsResponse implements Serializable{

    private Integer retCode;
    private Object data;

    public WsResponse() {
    }

    public WsResponse(Integer retCode) {
        this.retCode = retCode;
    }

    public WsResponse(Integer retCode, Object data) {
        this.retCode = retCode;
        this.data = data;
    }

    public WsResponse setRetCode(Integer retCode){
        this.retCode = retCode;
        return this;
    }

    public WsResponse setData(Object data){
        this.data = data;
        return this;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public Object getData() {
        return data;
    }
}
