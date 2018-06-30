package com.lhiot.mall.wholesale.pay.mapper;

import com.lhiot.mall.wholesale.pay.domain.Balance;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentLogMapper {


    int insertPaymentLog(PaymentLog paymentLog);

    int updatePaymentLog(PaymentLog paymentLog);

    List<Balance> getBalanceRecord(User user);

    PaymentLog getPaymentLog(String orderCode);

    List<PaymentLog> getPaymentLogList(List<String> orderCodes);

    PaymentLog countFee(List<String> orderId);
    
    //根据订单id查询已支付订单支付记录
    List<PaymentLog> paylogs(List<Long> orderId);
}
