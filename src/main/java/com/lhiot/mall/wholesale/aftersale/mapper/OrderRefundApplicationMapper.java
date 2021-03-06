package com.lhiot.mall.wholesale.aftersale.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundPage;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundResult;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.domain.SupplementRecords;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;

@Mapper
public interface OrderRefundApplicationMapper {

    Integer create(OrderRefundApplication orderRefundApplication);

    Integer updateById(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> orderRefundApplicationList(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> list(OrderRefundApplication orderRefundApplication);

    OrderRefundApplication refundInfo(OrderRefundApplication orderRefundApplication);

    OrderResult searchOrderById(String orderCode);

    Integer pageQueryCount(OrderGridParam param);

    List<OrderRefundPage> page(OrderGridParam param);
    
    List<OrderRefundApplication> select(String orderCode);

    //根据订单id查询补差额
    List<SupplementRecords> supplements(Map<String,Object> map);
    
    //查询售后订单详情，根据订单id
    OrderRefundResult orderRefundResult(Map<String,Object> map);
    
    List<Map<String, Object>> exportData(OrderGridParam param);

}
