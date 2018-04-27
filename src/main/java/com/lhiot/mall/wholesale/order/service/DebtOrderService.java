package com.lhiot.mall.wholesale.order.service;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.mapper.DebtOrderMapper;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.hd.client.HaiDingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
/**
 * 欠款账单
 */
public class DebtOrderService {
    private final DebtOrderMapper debtOrderMapper;

    private final SnowflakeId snowflakeId;

    @Autowired
    public DebtOrderService(DebtOrderMapper debtOrderMapper, SnowflakeId snowflakeId) {
        this.debtOrderMapper = debtOrderMapper;
        this.snowflakeId=snowflakeId;
    }

    /**
     * 创建账款订单
     * @param debtOrder
     * @return
     */
    public int create(DebtOrder debtOrder){
        //产生欠款订单编码
        debtOrder.setOrderDebtCode(snowflakeId.stringId());
        debtOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
        debtOrder.setCheckStatus(0);//未提交审核
        return debtOrderMapper.save(debtOrder);
    }

    /**
     * 提交欠款订单审核
     * @param debtOrder
     * @return
     */
    public int updateDebtOrderStatus(DebtOrder debtOrder){
        return debtOrderMapper.updateDebtOrderStatusByCode(debtOrder);
    }

    /**
     * 依据欠款订单编码查询欠款订单
     * @param debtOrderCode
     * @return
     */
    public DebtOrder findByCode(String debtOrderCode){
        return debtOrderMapper.findByCode(debtOrderCode);
    }

}
