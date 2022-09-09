package com.websocket.devicewebsocket.socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@ServerEndpoint(value = "/websocket")
@Component
public class WebSocket {

    private static Map<String,WebSocket> webSocketMap = new LinkedHashMap<>();

    private static int count = 0;

    private Session session;

    private Logger logger  = LoggerFactory.getLogger(WebSocket.class);

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketMap.put(session.getId(),this);
        addCount();
        logger.info("new connect join:",session.getId());
    }

    //receive message
    public void onMessage(String message,Session session){
        logger.info("receive client message:",session.getId(),message);
        try{
            this.sendMessage("receive message"+message);
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }

    //处理错误
    @OnError
    public void onError(Throwable error,Session session){
        logger.info("发生错误{},{}",session.getId(),error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose(){
        webSocketMap.remove(this.session.getId());
        reduceCount();
        logger.info("连接关闭:{}",this.session.getId());
    }

    //send message
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void broadcast(String message){
        WebSocket.webSocketMap.forEach((k,v) ->{
            try {
                v.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static synchronized void addCount() {
        WebSocket.count++;
    }

    //获取在线连接数目
    public static int getCount(){
        return count;
    }

    public static synchronized void reduceCount(){
        WebSocket.count--;
    }


}
