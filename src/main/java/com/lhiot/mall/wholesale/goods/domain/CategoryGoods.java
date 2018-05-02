package com.lhiot.mall.wholesale.goods.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分类及分类商品")
public class CategoryGoods {
	
	@ApiModelProperty(notes="分类列表",dataType="String")
	List<GoodsCategory> categoryList = new ArrayList<>();
	
	@ApiModelProperty(notes="分类下的商品",dataType="String")
	List<Goods> proList = new ArrayList<>();
}
