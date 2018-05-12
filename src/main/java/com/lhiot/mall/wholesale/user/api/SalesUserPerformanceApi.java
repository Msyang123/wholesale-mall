package com.lhiot.mall.wholesale.user.api;
import java.util.HashMap;
import java.util.Map;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
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
            @RequestParam(value="rows", required = false, defaultValue="10") Integer rows,
            @RequestParam(value="page", required = false, defaultValue="1") Integer page,
            @RequestParam(value="sidx", required = false, defaultValue="") String sidx,
            @RequestParam(value="sord", required = false, defaultValue="") String sord){
        OrderGridParam param = new OrderGridParam();
        param.setPage(page);
        param.setSord(sord);
        param.setRows(rows);
        param.setSidx(sidx);
        PageQueryObject pageQueryObject = salesUserPerformanceService.pagePerformanceDetail(param);
        return ResponseEntity.ok(pageQueryObject);
    }
}
