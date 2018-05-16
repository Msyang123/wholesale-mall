package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.DebtOrderResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.DebtOrderGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DebtOrderMapper {


    /**
     * 保存账款订单信息
     * @param debtOrder
     * @return
     */
    int save(DebtOrder debtOrder);



    /**
     * 依据订单号修改账款订单
     * @param debtOrder
     * @return
     */
    int updateDebtOrderByCode(DebtOrder debtOrder);

    /**
     * 依据编码查询账款订单信息
     * @param debtOrderCode
     * @return
     */
    DebtOrder findByCode(String debtOrderCode);

    /**
     * 依据订单号模糊查找账号订单
     * @param orderCode
     * @return
     */
    DebtOrder findByOrderIdLike(String orderCode);

    /**
     * 后台管理--分页查询欠款订单总数
     * @param param
     * @return
     */
    List<DebtOrder> pageQuery(DebtOrderGridParam param);


    /**
     * 后台管理--查询分类的总记录数
     * @param param
     * @return
     */
    int pageQueryCount(DebtOrderGridParam param);

    /**
     * 根据账款订单id查询账款订单信息
     * @param id
     * @return
     */
    DebtOrderResult searchDebtOrderById(long id);


    /**
     * 后台管理--导出账款订单
     * @param param
     * @return
     */
    List<Map<String, Object>> exportData(DebtOrderGridParam param);
}
