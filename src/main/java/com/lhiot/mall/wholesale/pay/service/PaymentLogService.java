package com.lhiot.mall.wholesale.pay.service;

import com.lhiot.mall.wholesale.pay.domain.Balance;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.mapper.PaymentLogMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
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
     * 添加日志
     * @param paymentLog
     * @return
     */
    public int insertPaymentLog(PaymentLog paymentLog){
        paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        return paymentLogMapper.insertPaymentLog(paymentLog);
    }

    /**
     * 回调修改日志
     * @return
     */
    public int updatePaymentLog(PaymentLog paymentLog){
        paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        return paymentLogMapper.updatePaymentLog(paymentLog);
    }

    public List<Balance> getBalanceRecord(User user){
        return paymentLogMapper.getBalanceRecord(user);
    }

    /**
     * 依据订单id集合查询支付日志
     * @param orderIds
     * @return
     */
    public List<PaymentLog> getPaymentLogList(List<Long> orderIds){
        return paymentLogMapper.getPaymentLogList(orderIds);
    }

    /**
     * 后台管理--根据订单查询订单实付金额之和
     * @return
     */
    public PaymentLog countFee(String orderCode) {
        //FIXME 应根据orderIds查询，后期统一修改
        String[] orderCodes = orderCode.split(",");
        List<String> list = Arrays.asList(orderCodes);
        return paymentLogMapper.countFee(list);
    }
}
