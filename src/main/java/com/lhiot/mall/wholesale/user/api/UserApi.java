package com.lhiot.mall.wholesale.user.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.base.QRCodeUtil;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.SearchUser;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description ="用户接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    private final UserService userService;

    private final WeChatUtil weChatUtil;

    private final SalesUserService salesUserService;

    private final RestTemplate restTemplate;

    @Autowired
    public UserApi(UserService userService,SalesUserService salesUserService,PaymentProperties properties,RestTemplate restTemplate) {
        this.userService = userService;
        this.salesUserService = salesUserService;
        this.weChatUtil = new WeChatUtil(properties);
        this.restTemplate=restTemplate;
    }

    /*@PostMapping("/user")
    @ApiOperation(value = "添加用户", response = User.class)
    public ResponseEntity add(@RequestBody User user) {
        if (userService.save(user)) {
            return ResponseEntity.created(URI.create("/user/" + user.getId())).body(user);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "根据ID修改用户信息", response = User.class)
    public ResponseEntity modify(@PathVariable("id") Long id, @RequestBody User user) {
        user.setId(id);
        if (userService.save(user)) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据ID删除一个用户")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }*/

    @GetMapping("query/{id}")
    @ApiOperation(value = "根据ID查询一个用户信息", response = User.class)
    public ResponseEntity<User> user(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.user(id));
    }

    @PostMapping("/search")
    @ApiOperation(value = "新建一个查询，用于返回用户列表", response = User.class, responseContainer = "List")
    public ResponseEntity<List<User>> search(@RequestBody(required = false) SearchUser param) {
        return ResponseEntity.ok(userService.users(param.getLikeName()));
    }
    /***********************************************微信授权登录***********************************************/

    //第一步
    @GetMapping("/weixin/login")
    @ApiOperation(value = "微信oauth鉴权登录 获取authorize url用于前端跳转", response = String.class)
    public ResponseEntity<String> weixinLogin(@RequestParam("uri") String uri) throws UnsupportedEncodingException {
        String requestUrl = MessageFormat.format(weChatUtil.OAUTH2_URL, weChatUtil.getProperties().getWeChatOauth().getAppId(),
                URLEncoder.encode(weChatUtil.getProperties().getWeChatOauth().getAppRedirectUri(),"UTF-8"),"snsapi_userinfo",uri);
        return ResponseEntity.ok(requestUrl);
    }
    private static Map<String,AccessToken> wxSignleUserMap=new HashMap<String,AccessToken>();
    private static Map<String,String> wxRefreshTokenMap=new HashMap<String,String>();
    //通过code换取网页授权access_token
    @GetMapping("/weixin/authorize")
    @ApiOperation(value = "微信oauth鉴权登录 authorize back之后处理业务", response = String.class)
    public void weixinAuthorize(HttpServletRequest request,HttpServletResponse response) throws IOException{
        Map<String,String> resultMap= paramsToMap(request);
        String code=resultMap.get("code");
        String clientUri=resultMap.get("state");
        log.info("clientUri:"+clientUri);
        AccessToken accessToken=weChatUtil.getAccessTokenByCode(weChatUtil.getProperties().getWeChatOauth().getAppId(),weChatUtil.getProperties().getWeChatOauth().getAppSecret(),code);
        log.info("weixinAuthorize:"+accessToken);
        //判断是否在数据库中存在此记录，如果存在直接登录，否则就注册用户微信信息
        //需要将access_token(2小时) 和 refresh_token （1个月）做为session缓存起来

        //以加密openId做为redis key
        wxSignleUserMap.put(accessToken.getOpenId(),accessToken);
        //FIXME redis缓存 refresh_token一个月
        wxRefreshTokenMap.put(accessToken.getOpenId(),accessToken.getRefreshToken());
        //如果用户存在就不做处理 否则插入数据库
        if(clientUri.indexOf("?")!=-1){
            clientUri=clientUri+"&openid="+accessToken.getOpenId();
        }else{
            clientUri=clientUri+"?openid="+accessToken.getOpenId();
        }
        List<User> users= userService.users(accessToken.getOpenId());
        if(users.size()>0){
            //检查手机号等相关信息用于判断是否需要设置手机号等
            User findUser= users.get(0);
            //return ResponseEntity.ok("{'openid':"+accessToken.getOpenId()+"}");
            response.sendRedirect(weChatUtil.getProperties().getWeChatOauth().getClientRedirectUrl()+clientUri);
            return;
        }
        //写入数据库中
        String weixinUserInfo=weChatUtil.getOauth2UserInfo(accessToken.getOpenId(),accessToken.getAccessToken());
        User user=userService.convert(weixinUserInfo);
        userService.save(user);
        response.sendRedirect(weChatUtil.getProperties().getWeChatOauth().getClientRedirectUrl()+clientUri);
        return;
    }

    @GetMapping("/weixin/detial")
    @ApiOperation(value = "微信 通过openId获取用户详细信息", response = User.class)
    public ResponseEntity getWxUserDetial(@PathVariable String openIdAfterMd5 ) throws IOException {
        AccessToken accessToken=wxSignleUserMap.get(openIdAfterMd5);
        //FIXME 如果不存在说明redis缓存时间已经到达，需要通过调用weChatUtil.refreshAccessToken()获取
        if(accessToken==null){
            String refreshToken=String.valueOf(wxRefreshTokenMap.get(openIdAfterMd5));
            if(StringUtils.isEmpty(refreshToken)){
                return ResponseEntity.badRequest().body("refreshToken失效 需要重新授权 请求/weixin/login获取调整链接");
            }
            //重新刷新accessToken 并放入redis 缓存中
            accessToken=weChatUtil.refreshAccessToken(refreshToken);
            wxSignleUserMap.put(openIdAfterMd5,accessToken);
        }
        String result=weChatUtil.getOauth2UserInfo(accessToken.getOpenId(),accessToken.getAccessToken());
        //将返回结果转换成用户对象
        return ResponseEntity.ok(userService.convert(result));
    }
    @GetMapping("/weixin/token")
    @ApiOperation(value = "微信oauth Token", response = Token.class)
    public ResponseEntity<Token> token() throws IOException {
        Token token = weChatUtil.getToken();
        return ResponseEntity.ok(token);
    }

    @GetMapping("/weixin/jsapi/ticket")
    @ApiOperation(value = "微信oauth jsapiTicket", response = JsapiTicket.class)
    public ResponseEntity<JsapiTicket> jsapiTicket()  throws IOException {
        Token token = weChatUtil.getToken();
        JsapiTicket ticket = weChatUtil.getJsapiTicket(token.getAccessToken());
        return ResponseEntity.ok(ticket);
    }
    private Map<String, String> paramsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((name, values) -> {
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                if (i == values.length - 1) {
                    valueStr = valueStr + values[i];
                } else {
                    valueStr = valueStr + values[i] + ",";
                }
            }
            params.put(name, valueStr);
        });
        return params;
    }



    @GetMapping("/{userId}")
    @ApiOperation("查询个人信息")
    public ResponseEntity<User> queryUser(@PathVariable("userId") long uid) {
        return ResponseEntity.ok(userService.user(uid)); // 查询操作幂等
    }

    @PutMapping("modify/{userId}")   //因为要从URL能看出 指定修改哪个用户，所以ID应该放path中
    @ApiOperation("修改个人信息")
    public ResponseEntity updateUser(@PathVariable("userId") long userId, @RequestBody User user) {
        user.setId(userId);
        userService.updateUser(user);   // 修改操作幂等
        return ResponseEntity.ok("修改成功");
    }

    @DeleteMapping("/address/remove/{addressId}")
    @ApiOperation("根据ID删除地址")
    public ResponseEntity deleteAddress(@PathVariable("addressId") @NotNull long addressId) {
        userService.deleteAddress(addressId);   // 删除操作幂等
        return ResponseEntity.ok("删除成功");
    }

    @GetMapping("/{userId}/addresses1")
    @ApiOperation(value = "我的地址列表11111", response = UserAddress.class, responseContainer = "List")
    public ResponseEntity<ArrayObject> userAddresses(@PathVariable("userId") @NotNull long userId) {
        List<UserAddress> addresses = userService.searchAddressList(userId);
        List<UserAddress> list = userService.searchAddressList(userId);
        for (UserAddress userAddress:addresses) {
            if ("yes".equals(userAddress.getIsDefault())){
                list.add(0,userAddress);
            }else{
                list.add(userAddress);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(addresses));
    }

    @GetMapping("/{userId}/addresses")
    @ApiOperation(value = "我的地址列表", response = UserAddress.class, responseContainer = "List")
    public ResponseEntity<ArrayObject> userAddresses1(@PathVariable("userId") @NotNull long userId) {
        //List<UserAddress> addresses = userService.searchAddressList(userId);
        UserAddress userAddressYes = userService.searchAddressListYes(userId);
        List<UserAddress> userAddressNo = userService.searchAddressListNO(userId);
        List<UserAddress> list = new ArrayList<UserAddress>();
            list.add(0,userAddressYes);
        for (UserAddress userAddress:userAddressNo) {
            list.add(userAddress);
        }
        return ResponseEntity.ok(ArrayObject.of(list));
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation("根据ID查询地址详情")
    public ResponseEntity<UserAddress> userAddress(@PathVariable("addressId") @NotNull long addressId) {
        return ResponseEntity.ok(userService.userAddress(addressId));
    }

    @PostMapping("/address")
    @ApiOperation("新增/修改地址")
    public ResponseEntity saveAddress(@RequestBody UserAddress userAddress) {
        List<UserAddress> addresses = userService.searchAddressList(userAddress.getUserId());
        if (CollectionUtils.isEmpty(addresses)) {
            userAddress.setIsDefault("yes");//如果只有第一条数据则为默认地址
        }
        if (userService.saveOrUpdateAddress(userAddress)) {
            return ResponseEntity.ok(userAddress);
        }
        return ResponseEntity.badRequest().body("添加失败");
    }

    @PostMapping("/set-default")
    @ApiOperation("设置默认接口")
    public ResponseEntity setDefault(@RequestBody UserAddress userAddress) {
        if (userService.updateDefault(userAddress)) {
            return ResponseEntity.ok(userAddress);
        }
        return ResponseEntity.badRequest().body("无效数据");
    }

    @GetMapping("verificationCode/{phone}")
    @ApiOperation("依据手机号发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "phone", value = "手机号", required = true, dataType = "String")
    })
    public ResponseEntity verificationCode(@PathVariable("phone") String phone){
        //发送验证码到第三方推送服务器
        String sendMessageUrl=MessageFormat.format(weChatUtil.getProperties().getSendMessageUrl(),phone,"sigup");
        restTemplate.postForObject(sendMessageUrl,null,Map.class);
        return ResponseEntity.ok(phone);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "user", value = "注册用户数据", required = true, dataType = "User"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "业务员邀请码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "verifCode", value = "手机验证码", required = true, dataType = "String")
    })
    public ResponseEntity register(@RequestBody @NotNull User user, @RequestParam("code") String code,@RequestParam("verifCode") String verifCode) {

        try {
            String redisKey=user.getPhone() + ":user:register";
            //TODO 需要通过redis客户端获取验证码code 然后比较传递的code与redis中存储的是否一致

            if (userService.register(user, code)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("用户注册失败");
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/seller/{sellerId}/approved")
    @ApiOperation("查询是否是审核通过的商户")
    public ResponseEntity<SalesUserRelation> sellerApproved(@PathVariable("sellerId") @NotNull long sellerId) {
        return ResponseEntity.ok(salesUserService.isSeller(sellerId));
    }


    @GetMapping("/balance/{userId}")
    @ApiOperation(value = "余额收支明细")
    public ResponseEntity<ArrayObject> getBalanceRecord(@PathVariable("userId") Integer userId) {
        List<PaymentLog> paymentLogList = userService.getBalanceRecord(userId);//待测
        return ResponseEntity.ok(ArrayObject.of(paymentLogList));
    }

    @GetMapping("/my/{userId}")
    @ApiOperation(value = "我的页面用户数据接口")
    public ResponseEntity<User> myData(@PathVariable("userId") Integer userId) {
        //List<PaymentLog> paymentLogList = userService.getBalanceRecord(userId);//待测
        User user = userService.user(userId);
        user.setDebtFee(userService.debtFee(userId));
        return ResponseEntity.ok(user);
    }

    @ApiOperation("获取二维码图片")
    @GetMapping("/qrcode/{code}")
    public void getCaptcha(@PathVariable("code") @NotNull String code,
                           HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

            try {
                int qrcodeWidth = 300;
                int qrcodeHeight = 300;
                String qrcodeFormat = "png";
                HashMap<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                BitMatrix bitMatrix = new MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE, qrcodeWidth, qrcodeHeight, hints);

                BufferedImage image = QRCodeUtil.toBufferedImage(bitMatrix);
                image.flush();
                ImageIO.write(image, qrcodeFormat, response.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

}
