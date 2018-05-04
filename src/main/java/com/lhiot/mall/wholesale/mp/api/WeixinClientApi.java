package com.lhiot.mall.wholesale.mp.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.util.ImmutableMap;
import com.lhiot.mall.wholesale.base.MySecurity;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Api(description = "微信公众平台对接接口")
@Slf4j
@RestController
@RequestMapping("/weixin")
public class WeixinClientApi {



    @GetMapping("/sign")
    @ApiOperation(value = "查询业务员信息")
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
    @ApiOperation(value = "门店管理")
    public void postSign(@RequestBody String msg) {
       log.info(msg);
    }


}
