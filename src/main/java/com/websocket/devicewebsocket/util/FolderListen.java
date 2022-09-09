package com.websocket.devicewebsocket.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import com.websocket.devicewebsocket.socket.WebSocket;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Component
public class FolderListen {

    @Value("${host.file.path}")
    private String filePath;

    @Value("${host.ip.path}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(FolderListen.class);

    public  void run() {
        File file = FileUtil.file(filePath);

        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.EVENTS_ALL);

        watchMonitor.setWatcher(new Watcher() {
            @Override
            public  void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                System.out.println("创建：{}-> {}" + currentPath.toString()+"\\"+ obj);
                try {
                    if (obj.toString().contains(".PNG")) {
//                    String prefix = url+currentPath.toString().split("\\\\")[4]+"/"+currentPath.toString().split("\\\\")[5]+"/";
//                    logger.info("定位资源地址{} -> {}"+prefix+obj);
                        String order = obj.toString().split("IM")[1].split(".PNG")[0];
                        int value = Integer.valueOf(order);
                        logger.info("------------>开始处理base64压缩图片");
                        logger.info("------------>开始延迟550");
                        Thread.sleep(550);
                        logger.info("------------>延迟结束");
                        if (value % 3 == 0) {
                            WebSocket.broadcast("0;" + getImageBase64(currentPath.toString() + "\\" + obj));
                        } else if (value % 3 == 1) {
                            WebSocket.broadcast("1;" + getImageBase64(currentPath.toString() + "\\" + obj));
                        } else {
                            WebSocket.broadcast("2;" + getImageBase64(currentPath.toString() + "\\" + obj));
                        }
                        logger.info("------------>广播完成");

                    }
                }catch (Exception e){
                   e.printStackTrace();
                }

            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
//                System.out.println("修改：{}-> {}" + currentPath + obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
//                System.out.println("删除：{}-> {}" + currentPath + obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
//                System.out.println("Overflow：{}-> {}" + currentPath + obj);
            }
        });

        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        watchMonitor.setMaxDepth(4);
        //启动监听
        watchMonitor.start();

    }

    public String getImageBase64(String imgPath){
        InputStream inputStream = null;
        byte[] data = null;
        try{
            inputStream = new FileInputStream(imgPath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        }catch (IOException e){
            logger.error("read photo stream error!");
        }
        logger.info(String.valueOf(data.length));
//        return resizeImageTo40K(new String(Base64.encodeBase64(data)));
//        return new String(Base64.encodeBase64(data));
        Base64 encoder = new Base64();
        return encoder.encodeAsString(data);
    }

    public   String reduceMargin (String path)  {
        BufferedImage  output  = null;
        File file = null;
        Base64 encoder = new Base64();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            synchronized (this){
               file = new File(path);
            }
            output = Thumbnails.of(file).size(200,200).asBufferedImage();
            ImageIO.write(output, "PNG", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoder.encodeAsString(baos.toByteArray());
    }


    //self define width and height
    public static String resizeImageTo40K(String base64Img) {
        try{
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64Img);
            InputStream   stream = new ByteArrayInputStream(bytes1);
            BufferedImage src = ImageIO.read(stream);
            BufferedImage output = Thumbnails.of(src).size(src.getWidth() / 3, src.getHeight() / 3).asBufferedImage();
            String base64 = imageToBase64(output);
            if (base64.length() - base64.length() / 8 * 2 > 40000) {
                output = Thumbnails.of(output).scale(1 / (base64.length() / 40000)).asBufferedImage();
                base64 = imageToBase64(output);
            }
            return base64;
        } catch (Exception e) {
            return base64Img;
        }
    }

    public static String imageToBase64(BufferedImage bufferedImage) {
        Base64 encoder = new Base64();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "PNG", baos);
        } catch (IOException e) {
        }
        return new String(encoder.encode((baos.toByteArray())));
    }


}
