package com.lhiot.mall.wholesale.user.mapper;

import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SalesUserMapper {

    List<SalesUserRelation> selectRelation(Map<String,Object> param);

    int updateUserSaleRelationship(SalesUserRelation salesUserRelation);

    SalesUser searchSalesUser(long id);

    SalesUser searchSalesUserCode(String code);

    int insertRelation(SalesUserRelation salesUserRelation);

    SalesUserRelation isSeller(long userId);
}
