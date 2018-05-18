package com.lhiot.mall.wholesale.user.mapper;

import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserPerformance;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SalesUserMapper {

    List<SalesUserRelation> selectRelation(Map<String,Object> param);

    Integer updateUserSaleRelationship(SalesUserRelation salesUserRelation);

    SalesUser searchSalesUser(long id);

    SalesUser searchSalesUserByOpenid(String openid);

    Integer insertRelation(SalesUserRelation salesUserRelation);

    SalesUserRelation isSeller(long userId);

    SalesUserRelation searchSaleRelationship(SalesUserRelation salesUserRelation);

    List<ShopResult> searchShopInfo(long salesId);

    List<SalesUserRelation> selectUserRelation(Long userId);

/********************后台管理系统接口***********************/

    Integer create(SalesUser salesUser);

    Integer updateById(SalesUser salesUser);

    Integer deleteByIds(List<String> ids);

    List<SalesUser> list(SalesUser salesUser);

    List<SalesUser> page(Map<String,Object> param);

    SalesUser findById(Long id);

    SalesUser findCode(String code);

    SalesUser login(String acount);
    //查询所有的销售，用于下拉
    List<SalesUser> salesUsers();

    Integer pageCountSalesUserPerformance(Map<String,Object> param);

    List<SalesUserPerformance> pageSalesUserPerformance(Map<String,Object> param);

    Integer pageCount(Map<String, Object> param);

    Integer updateSalesmanIdByUserId(Map<String, Object> param);

    Integer updateSalesmanIdBySalesmanId(Map<String, Object> param);

    Integer deleteRelation(Long id);
}
