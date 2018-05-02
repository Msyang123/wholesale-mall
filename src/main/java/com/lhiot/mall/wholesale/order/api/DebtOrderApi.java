package com.lhiot.mall.wholesale.order.api;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.DebtOrderResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.DebtOrderGridParam;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Api(description ="账款订单接口")
@Slf4j
@RestController
@RequestMapping("/debtorder")
public class DebtOrderApi {

    private final DebtOrderService debtOrderService;

    @Autowired
    public DebtOrderApi(DebtOrderService debtOrderService) {
        this.debtOrderService = debtOrderService;
    }



    @GetMapping("/{debtorderCode}")
    @ApiOperation(value = "根据订单编号查询订单详情")
    public ResponseEntity queryOrderByCode(@PathVariable("debtorderCode") String debtorderCode){
        DebtOrder debtOrder = debtOrderService.findByCode(debtorderCode);
        if (Objects.isNull(debtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }
        return ResponseEntity.ok(debtOrder);
    }

    @PostMapping("/grid")
    @ApiOperation(value = "后台管理-分页查询账款订单信息", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) DebtOrderGridParam param) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return ResponseEntity.ok(debtOrderService.pageQuery(param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "后台管理-账款订单详情页面",response = DebtOrderResult.class)
    public  ResponseEntity<DebtOrderResult> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(debtOrderService.detail(id));
    }
}
