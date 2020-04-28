package com.yearn.springboot.ws;

/**
 * websocket 事件监听
 * Created by Zhou_Bing on 2019/6/3.
 */
public interface WebSocketListener {

    public void execute(String message);

}
