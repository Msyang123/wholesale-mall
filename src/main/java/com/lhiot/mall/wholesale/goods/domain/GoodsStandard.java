package com.lhiot.mall.wholesale.goods.domain;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsStandard {

	@ApiModelProperty(notes="商品规格ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品编码",dataType="Long")
	private Long goodsId;
	
	@ApiModelProperty(notes="商品条码",dataType="String")
	private String barCode;
	
	@ApiModelProperty(notes="重量",dataType="weight")
	private BigDecimal weight;
	
	@ApiModelProperty(notes="计量单位",dataType="Long")
	private Long unitCode;
	
	@ApiModelProperty(notes="规格",dataType="String")
	private String standard;
	
	@ApiModelProperty(notes="原价",dataType="Integer")
	private Integer price;
	
	@ApiModelProperty(notes="是否上架 ",dataType="Boolean")
	private Boolean ifVaild;
	
	@ApiModelProperty(notes="海鼎商品sku_id",dataType="String")
	private String hdSkuId;
	
	@ApiModelProperty(notes="规格商品描述",dataType="String")
	private String standardDesc;
	
	@ApiModelProperty(notes="产品编码",dataType="String")
	private String goodsCode;
	
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="主图路径",dataType="String")
	private String goodsImage;
	
	@ApiModelProperty(notes="商品规格单位名称",dataType="String")
	private String goodsUnitName;

	@ApiModelProperty(notes="商品价格区间",dataType="List")
	private List<GoodsPriceRegion> goodsPriceRegionList;
	
}
