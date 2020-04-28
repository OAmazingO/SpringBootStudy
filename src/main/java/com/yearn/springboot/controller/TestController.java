package com.yearn.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhoub on 2019/12/11.
 */
@RestController //相对与 @Controller + @ResponseBody
public class TestController {

    private static Socket socket = null;
    private static Socket socket2 = null;
    private static ServerSocket serverSocket = null;
    static {
        String host = "localhost";
        int port = 8081;
        try {
            socket = new Socket(host,port);
//            serverSocket = new ServerSocket(8081);
//            socket2 = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/getVio")
    public void getVio(HttpServletRequest request, HttpServletResponse response){
        File file = new File("C:\\Users\\zhoub\\Videos\\Captures\\test.mp4");
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    String.format("inline;filename=\"%s\"", "test.mp4"));
            OutputStream os = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bs = new byte[1024*1024*8]; //8MB
            int len = 0;
            while ((len = bis.read(bs)) != -1) {
                System.out.println(len);
                os.write(bs,0,len);
            }
            // 关闭流
            bis.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/getVio2")
    public void getVio2(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                String.format("inline;filename=\"%s\"", "test.mp4"));
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            byte[] buf = new byte[1024*1024*8];
            int len=0;
            StringBuilder sb = new StringBuilder();
            while((len=bis.read(buf))!=-1){
                System.out.println(len);
                os.write(buf,0,len);
                System.out.println("发送成功"+len);
                if("over".equals(new String(buf,0,len,"ISO-8859-1"))){
                    bis.close();
                    os.flush();
                    os.close();
                    break;
                }
            }
            // 关闭流
            /*bis.close();
            os.flush();
            os.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/api/getVio3")
    public void getVio3(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                String.format("inline;filename=\"%s\"", "test.mp4"));
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            InputStream inputStream = socket2.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            byte[] buf = new byte[1024*1024*8];
            int len=0;
            StringBuilder sb = new StringBuilder();
            while((len=bis.read(buf))!=-1){
                System.out.println(len);
                os.write(buf,0,len);
            }
            // 关闭流  ()
            bis.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
