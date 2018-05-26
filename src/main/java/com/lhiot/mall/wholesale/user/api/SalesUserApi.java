package com.lhiot.mall.wholesale.user.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.coupon.service.CouponConfigService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.sgsl.util.ImmutableMap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(description = "业务员接口")
@Slf4j
@RestController
@RequestMapping("/sales")
public class SalesUserApi {

    private final SalesUserService salesUserService;

    private final UserService userService;

    private final OrderService orderService;

    private final CouponConfigService couponConfigService;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    public SalesUserApi(SalesUserService salesUserService, UserService userService, OrderService orderService, CouponConfigService couponConfigService) {
        this.salesUserService = salesUserService;
        this.userService = userService;
        this.orderService = orderService;
        this.couponConfigService = couponConfigService;
    }

    @GetMapping("/{id}/search/{approved}/sellers")
    @ApiOperation(value = "根据业务员ID查询是否审核通过或者未审核的商户")
    public ResponseEntity<ArrayObject> isCheck(@PathVariable @NotNull Integer id, @PathVariable String approved, @RequestParam Integer page, @RequestParam Integer rows) {
        Map<String, Object> param = ImmutableMap.of("id", id, "check", approved, "start", (page - 1) * rows, "rows", rows);
        List<SalesUserRelation> salesUserRelations = salesUserService.selectRelation(param);
        if (salesUserRelations.isEmpty()) {
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<>()));
        }
        List<Object> ids = new ArrayList<Object>();
        for (SalesUserRelation salesUserRelation : salesUserRelations) {
            ids.add(salesUserRelation.getUserId());
        }
        List<User> users = userService.search(ids);
        List<User> sellers = new ArrayList<User>();
        for (User user :users){
            for (SalesUserRelation salesUserRelation:salesUserRelations) {
                if (Objects.equals(user.getId(),salesUserRelation.getUserId())){
                    user.setAuditStatus(salesUserRelation.getAuditStatus());
                    sellers.add(user);
                }
            }
        }
        return ResponseEntity.ok(ArrayObject.of(sellers));
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "查询业务员信息")
    public ResponseEntity<SalesUser> salesUser(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(salesUserService.searchSalesUser(id));
    }

    @GetMapping("/{salesId}/shopInfo")
    @ApiOperation(value = "门店管理")
    public ResponseEntity<ArrayObject> shopInfo(@PathVariable("salesId") Long salesId, @RequestParam Integer dayNum) {
        List<ShopResult> shopResults = salesUserService.searchShopInfo(salesId);//查询门店基本信息
        if(shopResults.isEmpty()){
            ResponseEntity.ok(ArrayObject.of(new ArrayList<ShopResult>()));
        }
        for (ShopResult result:shopResults){
            System.out.println(result.getUserId());
            OrderDetail orderDetail = orderService.lateOneOrder(result.getUserId());//最近一单消费记录
            OrderParam param = new OrderParam();//传参对象
            if (orderDetail!=null){
                param.setId(result.getUserId());
                result.setLateOrdersFee(orderDetail.getPayableFee());//最近一单的消费金额
                result.setLateTime(orderDetail.getCreateTime());//最近一单的下单时间
                List<OrderDetail> orderDetailList = orderService.lateOrders(param);
                if (orderDetailList.isEmpty()){
                    result.setOrdersTotalFee(0);//最近下单总金额
                    result.setOrderTotal(0);//订单数
                }else{
                    result.setOrdersTotalFee(orderService.lateOrdersFee(param));//最近下单总金额
                    result.setOrderTotal(orderDetailList.size());//订单数
                }
            }else{
                result.setLateOrdersFee(0);//最近一单的消费金额
                result.setLateTime(null);//最近一单的下单时间
                result.setOrdersTotalFee(0);//最近下单总金额
                result.setOrderTotal(0);//订单数
            }
        }

        List<ShopResult> resultList = new ArrayList<ShopResult>();//创建一个未下单的容器
        if (shopResults.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<ShopResult>()));
        }
        if (dayNum==0){
            //全部门店
            return ResponseEntity.ok(ArrayObject.of(shopResults));
        }

        OrderParam param = new OrderParam();
        for (ShopResult shopResult:shopResults) {
            param.setId(shopResult.getUserId());
            param.setDayNum(dayNum);
            OrderDetail order = orderService.userOrder(param);
            if (order==null){
                resultList.add(shopResult);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(resultList));
    }

    @GetMapping("/detial/{openid}")
    @ApiOperation(value = "依据openid查询业务员详细信息")
    public ResponseEntity<SalesUser> detial(@RequestParam("openid") String openid) {
        return ResponseEntity.ok(salesUserService.searchSalesUserByOpenid(openid));
    }

    @PostMapping("/login")
    @ApiOperation(value = "业务员账号登陆接口")
    public ResponseEntity salesLogin(@RequestParam String account,@RequestParam String psw){
        SalesUser salesUser=salesUserService.login(account);
        if (salesUser==null){
            return ResponseEntity.badRequest().body("账号不存在");
        }
        //判断业务员账号是否已经禁用
        String salesStatus = salesUser.getSalesStatus();
        if(!"normal".equals(salesStatus)){
        	return ResponseEntity.badRequest().body("账号被禁用");
        }
        if (psw.equals(salesUser.getSalesmanPassword())){
            return ResponseEntity.ok().body(salesUser);
        }
        return ResponseEntity.badRequest().body("密码错误");
    }

    @GetMapping("/salesman/{salesId}")
    @ApiOperation(value = "业务员主页信息")
    public ResponseEntity<SalesUser> salesman(@PathVariable long salesId){
        SalesUser salesUser = salesUserService.findById(salesId);
        if (salesUser==null){
            return ResponseEntity.ok(new SalesUser());
        }
        return ResponseEntity.ok(salesUser);
    }
    @PostMapping("/check/{salesId}/{userId}")
    @ApiOperation(value = "业务员门店审核")
    public ResponseEntity check(@PathVariable("salesId")Long salesId,@PathVariable("userId") Long userId,@RequestParam String checkStatus){
       SalesUserRelation salesUserRelation = new SalesUserRelation();
       salesUserRelation.setAuditStatus(checkStatus);
       salesUserRelation.setUserId(userId);
       salesUserRelation.setSalesmanId(salesId);
        if (salesUserService.userCheck(salesUserRelation)){//关系表改状态
            return ResponseEntity.ok("操作成功");
        }else{
            return ResponseEntity.ok("操作失败");
        }
    }


    /***************************************后台管理系统接口***********************************************/
    @PostMapping("/create")
    @ApiOperation(value = "业务员信息表创建")
    public ResponseEntity create(@RequestBody SalesUser salesUser) {
        return ResponseEntity.ok(salesUserService.create(salesUser));
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "依据id更新业务员信息表")
    public ResponseEntity updateById(@PathVariable("id") Long id, @RequestBody SalesUser salesUser) {
        salesUser.setId(id);
        return ResponseEntity.ok(salesUserService.updateById(salesUser));
    }

    @DeleteMapping("/remove/{ids}")
    @ApiOperation(value = "依据id删除业务员信息表")
    public ResponseEntity deleteByIds(@PathVariable("ids") String ids) {
        return ResponseEntity.ok(salesUserService.deleteByIds(ids));
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询业务员信息表列表")
    public ResponseEntity<ArrayObject> list(@RequestBody SalesUser salesuser){
        return ResponseEntity.ok(ArrayObject.of(salesUserService.list(salesuser)));
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询业务员信息表列表")
    public ResponseEntity<PageQueryObject> list(
            @RequestParam(value="salesmanName", required = false) String salesmanName,
            @RequestParam(value="salesmanPhone", required = false) String salesmanPhone,
            @RequestParam(value="rows", required = false, defaultValue="10") Integer rows,
            @RequestParam(value="page", required = false, defaultValue="1") Integer page,
            @RequestParam(value="sidx", required = false, defaultValue="") String sidx,
            @RequestParam(value="sord", required = false, defaultValue="") String sord){
        Map<String,Object> param=new HashMap<>();
        param.put("salesmanName",salesmanName);
        param.put("salesmanPhone",salesmanPhone);
        param.put("page",page);
        param.put("rows",rows);
        param.put("sidx",sidx);
        param.put("sord",sord);
        PageQueryObject result  = salesUserService.page(param);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    @ApiOperation(value = "分页查询业务员信息表列表")
    public ResponseEntity<PageQueryObject> export(
            @RequestParam(value="salesmanName", required = false) String salesmanName,
            @RequestParam(value="salesmanPhone", required = false) String salesmanPhone){
        Map<String,Object> param=new HashMap<>();
        param.put("salesmanName",salesmanName);
        param.put("salesmanPhone",salesmanPhone);
        param.put("page",1);
        param.put("rows",5000);
        PageQueryObject result  = salesUserService.page(param);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find/{id}")
    @ApiOperation(value = "依据id查询业务员信息表详细信息")
    public ResponseEntity<SalesUser> find(@PathVariable("id") Long id){
        return ResponseEntity.ok(salesUserService.findById(id));
    }

    @GetMapping("/find-code/{code}")
    @ApiOperation(value = "依据邀请码判断邀请码是否存在")
    public ResponseEntity<SalesUser> findCode(@PathVariable("code") String code){
        return ResponseEntity.ok(salesUserService.findCode(code));
    }

    @GetMapping("/sales-users")
    @ApiOperation(value = "查询所有业务员")
    public ResponseEntity<List<SalesUser>> findAll(){
        return ResponseEntity.ok(salesUserService.salesUsers());
    }

    @GetMapping("/assginShop")
    @ApiOperation(value = "查询所有业务员")
    public ResponseEntity<List<SalesUser>> assginShop(@RequestParam(value="assginUserId") String assginUserId,
                                                      @RequestParam(value="shopId", required = false) String shopId,
                                                      @RequestParam(value="oldUserId", required = false) String oldUserId){
        salesUserService.assginShop(assginUserId,shopId,oldUserId);
        return ResponseEntity.ok(salesUserService.salesUsers());
    }
}
