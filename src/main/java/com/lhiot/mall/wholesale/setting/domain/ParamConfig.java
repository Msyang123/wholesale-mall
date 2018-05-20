package com.lhiot.mall.wholesale.setting.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ParamConfig {

    @JsonProperty("id")
    private long id;

    @JsonProperty("configCategoryParamId")
    private long configCategoryParamId;

    @JsonProperty("configParamKey")
    private String configParamKey;

    @JsonProperty("configParamValue")
    private String configParamValue;
    
    @ApiModelProperty(notes="参数配置类型 system-系统参数，business - 业务参数",dataType="String")
    private String paramType;
    
    @ApiModelProperty(notes="后台管理系统，使用字段",dataType="String")
    private String showType;
    
    private String notes;
}
