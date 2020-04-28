package com.yearn.springboot.ws;

import com.alibaba.fastjson.JSONObject;
import com.cma.iooip.eqm.task.SpringUtil;
import com.link.uaap.security.authentication.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * web socket服务
 * Created by Zhou_Bing on 2019/6/3.
 */
@ServerEndpoint("/eqm/websocket")
@Component
public class WebSocketServer {

    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    //{userCode:[websocket...]...} 用户可能在多个地方同时登陆
    private static ConcurrentHashMap<String,CopyOnWriteArraySet<WebSocketServer>> webSocketList = new ConcurrentHashMap<>();
    private Session session;
    private String userCode;

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        String token = null;
        if(session.getRequestParameterMap().get("access_token")!=null &&
                session.getRequestParameterMap().get("access_token").size()>0){
            token = session.getRequestParameterMap().get("access_token").get(0);
        }else{
            token = session.getRequestParameterMap().get("ticket").get(0);
        }
        JwtTokenUtil jwtTokenUtil = SpringUtil.getBean("jwtTokenUtil",JwtTokenUtil.class);
        if(jwtTokenUtil!=null){
            String userCode = null;
            if(session.getRequestParameterMap().get("access_token")!=null &&
                    session.getRequestParameterMap().get("access_token").size()>0){
                userCode = UserUtil.getUserCodeByToken(token);
            }else{
                userCode = jwtTokenUtil.getUsernameFromToken(token);
            }
            if(!StringUtils.isEmpty(userCode)){
                CopyOnWriteArraySet<WebSocketServer> socketServers = null;
                if(webSocketList.get(userCode)!=null){
                    socketServers = webSocketList.get(userCode);
                    socketServers.add(this);
                }else{
                    socketServers = new CopyOnWriteArraySet<>();
                    socketServers.add(this);
                    webSocketList.put(userCode,socketServers);
                }
                this.userCode = userCode;
            }
        }
        logger.info("连接接入....");
    }


    /**
     * 正常或异常断开都会调用
     */
    @OnClose
    public void onClose(){
        CopyOnWriteArraySet sockets = webSocketList.get(this.userCode);
        logger.info("当前用户的连接数:"+sockets.size());
        if(sockets!=null){
            logger.info("socket对象:"+this);
            sockets.remove(this);
            if(sockets.size() == 0){
                logger.info("webSocketList size:"+webSocketList.size());
                webSocketList.remove(userCode);
            }
        }
        logger.info("连接断开....");
    }

    @OnMessage
    public void onMessage(String message,Session session){
        logger.info("接收到消息:"+message);
        //todo 如何优雅的处理 监听
        if("哈喽,连接一下啊".equals(message)){
            //发送视频流
            File file = new File("D:\\cma\\test.mp4");
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                byte[] bs = new byte[1024*1024*8]; //8MB
                int len = 0;
                while ((len = bis.read(bs)) != -1) {
                    String msg = str2Unicode(new String(bs,0,len,"ISO-8859-1"));
                    sendMessage(msg);
                }
                // close
                sendMessage(str2Unicode("over"));
                bis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        error.printStackTrace();
    }

    public void sendMessage(Object message) throws IOException, EncodeException {
        this.session.isOpen();
        this.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
    }

    public static void sendMessage(String userCode,Object message) throws IOException, EncodeException {
        CopyOnWriteArraySet<WebSocketServer> sockets = webSocketList.get(userCode);
        if(sockets!=null){
            for (WebSocketServer socket : sockets) {
                socket.sendMessage(message);
            }
        }
    }

    public static void sendMessage(Set<String> userCodes,Object message) throws IOException, EncodeException {
        if(userCodes!=null && userCodes.size()>0){
            for (String userCode : userCodes) {
                sendMessage(userCode,message);
            }
        }
    }



    /**
     * 字符串转unicode
     * @param str
     * @return
     */
    public static String str2Unicode(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); // 取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); // 取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
        }
        return sb.toString();
    }

    /**
     * 字符串转unicode
     *
     * @param str
     * @return
     */
    public static String strToUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            sb.append("\\u" + Integer.toHexString(c[i]));
        }
        return sb.toString();
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
