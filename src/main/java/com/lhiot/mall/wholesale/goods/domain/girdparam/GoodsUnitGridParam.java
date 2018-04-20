package com.lhiot.mall.wholesale.goods.domain.girdparam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品单位分页查询的参数")
@NoArgsConstructor
public class GoodsUnitGridParam {
	@ApiModelProperty(notes = "行数", dataType = "Integer")
	private Integer rows;
	@ApiModelProperty(notes = "第几页", dataType = "Integer")
	private Integer page;
	@ApiModelProperty(notes = "排序字段", dataType = "String")
	private String sidx;
	@ApiModelProperty(notes = "排序方式desc或者asc", dataType = "String")
	private String sord;
	@ApiModelProperty(notes="商品单位编码",dataType="String")
	private String unitCode;
	@ApiModelProperty(notes="商品单位名称",dataType="String")
	private String unitName;
}
