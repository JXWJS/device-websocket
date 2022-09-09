package com.websocket.devicewebsocket.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


//@Configuration
//@EnableScheduling
@Component
public class TaskJob  implements ApplicationListener<ContextRefreshedEvent> {

//    @Scheduled(cron = "*/1 * * * * ?")
//    public void job1(){
//        new FolderListen().start(new String[]);
//        WebSocket.broadcast();
//    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        FolderListen folderListen = applicationContext.getBean(FolderListen.class);
        folderListen.run();
    }
}
