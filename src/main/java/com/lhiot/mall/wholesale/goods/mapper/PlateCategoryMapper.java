package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;

@Mapper
public interface PlateCategoryMapper {
    int insert(PlateCategory plateCategory);

    int update(PlateCategory plateCategory);

    void removeInbatch(List<Long> ids);

    PlateCategory select(long id);

    List<PlateCategory> search();
    
    //分页查询分类
    List<PlateCategory> pageQuery(GoodsStandardGirdParam param);
    //查询分类的总记录数
    int pageQueryCount();
}
