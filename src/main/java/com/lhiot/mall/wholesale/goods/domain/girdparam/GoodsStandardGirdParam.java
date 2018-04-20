package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品单位分页查询的参数")
@NoArgsConstructor
public class GoodsStandardGirdParam extends PageObject {
	
	@ApiModelProperty(notes="商品编码",dataType="Long")
	private Long goodsId;
	
	@ApiModelProperty(notes="商品条码",dataType="String")
	private String barCode;
	
	@ApiModelProperty(notes="是否上架 ",dataType="Boolean")
	private Boolean ifVaild;
	
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类名称",dataType="String")
	private String categoryName;
}
