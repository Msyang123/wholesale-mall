package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
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

    public int updateUserSaleReletionship(SalesUserRelation salesUserRelation){
        return salesUserMapper.updateUserSaleReletionship(salesUserRelation);
    }
}
