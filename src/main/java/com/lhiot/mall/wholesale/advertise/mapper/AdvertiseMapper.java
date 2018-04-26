package com.lhiot.mall.wholesale.advertise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.AdvertiseType;
import com.lhiot.mall.wholesale.advertise.domain.gridparam.AdvertiseGirdParam;

@Mapper
public interface AdvertiseMapper {

    int insert(Advertise advertise);

    int update(Advertise advertise);

    void removeInbatch(List<Long> ids);

    Advertise select(long id);
    
    //根据类型查询广告
    List<Advertise> findByType(String type);
    
    //分页查询分类
    List<Advertise> pageQuery(AdvertiseGirdParam advertiseGirdParam);
    //查询分类的总记录数
    int pageQueryCount(AdvertiseGirdParam advertiseGirdParam);
}
