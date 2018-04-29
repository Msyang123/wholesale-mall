package com.lhiot.mall.wholesale.pay.mapper;

import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentLogMapper {

    int insertPaymentLog(PaymentLog paymentLog);

    PaymentLog getPaymentLog(String orderCode);

    List <PaymentLog> getBalanceRecord(Integer userId);

}
