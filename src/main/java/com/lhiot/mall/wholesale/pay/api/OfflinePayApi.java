package com.lhiot.mall.wholesale.pay.api;

import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.hdsend.Inventory;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Api(description = "账款订单线下支付接口")
@RestController
@RequestMapping("/offline")
public class OfflinePayApi {

    private final OrderService orderService;
    private final DebtOrderService debtOrderService;
    private final Warehouse warehouse;

	@Autowired
	public OfflinePayApi(OrderService orderService, DebtOrderService debtOrderService, Warehouse warehouse){
        this.orderService = orderService;

        this.debtOrderService=debtOrderService;
        this.warehouse = warehouse;
    }

    @PutMapping("/confirm/{orderCode}")
    @ApiOperation(value = "依据订单编码修改订单为线下支付方式并且发货")
    public ResponseEntity<OrderDetail> confirm(@PathVariable("orderCode") String orderCode, @RequestBody OrderDetail orderDetail){
        //线下支付
        if(Objects.equals(orderDetail.getSettlementType(),"cod")) {
            orderDetail.setOrderStatus("undelivery");//待收货
            //FIXME 直接发送总仓
            Inventory inventory = new Inventory();
            inventory.setUuid(UUID.randomUUID().toString());
            inventory.setSenderCode("9646");
            inventory.setSenderWrh("07310101");
            inventory.setReceiverCode(null);
            inventory.setContactor("老曹");
            inventory.setPhoneNumber("18888888888");
            inventory.setDeliverAddress("五一大道98号");
            inventory.setRemark("快点送");
            inventory.setOcrDate(new Date());
            inventory.setFiller("填单人");
            inventory.setSeller("销售员");
            inventory.setSouceOrderCls("批发商城");
            inventory.setNegInvFlag("1");
            inventory.setMemberCode(null);
            inventory.setFreight(new BigDecimal(21.3));

            //清单
            List<Inventory.WholeSaleDtl> wholeSaleDtlList = new ArrayList<>();
            Inventory.WholeSaleDtl wholeSaleDtl1 = inventory.new WholeSaleDtl();
            wholeSaleDtl1.setSkuId("010100100011");
            wholeSaleDtl1.setQty(new BigDecimal(3));
            wholeSaleDtl1.setPrice(new BigDecimal(100.1));
            wholeSaleDtl1.setTotal(null);
            wholeSaleDtl1.setFreight(null);
            wholeSaleDtl1.setPayAmount(new BigDecimal((99.1)));
            wholeSaleDtl1.setUnitPrice(null);
            wholeSaleDtl1.setPriceAmount(new BigDecimal(200.1));
            wholeSaleDtl1.setBuyAmount(new BigDecimal(99.2));
            wholeSaleDtl1.setBusinessDiscount(new BigDecimal(0.1));
            wholeSaleDtl1.setPlatformDiscount(new BigDecimal(0));
            wholeSaleDtl1.setQpc(new BigDecimal(5));
            wholeSaleDtl1.setQpcStr("1*5");

            wholeSaleDtlList.add(wholeSaleDtl1);
            inventory.setProducts(wholeSaleDtlList);

            List<Inventory.Pay> pays = new ArrayList<>();
            Inventory.Pay pay = inventory.new Pay();

            pay.setTotal(new BigDecimal(234.56));
            pay.setPayName("线下支付");
            pays.add(pay);
            inventory.setPays(pays);

            String hdCode = warehouse.savenew2state(inventory);
            log.info(hdCode);

            //修改订单状态为已支付状态
            orderDetail.setHdCode(hdCode);//总仓编码
            orderDetail.setHdStatus("success");//海鼎发送成功
            orderDetail.setOrderStatus("undelivery");//待发货状态
        }else{
            orderDetail.setCode(-1001);
            orderDetail.setMsg("非货到付款订单");
            return ResponseEntity.ok(orderDetail);
        }
        orderDetail.setOrderCode(orderCode);
        int result=orderService.updateOrder(orderDetail);
        if(result>0){
            orderDetail.setCode(1001);
            orderDetail.setMsg("支付成功");
        }else{
            orderDetail.setCode(-1001);
            orderDetail.setMsg("支付失败");
        }
        return ResponseEntity.ok(orderDetail);
    }

    @PostMapping("/debtorderpay")
    @ApiOperation(value = "线下支付账款订单提交审核", response = String.class)
    public ResponseEntity debtorderPay(@RequestBody DebtOrder debtOrder){

        //依据欠款订单业务编码查询欠款订单信息
        DebtOrder searchDebtOrder= debtOrderService.findByCode(debtOrder.getOrderDebtCode());
        //审核状态：unpaid-未支付 failed-已失效 paid-已支付 unaudited-未审核 agree-审核通过 reject-审核不通过
        if(Objects.isNull(searchDebtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }else if(Objects.equals(searchDebtOrder.getCheckStatus(),"unaudited")){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(Objects.equals(searchDebtOrder.getCheckStatus(),"agree")||Objects.equals(searchDebtOrder.getCheckStatus(),"paid")){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }
        //提交账款订单审核
        debtOrder.setCheckStatus("unaudited");
        int result=debtOrderService.updateDebtOrderByCode(debtOrder);
        if(result>0){
            //修改账款订单状态
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("线下支付账款订单提交审核失败");
    }


}
