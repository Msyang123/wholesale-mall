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
	
	@ApiModelProperty(notes="是否上架 ",dataType="String")
	private String vaild;
	
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
	
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="商品最低价格",dataType="Integer")
	private Integer minPrice;
	
	@ApiModelProperty(notes="商品最高价格",dataType="Integer")
	private Integer maxPrice;
	
	@ApiModelProperty(notes="版块di",dataType="Long")
	private Long plateId;
}
