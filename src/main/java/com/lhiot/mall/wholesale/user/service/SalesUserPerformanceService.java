package com.lhiot.mall.wholesale.user.service;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserPerformance;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.mapper.SalesUserMapper;
import org.apache.http.client.utils.DateUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
/**
 * @author zhangs on 2018/5/5.
 */
@Service
@Transactional
public class SalesUserPerformanceService{
    private final SalesUserMapper salesUserMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final SalesUserService salesUserService;
    private final UserService userService;
    @Autowired
    public SalesUserPerformanceService(SqlSession sqlSession, SnowflakeId snowflakeId, SalesUserMapper salesUserMapper,
            OrderService orderService,SalesUserService salesUserService,OrderMapper orderMapper,UserService userService) {
        this.salesUserMapper = salesUserMapper;
        this.orderService = orderService;
        this.salesUserService = salesUserService;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(Map<String,Object> param){
        PageQueryObject result = new PageQueryObject();
        int count = salesUserMapper.pageCountSalesUserPerformance(param);
        int page = (Integer)param.get("page");
        int rows = (Integer)param.get("rows");
        //起始行
        param.put("start",(page-1)*rows);
        //param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            page = 1;
            param.put("page",page);
            param.put("start",0);
        }
        List<SalesUserPerformance> salesUserPerformances = salesUserMapper.pageSalesUserPerformance(param);
        //本月第一天
        String thisMonthFirstDay =com.lhiot.mall.wholesale.base.DateUtils.thisMonthFirstDay();
        //上月第一天
        String preMonthFirstDay =com.lhiot.mall.wholesale.base.DateUtils.perMonthFirstDay();
        if(!CollectionUtils.isEmpty(salesUserPerformances)){
            for(SalesUserPerformance salesUserPerformance:salesUserPerformances){
                List<ShopResult> shopResults = salesUserService.searchShopInfo(salesUserPerformance.getId());
                List<Long> shopIds = getShopIds(shopResults);
                //上月业绩
                salesUserPerformance.setPerMonthPerformance(orderService.countPayAbleFeeByUserId(shopIds,preMonthFirstDay,thisMonthFirstDay));
                //本月业绩
                salesUserPerformance.setThisMonthPerformance(orderService.countPayAbleFeeByUserId(shopIds,thisMonthFirstDay,null));
                //业绩总额
                salesUserPerformance.setPerformanceTotal(orderService.countPayAbleFeeByUserId(shopIds,null,null));
                //累计欠款
                salesUserPerformance.setOverDueTotal(orderService.countOverDue(shopIds));
                if(!CollectionUtils.isEmpty(shopResults)){
                    salesUserPerformance.setNewShopNumTotal(shopIds.size());
                    for(ShopResult shop:shopResults){
                        if(null != shop){
                            if(orderService.isExistsOrderByuserId(shop.getUserId())){
                                salesUserPerformance.setNewShopNumTotal(salesUserPerformance.getNewShopNumTotal()+1);
                                if(shop.getRegisterTime().getTime()>DateFormatUtil.parse5(thisMonthFirstDay).getTime()){
                                    salesUserPerformance.setThisMonthNewShopNum(salesUserPerformance.getThisMonthNewShopNum()+1);
                                }else if(shop.getRegisterTime().getTime()>DateFormatUtil.parse5(preMonthFirstDay).getTime()){
                                    salesUserPerformance.setPerMonthNewShopNum(salesUserPerformance.getPerMonthNewShopNum()+1);
                                }
                            }
                        }
                    }
                }
            }
        }
        result.setRows(salesUserPerformances);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }
    private List<Long> getShopIds(List<ShopResult> shopResults){
        List<Long> shopIds = new ArrayList<Long>();
        if(!CollectionUtils.isEmpty(shopResults)){
            for(ShopResult shopResult:shopResults){
                if(null != shopResult){
                    shopIds.add(shopResult.getUserId());
                }
            }
        }
        return shopIds;
    }
    public PageQueryObject pagePerformanceDetail(OrderGridParam param){
        PageQueryObject result = new PageQueryObject();
        int count = orderMapper.pageQueryCount(param);
        int page = param.getPage();
        int rows = param.getRows();
        //起始行
        param.setStart((page-1)*rows);
        //param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            param.setPage(0);
            param.setStart(0);
        }
        try{
            result=pageOrderQuery(param);
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }catch(IntrospectionException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
//        result.setRows(salesUserPerformances);
//        result.setPage(page);
//        result.setRecords(rows);
//        result.setTotal(totalPages);
        return result;
    }

