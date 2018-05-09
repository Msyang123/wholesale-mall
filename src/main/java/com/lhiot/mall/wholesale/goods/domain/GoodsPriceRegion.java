package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsPriceRegion {

	@JsonProperty("id")
	@ApiModelProperty(notes="商品价格区间id",dataType="Long")
	private Long id;

	@JsonProperty("goodsStandardId")
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;

	@JsonProperty("price")
	@ApiModelProperty(notes="价格",dataType="Integer")
	private Integer price;
	
	@ApiModelProperty(notes="后台管理",dataType="String")
	private String priceDisplay;

	@JsonProperty("categoryId")
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;

	@JsonProperty("desc")
	@ApiModelProperty(notes="规格数量描述",dataType="String")
	private String standardDes;

	@JsonProperty("minCount")
	@ApiModelProperty(notes="享受价格的最低购买数量",dataType="Integer")
	private Integer minQuantity;

	@JsonProperty("maxCount")
	@ApiModelProperty(notes="享受价格的最高购买数量",dataType="Integer")
	private Integer maxQuantity;

	@JsonProperty("reorder")
	@ApiModelProperty(notes="排序",dataType="Integer")
	private Integer reorder;

	@JsonProperty("goodsName")
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;

	@JsonProperty("standard")
	@ApiModelProperty(notes="商品规格",dataType="String")
	private String standard;

	@JsonProperty("goodsUnit")
	@ApiModelProperty(notes="商品单位",dataType="String")
	private String goodsUnit;
}
