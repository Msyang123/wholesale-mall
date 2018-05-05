package com.lhiot.mall.wholesale.introduction.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.sql.Timestamp;


@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Introduction {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("serviceType")
    private String serviceType;

    @JsonProperty("serviceTitle")
    private String serviceTitle;

    @JsonProperty("content")
    private String content;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createTime;

    @JsonProperty("createPerson")
    private String createPerson;

    @JsonProperty("serviceStatus")
    private String serviceStatus;

}
