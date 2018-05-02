package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.mapper.SalesUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public SalesUser searchSalesUserCode(String code){
        return salesUserMapper.searchSalesUserCode(code);
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
}
