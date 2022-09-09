package com.websocket.devicewebsocket.controller;

import com.websocket.devicewebsocket.socket.WebSocket;
import com.websocket.devicewebsocket.util.FolderListen;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocketController {

        @GetMapping("/broadcast")
        public void broadcast(){
            WebSocket.broadcast("这是测试消息");
        }

}
