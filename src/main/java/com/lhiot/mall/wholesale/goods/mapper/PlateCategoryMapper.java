package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PlateGirdParam;

@Mapper
public interface PlateCategoryMapper {
    int insert(PlateCategory plateCategory);

    int update(PlateCategory plateCategory);

    void removeInbatch(List<Long> ids);

    PlateCategory select(long id);
    
    List<PlateCategory> searchByType(String type);
    
    List<PlateCategory> searchAll();
    
    List<PlateCategory> findTree();
    
    List<PlateCategory> search(List<Long> ids);
    
    //分页查询分类
    List<PlateCategory> pageQuery(PlateGirdParam plateGirdParam);
    //查询分类的总记录数
    int pageQueryCount();
    
    //判断是否存在滚动布局的版块
    int rollongLayout();
    //获取版块的商品id
    List<Long> plateGoodsId(String type);
}
