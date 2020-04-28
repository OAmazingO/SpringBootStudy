package com.yearn.springboot.controller;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * 内外网视频流转换器
 *
 * Created by zhoub on 2019/12/11.
 */
@RestController //相对与 @Controller + @ResponseBody
public class TestController2 {

    public static WebSocketClient client;
    public static Queue<byte[]> videoData = new LinkedBlockingQueue<>();

    {
        try {
            client = new WebSocketClient(new URI("ws://localhost:9090/eqm/websocket?ticket=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0LWd1b2oiLCJhdWRpZW5jZSI6IkFVRElFTkNFX1dFQiIsImNyZWF0ZWQiOjE1ODgwNDMxNjE0MzEsInVzZXJuYW1lQ24iOiLln7norq3lm73lrrbnuqfnlKjmiLciLCJleHAiOjE1ODg2NDMxNjEsInVzZXJpZCI6IjMxNDkzIn0.cAe3G6rx34mSDyE1h9f7tGR1oXp2TBqbR7njY7RtzxeEpIGNVzdbs0Fu9cuod756sL46nnNXwtbCnjeAmcMcIQ"),new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("握手成功");
                }

                @Override
                public void onMessage(String msg) {
                    System.out.println("收到消息=========="+msg.length());
                    msg = msg.substring(1,msg.length()-1);
                    msg = msg.replaceAll("\\\\u","\\u");
                    msg = unicodeToStr(msg);
                    byte[] bs = new byte[0];
                    try {
                        bs = msg.getBytes("ISO-8859-1");
                        videoData.add(bs);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(msg.equals("over")){
                        client.close();
                    }

                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("链接已关闭");
                }

                @Override
                public void onError(Exception e){
                    e.printStackTrace();
                    System.out.println("发生错误已关闭");
                }
            };
            client.connect();
            System.out.println(client.getDraft());
            while(!client.getReadyState().equals(ReadyState.OPEN)){
                System.out.println("正在连接...");
            }
            //连接成功,发送信息
            client.send("哈喽,连接一下啊");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }



    @GetMapping("/api/getVio4")
    public void getVio4(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                String.format("inline;filename=\"%s\"", "test.mp4"));
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            byte[] buf = new byte[1024*1024*8];
            int len=0;
            StringBuilder sb = new StringBuilder();

            byte[] data = null;

            while((data=videoData.poll())!=null){
                System.out.println(data.length);
                os.write(data);
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * unicode转字符串
     *
     * @param unicode
     * @return
     */
    public static String unicodeToStr(String unicode) {
        StringBuilder sb = new StringBuilder();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int index = Integer.parseInt(hex[i], 16);
            sb.append((char) index);
        }
        return sb.toString();
    }

}
