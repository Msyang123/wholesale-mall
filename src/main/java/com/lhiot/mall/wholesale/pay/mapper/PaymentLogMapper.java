package com.lhiot.mall.wholesale.pay.mapper;

import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentLogMapper {


    int insertPaymentLog(PaymentLog paymentLog);

    PaymentLog getPaymentLog(String orderCode);

}
