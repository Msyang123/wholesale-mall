package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(description = "商品价格区间分页查询的参数")
public class PriceRegionGirdParam extends PageObject {

	@ApiModelProperty(notes="",dataType="Integer")
	private Integer minPrice;
	
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer maxPrice;
	
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
