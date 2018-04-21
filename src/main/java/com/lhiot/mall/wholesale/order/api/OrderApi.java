package com.lhiot.mall.wholesale.order.api;

import com.leon.microx.common.wrapper.TipsObject;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.order.vo.Order;
import com.lhiot.mall.wholesale.order.vo.SearchOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Created by HuFan on 2018/4/21.
 */
@Api
@Slf4j
@RestController
public class OrderApi {

    private final OrderService orderService;

    @Autowired
    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    @ApiOperation(value = "添加新品需求", response = Order.class)
    public ResponseEntity add(@RequestBody Order order) {
        if (orderService.save(order)) {
            return ResponseEntity.created(URI.create("/demandgoods/" + order.getId())).body(order);
        }
        return ResponseEntity.badRequest().body(TipsObject.of("添加失败"));
    }

    @PutMapping("/order/{id}")
    @ApiOperation(value = "根据ID修改新品需求信息", response = Order.class)
    public ResponseEntity modify(@PathVariable("id") Long id, @RequestBody Order order) {
        order.setId(id);
        if (orderService.save(order)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().body(TipsObject.of("修改失败"));
    }

    @DeleteMapping("/order/{id}")
    @ApiOperation(value = "根据ID删除一个新品需求")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{id}")
    @ApiOperation(value = "根据ID查询一个新品需求信息", response = Order.class)
    public ResponseEntity<Order> demandGoods(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.order(id));
    }

    @PostMapping("/order/search")
    @ApiOperation(value = "新建一个查询，用于返回新品需求列表", response = Order.class, responseContainer = "List")
    public ResponseEntity<List<Order>> search(@RequestBody(required = false) SearchOrder param) {
        return ResponseEntity.ok(orderService.order(param));
    }
}
