package com.lhiot.mall.wholesale.introduction.domain;

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
public class Introduction {
    @JsonProperty("id")
    private long id;

    @JsonProperty("serviceType")
    private Integer serviceType;

    @JsonProperty("serviceTitle")
    private String serviceTitle;

    @JsonProperty("content")
    private String content;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("createPerson")
    private String createPerson;

    @JsonProperty("serviceStatus")
    private Integer serviceStatus;

}
