package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(description = "商品关键词分页查询的参数")
public class KeywordsGirdParam extends PageObject {
	
	@ApiModelProperty(notes="关键词",dataType="String")
	private String keyword;
	
	@ApiModelProperty(notes="是否为热搜商品",dataType="Boolean")
	private Boolean hotSearch;
}
