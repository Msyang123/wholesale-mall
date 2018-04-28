package com.lhiot.mall.wholesale.faq.domain;

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
public class Faq {

    @JsonProperty("id")
    private long id;

    @JsonProperty("faqCategoryId")
    private long faqCategoryId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("rankNum")
    private String rankNum;

    @JsonProperty("linkUrl")
    private String linkUrl;

    @JsonProperty("createTime")
    private Timestamp createTime;


}
