package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DebtOrderMapper {


    /**
     * 保存账款订单信息
     * @param debtOrder
     * @return
     */
    int save(DebtOrder debtOrder);



    /**
     * 依据订单号修改账款订单状态
     * @param debtOrder
     * @return
     */
    int updateDebtOrderStatusByCode(DebtOrder debtOrder);

    /**
     * 依据编码查询账款订单信息
     * @param debtOrderCode
     * @return
     */
    DebtOrder findByCode(String debtOrderCode);
}
