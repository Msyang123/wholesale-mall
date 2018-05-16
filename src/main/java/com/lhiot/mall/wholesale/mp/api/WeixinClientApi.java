package com.lhiot.mall.wholesale.mp.api;

import com.lhiot.mall.wholesale.base.MySecurity;
import com.lhiot.mall.wholesale.mp.api.backmsg.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(description = "微信公众平台对接接口")
@Slf4j
@RestController
public class WeixinClientApi {

    private final RedissonClient redissonClient;

    @Autowired
    public WeixinClientApi(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @GetMapping("/sign")
    @ApiOperation(value = "微信发送的Token验证")
    public String getSign(@RequestParam("signature") String signature,
                                             @RequestParam("timestamp") String timestamp,
                                             @RequestParam("nonce") String nonce,
                                             @RequestParam("echostr") String echostr) {
        // 重写totring方法，得到三个参数的拼接字符串
        List<String> list = new ArrayList<String>(3) {
            private static final long serialVersionUID = 2621444383666420433L;
            public String toString() {
                return this.get(0) + this.get(1) + this.get(2);
            }
        };
        list.add("shuiguoshule");//正式环境要修改下
        list.add(timestamp);
        list.add(nonce);
        Collections.sort(list);// 排序
        String tmpStr = new MySecurity().encode(list.toString(),
                MySecurity.SHA_1);// SHA-1加密

        if (signature.equals(tmpStr)) {
            return echostr;
        }else{
            return null;
        }
    }

    @PostMapping("/sign")
    @ApiOperation(value = "微信发送的信息响应")
    public void postSign(@RequestBody String msg, HttpServletRequest request, HttpServletResponse response) {
       log.info(msg);
        try{
            response.setContentType("text/html; charset=utf-8");
            InputStream is = request.getInputStream();
            OutputStream os = response.getOutputStream();
            final DefaultSession session = DefaultSession.newInstance();

            session.addOnHandleMessageListener(new HandleMessageAdapter() {
                //自动回复消息
                @Override
                public void onTextMsg(Msg4Text msg) {
                    log.info("Msg4Text:",msg);
                }
            });

            // 语音识别消息
            session.addOnHandleMessageListener(new HandleMessageAdapter() {

                public void onVoiceMsg(Msg4Voice msg) {
                    Msg4Text reMsg = new Msg4Text();
                    reMsg.setFromUserName(msg.getToUserName());
                    reMsg.setToUserName(msg.getFromUserName());
                    reMsg.setCreateTime(msg.getCreateTime());
                    reMsg.setContent("识别结果: " + msg.getRecognition());
                    session.callback(reMsg);// 回传消息
                }
            });

            // 处理事件
            session.addOnHandleMessageListener(new HandleMessageAdapter() {
                public void onEventMsg(Msg4Event msg) {

                    //根据当期接收人自定义消息给予回复
                    String eventType = msg.getEvent();
                    if (Msg4Event.SUBSCRIBE.equals(eventType)) {// 订阅
                        //查找当前接收人设置的订阅消息
                        log.info("eventType:",eventType);

                    } else if (Msg4Event.UNSUBSCRIBE.equals(eventType)) {// 取消订阅
                        log.info("取消关注：" + msg.getFromUserName());
                        RMapCache<String,String> cache=  redissonClient.getMapCache("userToken");
                        //将access_token(2小时) 缓存清除掉
                        cache.remove("accessToken"+msg.getFromUserName());
                        //redis缓存 refresh_token一个月 缓存清除掉
                        cache.remove("refreshToken"+msg.getFromUserName());
                        log.info("取消后清除了accessToken：" + cache.get("accessToken"+msg.getFromUserName()));
                        log.info("refreshToken：" + cache.get("refreshToken"+msg.getFromUserName()));
                    } else if (Msg4Event.CLICK.equals(eventType)) {// 点击事件
                        //查找当前接收人
                        log.info("eventType:",eventType);
                    }else if(Msg4Event.SUBSCRIBE.equals(eventType)) {
                        log.info("关注公众号：" + msg.getFromUserName());
                    }
                }
            });

            // 处理地理位置
            session.addOnHandleMessageListener(new HandleMessageAdapter() {
                public void onLocationMsg(Msg4Location msg) {
                    System.out.println("收到地理位置消息：");
                    System.out.println("X:" + msg.getLocation_X());
                    System.out.println("Y:" + msg.getLocation_Y());
                    System.out.println("Scale:" + msg.getScale());
                }

            });

            session.process(is, os);// 处理微信消息
            session.close();// 关闭Session
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
