package com.lhiot.mall.wholesale.user.api;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.lhiot.mall.wholesale.base.DateFormatUtil;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.base.QRCodeUtil;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.domain.UserGridParam;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.AccessToken;
import com.lhiot.mall.wholesale.user.wechat.JsapiPaySign;
import com.lhiot.mall.wholesale.user.wechat.JsapiTicket;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.Token;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(description ="用户接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    private final UserService userService;

    private final WeChatUtil weChatUtil;

    private final SalesUserService salesUserService;

    private final RestTemplate restTemplate;

    private final RedissonClient redissonClient;


    @Autowired
    public UserApi(UserService userService, SalesUserService salesUserService,
                   PaymentProperties properties, RestTemplate restTemplate,
                   RedissonClient redissonClient) {
        this.userService = userService;
        this.salesUserService = salesUserService;
        this.weChatUtil = new WeChatUtil(properties);
        this.restTemplate=restTemplate;
        this.redissonClient = redissonClient;
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

    /*@PostMapping("/search")
    @ApiOperation(value = "新建一个查询，用于返回用户列表", response = User.class, responseContainer = "List")
    public ResponseEntity<List<User>> search(@RequestBody(required = false) SearchUser param) {
        return ResponseEntity.ok(userService.users(param.getLikeName()));
    }*/
    /***********************************************微信授权登录***********************************************/

    //第一步 userType 0商户 1业务员
    @GetMapping("/wechat/login")
    @ApiOperation(value = "微信oauth鉴权登录 获取authorize url用于前端跳转", response = String.class)
    public ResponseEntity<String> weixinLogin(@RequestParam("uri") String uri,@RequestParam("userType") String userType) throws UnsupportedEncodingException {
        String requestUrl = MessageFormat.format(weChatUtil.OAUTH2_URL, weChatUtil.getProperties().getWeChatOauth().getAppId(),
                URLEncoder.encode(weChatUtil.getProperties().getWeChatOauth().getAppRedirectUri(),"UTF-8"),"snsapi_userinfo",uri+"|"+userType);
        return ResponseEntity.ok(requestUrl);
    }
    //通过code换取网页授权access_token
    @GetMapping("/wechat/authorize")
    @ApiOperation(value = "微信oauth鉴权登录 authorize back之后处理业务", response = String.class)
    public void weixinAuthorize(HttpServletRequest request,HttpServletResponse response) throws IOException{
        Map<String,String> resultMap= paramsToMap(request);
        String code=resultMap.get("code");
        String state=resultMap.get("state");
        String[] states=state.split("\\|");
        String clientUri=states[0];
        String userType=states[1];
        log.info("state:"+state);
        AccessToken accessToken=weChatUtil.getAccessTokenByCode(weChatUtil.getProperties().getWeChatOauth().getAppId(),weChatUtil.getProperties().getWeChatOauth().getAppSecret(),code);
        log.info("weixinAuthorize:"+accessToken);
        //判断是否在数据库中存在此记录，如果存在直接登录，否则就注册用户微信信息


        RMapCache<String,String> cache=  redissonClient.getMapCache("userToken");
        //将access_token(2小时) 缓存起来
        cache.put("accessToken"+accessToken.getOpenId(),accessToken.getAccessToken(),2, TimeUnit.HOURS);
        //redis缓存 refresh_token一个月
        cache.put("refreshToken"+accessToken.getOpenId(),accessToken.getRefreshToken(),30, TimeUnit.DAYS);

        //如果用户存在就不做处理 否则插入数据库
        if(clientUri.indexOf("?")!=-1){
            clientUri=accessToken.getOpenId()+"?userType="+userType+"&clientUri="+clientUri;
        }else{
            clientUri=accessToken.getOpenId()+"?userType="+userType+"&clientUri="+clientUri;
        }
        if(Objects.equals("0",userType)){
            //商户
            User searchUser= userService.searchUserByOpenid(accessToken.getOpenId());
            if(searchUser==null){
                //写入数据库中
                String weixinUserInfo=weChatUtil.getOauth2UserInfo(accessToken.getOpenId(),accessToken.getAccessToken());
                User user=userService.convert(weixinUserInfo);
                userService.save(user);
            }
            log.info("商户sendRedirect:"+weChatUtil.getProperties().getWeChatOauth().getAppFrontUri()+clientUri);
            response.sendRedirect(weChatUtil.getProperties().getWeChatOauth().getAppFrontUri()+clientUri);
            return;
        }else{
            //业务员
            /*SalesUser salesUser= salesUserService.searchSalesUserByOpenid(accessToken.getOpenId());
            if(Objects.isNull(salesUser)){
                //写入数据库中
                String weixinUserInfo=weChatUtil.getOauth2UserInfo(accessToken.getOpenId(),accessToken.getAccessToken());
                //转换微信用户信息
                User user=userService.convert(weixinUserInfo);
                salesUser=new SalesUser();
                salesUser.setOpenId(user.getOpenid());
                salesUser.setSalesmanHeadImage(user.getProfilePhoto());
                salesUser.setCreateAt(new Timestamp(System.currentTimeMillis()));
                salesUser.setSalesmanName(user.getNickname());
                salesUserService.create(salesUser);
            }*/
            log.info("业务员sendRedirect:"+weChatUtil.getProperties().getWeChatOauth().getAppFrontUri()+clientUri);
            response.sendRedirect(weChatUtil.getProperties().getWeChatOauth().getAppFrontUri()+clientUri);
            return;
        }
    }

    @GetMapping("/wechat/detail/{openid}")
    @ApiOperation(value = "微信 通过openId获取商户详细信息", response = User.class)
    public ResponseEntity detail(@PathVariable("openid") String openid ) throws IOException {
        RMapCache<String,String> cache=  redissonClient.getMapCache("userToken");
        //获取access_token(2小时) 缓存
        String accessToken=cache.get("accessToken"+openid);

        //如果不存在说明redis缓存时间已经到达，需要通过调用weChatUtil.refreshAccessToken()获取
        if(StringUtils.isEmpty(accessToken)){
            //获取redis缓存 refresh_token
            String refreshToken=cache.get("refreshToken"+openid);
            if(StringUtils.isEmpty(refreshToken)){
                //TODO 需要和前端协商处理此问题 可以考虑前端缓存超时时间
                return ResponseEntity.badRequest().body("refreshToken失效 需要重新授权 请求/wechat/login获取调整链接");
            }
            //重新刷新accessToken 并放入redis 缓存中
            AccessToken getAccessToken=weChatUtil.refreshAccessToken(refreshToken);
            //将access_token(2小时) 缓存起来
            cache.put("accessToken"+getAccessToken.getOpenId(),getAccessToken.getAccessToken(),2, TimeUnit.HOURS);
            accessToken=getAccessToken.getAccessToken();
        }
        String result=weChatUtil.getOauth2UserInfo(openid,accessToken);
        //将返回结果转换成用户对象
        User wxUser=userService.convert(result);
        //查询数据库相关信息
        User dbUser= userService.searchUserByOpenid(openid);
        wxUser.setRegisterTime(dbUser.getRegisterTime());
        wxUser.setBalance(dbUser.getBalance());
        wxUser.setId(dbUser.getId());
        wxUser.setPhone(dbUser.getPhone());
        wxUser.setUserStatus(dbUser.getUserStatus());
        wxUser.setDebtFee(userService.debtFee(dbUser.getId()));//账款订单金额
        return ResponseEntity.ok(wxUser);
    }
    @GetMapping("/wechat/token")
    @ApiOperation(value = "微信oauth Token 全局缓存的", response = Token.class)
    public ResponseEntity<Token> token() throws IOException {
        RMapCache<String,Token> cache=  redissonClient.getMapCache("token");
        Token token=cache.get("token");
        //先从缓存中获取 如果为空再去微信服务器获取
        if(Objects.isNull(token)){
            token = weChatUtil.getToken();
            cache.put("token",token,2, TimeUnit.HOURS);//缓存2小时
        }
        return ResponseEntity.ok(token);
    }

    @GetMapping("/wechat/jsapi/ticket")
    @ApiOperation(value = "微信oauth jsapiTicket", response = JsapiTicket.class)
    public ResponseEntity<JsapiPaySign> jsapiTicket(@RequestParam("url") String url)  throws IOException {
        RMapCache<String,Object> cache=  redissonClient.getMapCache("token");
        Token token=(Token)cache.get("token");
        //先从缓存中获取 如果为空再去微信服务器获取
        if(Objects.isNull(token)){
            token = weChatUtil.getToken();
            cache.put("token",token,2, TimeUnit.HOURS);//缓存2小时
        }
        //获取缓存中的js ticket (2小时) 缓存
        JsapiTicket ticket=(JsapiTicket)cache.get("ticket");
        if(Objects.isNull(ticket)){
            ticket = weChatUtil.getJsapiTicket(token.getAccessToken());
            cache.put("ticket",ticket,2, TimeUnit.HOURS);//缓存2小时
        }

        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString();
        String decodedUrl = URLDecoder.decode(url, "UTF-8");
        String signature = weChatUtil.getSignature(ticket.getTicket(), timestamp, nonceStr, decodedUrl);
        //构造jsapi返回结果
        JsapiPaySign jsapiPaySign=new JsapiPaySign();
        jsapiPaySign.setAppid(weChatUtil.getProperties().getWeChatOauth().getAppId());
        jsapiPaySign.setNonceStr(nonceStr);
        jsapiPaySign.setTimestamp(timestamp);
        jsapiPaySign.setSignature(signature);
        return ResponseEntity.ok(jsapiPaySign);
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

    @GetMapping("/{userId}/addresses")
    @ApiOperation(value = "我的地址列表", response = UserAddress.class, responseContainer = "List")
    public ResponseEntity<ArrayObject> userAddresses(@PathVariable("userId") @NotNull long userId) {
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
        //手机验证码
        RMapCache<String,String> cache=  redissonClient.getMapCache("userVerificationCode");
        if(Objects.nonNull(cache.get("phone"+phone))){
            return ResponseEntity.badRequest().body("验证码2分钟内有效，请勿重复发送");
        }
        try {
            String randomCode= ""+weChatUtil.buildRandom(6);
            //将手机验证码(2分钟) 缓存起来
            cache.put("phone"+phone,randomCode,2, TimeUnit.MINUTES);
            //发送验证码到第三方推送服务器
            Map<String,String> body=new HashMap<>();
            body.put("number",randomCode);
            String sendMessageUrl=MessageFormat.format(weChatUtil.getProperties().getSendMessageUrl(),"verification",phone);
            restTemplate.postForObject(sendMessageUrl, body, String.class);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("验证码发送失败");
        }

    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "user", value = "注册用户数据", required = true, dataType = "User"),
    })
    public ResponseEntity register(@RequestBody @NotNull User user) {
        //手机验证码
        RMapCache<String,String> cache =  redissonClient.getMapCache("userVerificationCode");
        if(Objects.isNull(cache.get("phone"+user.getPhone()))){
            return ResponseEntity.badRequest().body("验证码已失效，请再次发送验证码");
        }
        try {
            //到远端验证手机验证码是否正确
            String verifiUrl=MessageFormat.format(weChatUtil.getProperties().getValidateMessageUrl(),"verification-wholesale",user.getPhone());
            Map<String,String> body=new HashMap<>();
            body.put("code",user.getCode());
            body.put("key","number");
            String result=restTemplate.postForObject(verifiUrl, body, String.class);
            log.info("verifiCode result:"+result);
            if (!Objects.equals(result,"true")){
                return ResponseEntity.badRequest().body("手机验证码不正确");
            }

            User u = userService.user(user.getId());
            if (Objects.equals(u.getUserStatus(),"unaudited")){
                return ResponseEntity.badRequest().body("您审核申请已提交，不能重复操作");
            }
            if (userService.register(user, user.getCode())) {
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
    
    @PostMapping("/info/gird")
    @ApiOperation(value = "新建一个查询，分页查询", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) UserGridParam param) {
        return ResponseEntity.ok(userService.pageQuery(param));
    }
    
}