    /**
     * 后台管理系统--分页查询订单信息
     * @param param
     * @return
     */
    public PageQueryObject pageOrderQuery(OrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException{
        String phone=param.getPhone();
        User userParam=new User();
        userParam.setPhone(phone);
        List<OrderGridResult> orderGridResultList=new ArrayList<OrderGridResult>();
        List<User> userList=new ArrayList<User>();
        int count=0;
        int page=param.getPage();
        int rows=param.getRows();
        //总记录数
        int totalPages=0;
        if(phone==null){//未传手机号查询条件,先根据条件查询分页的订单列表及用户ids，再根据ids查询用户信息列表
            count=orderMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages=(count%rows==0?count/rows:count/rows+1);
            if(totalPages<page){
                page=1;
                param.setPage(page);
                param.setStart(0);
            }
            orderGridResultList=orderMapper.pageQuery(param);
            List<Long> userIds = new ArrayList<Long>();
            List<Long> orderIds = new ArrayList<Long>();
            if(orderGridResultList != null && orderGridResultList.size() > 0){//查询订单对应的用户ID列表与订单ID列表
                for(OrderGridResult orderGridResult : orderGridResultList){
                    long orderId = orderGridResult.getId();
                    long userId = orderGridResult.getUserId();
                    if(!userIds.contains(userId)){//用户id去重
                        userIds.add(userId);
                    }
                    if(!orderIds.contains(orderId)){
                        orderIds.add(orderId);
                    }
                }
            }
            userList = userService.search(userIds);//根据用户ID列表查询用户信息
        }else{//传了手机号查询条件，先根据条件查询用户列表及用户ids，再根据ids和订单其他信息查询订单信息列表
            userList=userService.searchByPhoneOrName(userParam);
            List<Long> userIds=new ArrayList<Long>();
            if(userList!=null&&userList.size()>0){
                for(User user : userList){
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
                count=orderMapper.pageQueryCount(param);
                //起始行
                param.setStart((page-1)*rows);
                //总记录数
                totalPages=(count%rows==0?count/rows:count/rows+1);
                if(totalPages<page){
                    page=1;
                    param.setPage(page);
                    param.setStart(0);
                }
                orderGridResultList=orderMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询用户信息
                List<Long> orderIds=new ArrayList<Long>();
                if(orderGridResultList!=null&&orderGridResultList.size()>0){
                    for(OrderGridResult orderGridResult : orderGridResultList){
                        orderIds.add(orderGridResult.getId());
                    }
                }
            }
        }
        PageQueryObject result=new PageQueryObject();
        if(orderGridResultList!=null&&orderGridResultList.size()>0){//如果订单信息不为空,将订单列表与用户信息列表进行行数据组装
            //根据用户id与订单中的用户id匹配
            for(OrderGridResult orderGridResult : orderGridResultList){
                Long orderUserId=orderGridResult.getUserId();
                for(User user : userList){
                    Long uId=user.getId();
                    if(Objects.equals(orderUserId,uId)){
                        orderGridResult.setPhone(user.getPhone());
                        orderGridResult.setUserName(user.getUserName());
                        orderGridResult.setShopName(user.getShopName());
                        orderGridResult.setCreateTime(orderGridResult.getCreateTime().toString());
                        break;
                    }
                }
            }
            //根据订单id和支付记录orderId进行信息匹配
            //            for (OrderGridResult orderGridResult : orderGridResultList) {
            //                Long orderId = orderGridResult.getId();
            //                for (PaymentLog paymentLog : paymentLogList) {
            //                    Long porderId = paymentLog.getOrderId();
            //                    if (Objects.equals(orderId, porderId)) {
            //                        orderGridResult.setPaymentTime(paymentLog.getPaymentTime());
            //                        break;
            //                    }
            //                }
            //            }
            //        }
            result.setPage(page);
            result.setRecords(rows);
            result.setTotal(totalPages);
            result.setRows(orderGridResultList);//将查询记录放入返回参数中
        }
        return result;
    }

}
