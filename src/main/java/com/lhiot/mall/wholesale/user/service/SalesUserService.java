package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.mapper.SalesUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SalesUserService {

    private final SnowflakeId snowflakeId;

    private final SalesUserMapper salesUserMapper;


    @Autowired
    public SalesUserService(SqlSession sqlSession, SnowflakeId snowflakeId, SalesUserMapper salesUserMapper) {
        this.snowflakeId = snowflakeId;
        this.salesUserMapper = salesUserMapper;
    }


    public List<SalesUserRelation> selectRelation(Map<String,Object> param) {
        return salesUserMapper.selectRelation(param);
    }

    public int updateUserSaleRelationship(SalesUserRelation salesUserRelation){
        return salesUserMapper.updateUserSaleRelationship(salesUserRelation);
    }

    public SalesUser searchSalesUser(long id){
        return salesUserMapper.searchSalesUser(id);
    }

    public SalesUser searchSalesUserByOpenid(String openid){
        return salesUserMapper.searchSalesUserByOpenid(openid);
    }

    public int insertRelation(SalesUserRelation salesUserRelation){
        return salesUserMapper.insertRelation(salesUserRelation);
    }

    public SalesUserRelation isSeller(long userId){
        return salesUserMapper.isSeller(userId);
    }

    public SalesUserRelation searchSaleRelationship(SalesUserRelation salesUserRelation){
        return salesUserMapper.searchSaleRelationship(salesUserRelation);
    }

    public List<ShopResult> searchShopInfo(long salesId){
        return salesUserMapper.searchShopInfo(salesId);
    }


    /***************后台管理系统*******************/
    public int create(SalesUser salesUser){
        return this.salesUserMapper.create(salesUser);
    }

    public int updateById(SalesUser salesUser){
        return this.salesUserMapper.updateById(salesUser);
    }

    public int deleteByIds(String ids){
        List<String> idList = Arrays.asList(ids.split(","));
        return this.salesUserMapper.deleteByIds(idList);
    }

    public List<SalesUser> list(SalesUser salesUser){
        return this.salesUserMapper.list(salesUser);
    }

    public List<SalesUser> page(Map<String,Object> param){
        return this.salesUserMapper.page(param);
    }


    public SalesUser findById(Long id){
        return this.salesUserMapper.findById(id);
    }

    public SalesUser findCode(String code){
        return this.salesUserMapper.findCode(code);
    }

    public SalesUser login(String acount){
        return this.salesUserMapper.login(acount);
    }

    /**
     * 门店管理信息
     * @param salesId
     * @return
     */
   /* public List<ShopResult> shopResultList(Long salesId){
        List<ShopResult> shopResults = salesUserMapper.searchShopInfo(salesId);//查询门店基本信息
        if(shopResults.isEmpty()){
            return new ArrayList<ShopResult>();
        }
        for (ShopResult result:shopResults){
            System.out.println(result.getUserId());
            OrderDetail orderDetail = orderService.lateOneOrder(result.getUserId());//最近一单消费记录
            OrderParam param = new OrderParam();//传参对象
            if (orderDetail!=null){
                param.setId(result.getUserId());
                result.setLateOrdersFee(orderDetail.getPayableFee());//最近一单的消费金额
                result.setLateTime(orderDetail.getCreateTime());//最近一单的下单时间
                List<OrderDetail> orderDetailList = orderService.lateOrders(param);
                if (orderDetailList.isEmpty()){
                    result.setOrdersTotalFee(0);//最近下单总金额
                    result.setOrderTotal(0);//订单数
                }else{
                    result.setOrdersTotalFee(orderService.lateOrdersFee(param));//最近下单总金额
                    result.setOrderTotal(orderDetailList.size());//订单数
                }
            }else{
                result.setLateOrdersFee(0);//最近一单的消费金额
                result.setLateTime(null);//最近一单的下单时间
                result.setOrdersTotalFee(0);//最近下单总金额
                result.setOrderTotal(0);//订单数
            }
        }
        return shopResults;
    }*/
}
