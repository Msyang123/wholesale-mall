package com.lhiot.mall.wholesale.mp.api;

import com.lhiot.mall.wholesale.base.MySecurity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Api(description = "微信公众平台对接接口")
@Slf4j
@RestController
public class WeixinClientApi {



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
    public void postSign(@RequestBody String msg) {
       log.info(msg);
    }


}
