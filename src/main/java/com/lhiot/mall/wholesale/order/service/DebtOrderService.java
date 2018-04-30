package com.lhiot.mall.wholesale.order.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.DebtOrderGridResult;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.DebtOrderGridParam;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.DebtOrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
/**
 * 欠款账单
 */
public class DebtOrderService {
    private final DebtOrderMapper debtOrderMapper;

    private final UserService userService;

    private final SnowflakeId snowflakeId;

    @Autowired
    public DebtOrderService(DebtOrderMapper debtOrderMapper, UserService userService, SnowflakeId snowflakeId) {
        this.debtOrderMapper = debtOrderMapper;
        this.userService = userService;
        this.snowflakeId=snowflakeId;
    }

    /**
     * 创建账款账款订单
     * @param debtOrder
     * @return
     */
    public int create(DebtOrder debtOrder){
        //产生欠款账款订单编码
        debtOrder.setOrderDebtCode(snowflakeId.stringId());
        debtOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
        debtOrder.setCheckStatus("unpaid");//未提交审核
        return debtOrderMapper.save(debtOrder);
    }

    /**
     * 提交欠款账款订单审核
     * @param debtOrder
     * @return
     */
    public int updateDebtOrderByCode(DebtOrder debtOrder){
        return debtOrderMapper.updateDebtOrderByCode(debtOrder);
    }

    /**
     * 依据欠款账款订单编码查询欠款账款订单
     * @param debtOrderCode
     * @return
     */
    public DebtOrder findByCode(String debtOrderCode){
        return debtOrderMapper.findByCode(debtOrderCode);
    }

    /**
     * 后台管理系统--分页查询账款订单信息
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(DebtOrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String phone = param.getPhone();
        User userParam = new User();
        userParam.setPhone(phone);
        List<DebtOrder> debtOrderList = new ArrayList<DebtOrder>();
        List<User> userList = new ArrayList<User>();
        List<DebtOrderGridResult> debtOrderGridResults = new ArrayList<>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        if(phone == null){//未传手机号查询条件,先根据条件查询分页的账款订单列表及用户ids，再根据ids查询用户信息列表
            count = debtOrderMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages = (count%rows==0?count/rows:count/rows+1);
            if(totalPages < page){
                page = 1;
                param.setPage(page);
                param.setStart(0);
            }
            debtOrderList = debtOrderMapper.pageQuery(param);
            List<Long> userIds = new ArrayList<Long>();
            List<Long> orderIds = new ArrayList<Long>();
            if(debtOrderList != null && debtOrderList.size() > 0){//查询账款订单对应的用户ID列表与账款订单ID列表
                for(DebtOrder debtOrder : debtOrderList){
                    long userId = debtOrder.getUserId();
                    long orderId = debtOrder.getId();
                    if(!userIds.contains(userId)){//用户id去重
                        userIds.add(userId);
                    }
                    if(!orderIds.contains(orderId)){
                        orderIds.add(orderId);
                    }
                }
            }
            userList = userService.search(userIds);//根据用户ID列表查询用户信息
        }else{//传了手机号查询条件，先根据条件查询用户列表及用户ids，再根据ids和账款订单其他信息查询账款订单信息列表
            userList = userService.searchByPhoneOrName(userParam);
            List<Long> userIds = new ArrayList<Long>();
            if(userList != null && userList.size() > 0){
                for(User user : userList){
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
                count = debtOrderMapper.pageQueryCount(param);
                //起始行
                param.setStart((page-1)*rows);
                //总记录数
                totalPages = (count%rows==0?count/rows:count/rows+1);
                if(totalPages < page){
                    page = 1;
                    param.setPage(page);
                    param.setStart(0);
                }
                debtOrderList = debtOrderMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询用户信息
                List<Long> orderIds = new ArrayList<Long>();
                if(debtOrderList != null && debtOrderList.size() > 0){
                    for(DebtOrder debtOrder : debtOrderList){
                        orderIds.add(debtOrder.getId());
                    }
                }
            }
        }
        PageQueryObject result = new PageQueryObject();
        if(debtOrderList != null && debtOrderList.size() > 0){//如果账款订单信息不为空,将账款订单列表与用户信息列表进行行数据组装
            debtOrderGridResults = DataMergeUtils.dataMerge(debtOrderList,userList,"userId","id",DebtOrderGridResult.class);
        }
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(debtOrderGridResults);//将查询记录放入返回参数中
        return result;
    }

}
