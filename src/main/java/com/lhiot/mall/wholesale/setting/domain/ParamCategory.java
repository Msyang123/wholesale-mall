package com.lhiot.mall.wholesale.setting.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ParamCategory {

	@ApiModelProperty(notes="广告ID",dataType="Long")
    private Long id;

	@ApiModelProperty(notes="参数分类名称",dataType="String")
    private String paramCategoryName;

	@ApiModelProperty(notes="参数分类编码",dataType="String")
    private String paramCategoryCode;

	@ApiModelProperty(notes="后台管理展示样式，normal-常态 list-列表",dataType="String")
    private String showType;
    
	@ApiModelProperty(notes="父节点id",dataType="Long")
    private Long parentId;
}
