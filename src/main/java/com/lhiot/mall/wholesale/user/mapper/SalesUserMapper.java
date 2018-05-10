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

    int insertRelation(SalesUserRelation salesUserRelation);

    SalesUserRelation isSeller(long userId);

    SalesUserRelation searchSaleRelationship(SalesUserRelation salesUserRelation);

    List<ShopResult> searchShopInfo(long salesId);

/********************后台管理系统接口***********************/

    int create(SalesUser salesUser);

    int updateById(SalesUser salesUser);

    int deleteByIds(List<String> ids);

    List<SalesUser> list(SalesUser salesUser);

    List<SalesUser> page(Map<String,Object> param);

    SalesUser findById(Long id);

    SalesUser findCode(String code);

    SalesUser login(String acount);

    Integer pageCountSalesUserPerformance(Map<String,Object> param);

    List<SalesUserPerformance> pageSalesUserPerformance(Map<String,Object> param);
}
