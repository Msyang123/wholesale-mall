package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsKeywords {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="关键词",dataType="String")
	private String keyword;
	
	@ApiModelProperty(notes="是否为热搜商品",dataType="Boolean")
	private Boolean hotSearch;
	
	@ApiModelProperty(notes="是否为热搜商品",dataType="Integer")
	private Integer isHotSearch;
	
	@ApiModelProperty(notes="关键词类型 goods-商品,category-分类",dataType="String")
	private String kwType;
	
	@ApiModelProperty(notes="管理商品或者分类的id",dataType="Long")
	private Long mappingId;
	
	@ApiModelProperty(notes="品类或商品名称",dataType="String")
	private String mappingName;
}
