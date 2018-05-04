package com.lhiot.mall.wholesale.user.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.util.ImmutableMap;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "业务员接口")
@Slf4j
@RestController
@RequestMapping("/sales")
public class SalesUserApi {

    private final SalesUserService salesUserService;

    private final UserService userService;

    private final OrderService orderService;

    @Autowired
    public SalesUserApi(SalesUserService salesUserService, UserService userService, OrderService orderService) {
        this.salesUserService = salesUserService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/{id}/search/{approved}/sellers")
    @ApiOperation(value = "根据业务员ID查询是否审核通过或者未审核的商户")
    public ResponseEntity<List<User>> isCheck(@PathVariable @NotNull Integer id, @PathVariable Integer approved, @RequestParam Integer page, @RequestParam Integer rows) {
        Map<String, Object> param = ImmutableMap.of("id", id, "isCheck", approved, "START", (page - 1) * rows, "ROWS", rows);
        List<SalesUserRelation> salesUserRelations = salesUserService.selectRelation(param);
        if (salesUserRelations.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List ids = new ArrayList();
        for (SalesUserRelation salesUserRelation : salesUserRelations) {
            ids.add(salesUserRelation.getUserId());
        }
        return ResponseEntity.ok(userService.search(ids));
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "查询业务员信息")
    public ResponseEntity<SalesUser> salesUser(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(salesUserService.searchSalesUser(id));
    }

    @GetMapping("/{salesId}/shopInfo")
    @ApiOperation(value = "门店管理")
    public ResponseEntity<ArrayObject> shopInfo(@PathVariable("salesId") Long salesId, @RequestParam Integer dayNum) {
        List<ShopResult> shopResultList = salesUserService.searchShopInfo(salesId);//查询门店基本信息
        if(shopResultList.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<ShopResult>()));
        }
        for (ShopResult result:shopResultList){
            System.out.println(result.getUserId());
            OrderDetail orderDetail = orderService.lateOneOrder(result.getUserId());//最近一单消费记录
            OrderParam param = new OrderParam();//传参对象
            if (orderDetail!=null){
                param.setDayNum(dayNum);
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
            }
        }
        return ResponseEntity.ok(ArrayObject.of(shopResultList));
    }

    /*@GetMapping("/page")
    @ApiOperation(value = "业务员账号登陆接口")
    public ResponseEntity<SalesUser> salesLogin(@RequestParam String acount,@RequestParam String salesmanPassword){

        return ResponseEntity.ok(SalesUser);
    }*/


    /***************************************后台管理系统接口***********************************************/
    @PostMapping("/create")
    @ApiOperation(value = "业务员信息表创建")
    public ResponseEntity create(@RequestBody SalesUser salesUser) {
        return ResponseEntity.ok(salesUserService.create(salesUser));
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "依据id更新业务员信息表")
    public ResponseEntity updateById(@PathVariable("id") Long id,@RequestBody SalesUser salesUser) {
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
    public ResponseEntity<ArrayObject> list(
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
        ArrayObject result=ArrayObject.of(salesUserService.page(param));
        //TODO 此处需要做记录总数查询
        result.setPage(page);
        result.setRows(rows);
        result.setSidx(sidx);
        result.setSord(sord);
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

}
