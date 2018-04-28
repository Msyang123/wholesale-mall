package com.lhiot.mall.wholesale.setting.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
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
}
