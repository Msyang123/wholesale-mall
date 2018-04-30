package com.lhiot.mall.wholesale.pay.service;

import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.mapper.PaymentLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class PaymentLogService {

    private final PaymentLogMapper paymentLogMapper;


    @Autowired
    public PaymentLogService(PaymentLogMapper paymentLogMapper){
        this.paymentLogMapper=paymentLogMapper;
    }
    /**
     * 依据订单编码查询支付日志
     * @param orderCode
     * @return
     */
    public PaymentLog getPaymentLog(String orderCode){
        return paymentLogMapper.getPaymentLog(orderCode);
    }

    /**
     * 依据订单id集合查询支付日志
     * @param orderIds
     * @return
     */
    public List<PaymentLog> getPaymentLogList(List<Long> orderIds){
        return paymentLogMapper.getPaymentLogList(orderIds);
    }
}
