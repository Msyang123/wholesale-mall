package com.lhiot.mall.wholesale.aftersale.service;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.mapper.OrderRefundApplicationMapper;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.mapper.FaqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderRefundApplicationService {

    private final OrderRefundApplicationMapper orderRefundApplicationMapper;

    @Autowired
    public OrderRefundApplicationService(OrderRefundApplicationMapper orderRefundApplicationMapper) {
        this.orderRefundApplicationMapper = orderRefundApplicationMapper;
    }

    public int create(OrderRefundApplication orderRefundApplication){
        return this.orderRefundApplicationMapper.create(orderRefundApplication);
    }

    public int updateById(OrderRefundApplication orderRefundApplication){
        return this.orderRefundApplicationMapper.updateById(orderRefundApplication);
    }

    public List<OrderRefundApplication> orderRefundApplicationList(long userId){
        return this.orderRefundApplicationMapper.orderRefundApplicationList(userId);
    }

}
