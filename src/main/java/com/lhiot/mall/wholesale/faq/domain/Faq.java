package com.lhiot.mall.wholesale.faq.domain;

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
public class Faq {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("faqCategoryId")
    private Long faqCategoryId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("rankNum")
    private Integer rankNum;

    @JsonProperty("linkUrl")
    private String linkUrl;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createTime;

    @JsonProperty("faqStatus")
    private String faqStatus;

    @JsonProperty("createPerson")
    private String createPerson;

    /*后台管理分页查询grid所需字段*/
    @JsonProperty("faqCategoryName")
    private String faqCategoryName;
}
