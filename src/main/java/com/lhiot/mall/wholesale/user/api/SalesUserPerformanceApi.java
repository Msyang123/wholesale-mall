package com.lhiot.mall.wholesale.user.api;
import java.util.HashMap;
import java.util.Map;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import com.lhiot.mall.wholesale.user.service.SalesUserPerformanceService;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author zhangs on 2018/5/4.
 */
@Api(description = "业务员销售业绩接口")
@Slf4j
@RestController
@RequestMapping("/salesperformance")
public class SalesUserPerformanceApi{
    private final SalesUserService salesUserService;

    private final UserService userService;

    private final OrderService orderService;

    private final SalesUserPerformanceService salesUserPerformanceService;

    @Autowired
    public SalesUserPerformanceApi(SalesUserService salesUserService, UserService userService, OrderService orderService,SalesUserPerformanceService salesUserPerformanceService) {
        this.salesUserService = salesUserService;
        this.userService = userService;
        this.orderService = orderService;
        this.salesUserPerformanceService = salesUserPerformanceService;
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
        param.put("sord",sord);
        param.put("rows",rows);
        param.put("sidx",sidx);
        PageQueryObject pageQueryObject = salesUserPerformanceService.pageQuery(param);
        return ResponseEntity.ok(pageQueryObject);
    }
    @GetMapping("/performanceDetailGrid")
    @ApiOperation(value = "分页查询业务员信息表列表")
    public ResponseEntity<PageQueryObject> performanceDetailGrid(
            @RequestParam(value="salesmanName", required = false) String salesmanName,
            @RequestParam(value="salesmanPhone", required = false) String salesmanPhone,
            @RequestParam(value="salesmanId", required = false) String salesmanId,
            @RequestParam(value="orderStatus", required = false)String orderStatus,
            @RequestParam(value="settementType", required = false)String settementType,
            @RequestParam(value="payStatus", required = false)String payStatus,
            @RequestParam(value="createTimeBegin", required = false)String createTimeBegin,
            @RequestParam(value="createTimeEnd", required = false)String createTimeEnd,
            @RequestParam(value="rows", required = false, defaultValue="10") Integer rows,
            @RequestParam(value="page", required = false, defaultValue="1") Integer page,
            @RequestParam(value="sidx", required = false, defaultValue="") String sidx,
            @RequestParam(value="sord", required = false, defaultValue="") String sord){
        OrderGridParam param = new OrderGridParam();
        param.setOrderStatus(orderStatus);
        param.setSettlementType(settementType);
        param.setCreateTimeBegin(createTimeBegin);
        param.setCreateTimeEnd(createTimeEnd);
        param.setPayStatus(payStatus);
        param.setPage(page);
        param.setSord(sord);
        param.setRows(rows);
        param.setSidx(sidx);
        PageQueryObject pageQueryObject = salesUserPerformanceService.pagePerformanceDetail(param,salesmanName,salesmanPhone,salesmanId);
        return ResponseEntity.ok(pageQueryObject);
    }
    @GetMapping("/performanceShopDetailGrid")
    @ApiOperation(value = "分页查询业务员信息表列表")
    public ResponseEntity<PageQueryObject> performanceShopDetailGrid(
            @RequestParam(value="salesmanName", required = false) String salesmanName,
            @RequestParam(value="salesmanPhone", required = false) String salesmanPhone,
            @RequestParam(value="salesmanId", required = false) String salesmanId,
            @RequestParam(value="rows", required = false, defaultValue="10") Integer rows,
            @RequestParam(value="page", required = false, defaultValue="1") Integer page,
            @RequestParam(value="sidx", required = false, defaultValue="") String sidx,
            @RequestParam(value="sord", required = false, defaultValue="") String sord){
        UserPerformanceGridParam param = new UserPerformanceGridParam();
        param.setPage(page);
        param.setSidx(sidx);
        param.setSord(sord);
        param.setRows(rows);
        PageQueryObject pageQueryObject = salesUserPerformanceService.pagePerformanceShopDetail(param,salesmanName,salesmanPhone,salesmanId);
        return ResponseEntity.ok(pageQueryObject);
    }
    @GetMapping("/export")
    @ApiOperation(value = "分页查询业务员信息表列表")
    public ResponseEntity<PageQueryObject> export(
            @RequestParam(value="salesmanPhone", required = false, defaultValue="") String salesmanPhone,
            @RequestParam(value="salesmanName", required = false, defaultValue="") String salesmanName,
            @RequestParam(value="salesmanId", required = false, defaultValue="") String salesmanId,
            @RequestParam(value="orderStatus", required = false, defaultValue="") String orderStatus,
            @RequestParam(value="settementType", required = false, defaultValue="") String settementType,
            @RequestParam(value="createTimeBegin", required = false, defaultValue="") String createTimeBegin,
            @RequestParam(value="createTimeEnd", required = false, defaultValue="") String createTimeEnd){
        OrderGridParam param = new OrderGridParam();
        param.setOrderStatus(orderStatus);
        param.setSettlementType(settementType);
        param.setCreateTimeBegin(createTimeBegin);
        param.setCreateTimeEnd(createTimeEnd);
        param.setPage(1);
        param.setRows(5000);
        PageQueryObject pageQueryObject = salesUserPerformanceService.pagePerformanceDetail(param,salesmanName,salesmanPhone,salesmanId);
        return ResponseEntity.ok(pageQueryObject);
    }
}
