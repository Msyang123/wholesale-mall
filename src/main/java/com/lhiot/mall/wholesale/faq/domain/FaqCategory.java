package com.lhiot.mall.wholesale.faq.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FaqCategory {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("faqCategoryName")
    private String faqCategoryName;

    @JsonProperty("faqList")
    private List<Faq> faqList;

    @JsonProperty("faqCategoryCreateTime")
    private Timestamp createTime;

    @JsonProperty("faqCategoryCreatePerson")
    private String createPerson;
}
