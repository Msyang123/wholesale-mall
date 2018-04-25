package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品分页查询的参数")
@NoArgsConstructor
public class GoodsGirdParam extends PageObject {

	@ApiModelProperty(notes="商品编码",dataType="String")
	private String goodsCode;
	
	@ApiModelProperty(notes="商品名字",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
	
	@ApiModelProperty(notes="商品版块id",dataType="Long")
	private Long plateId;
	
	@ApiModelProperty(notes="分页查询开始页面",dataType="Integer")
	private Integer start;
}
