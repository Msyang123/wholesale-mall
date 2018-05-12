package com.lhiot.mall.wholesale.setting.domain.gridparam;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品分页查询的参数")
@NoArgsConstructor
public class ParamConfigGirdParam extends PageObject {

	@ApiModelProperty(notes="系统参数分类id",dataType="Long")
	private Long configCategoryParamId;
	
    @JsonProperty("configParamKey")
    private String configParamKey;

	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
