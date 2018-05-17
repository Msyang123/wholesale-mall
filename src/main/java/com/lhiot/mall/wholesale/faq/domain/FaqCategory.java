package com.lhiot.mall.wholesale.faq.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("faqCategoryCreateTime")
    private Timestamp createTime;

    @JsonProperty("createPerson")
    private String createPerson;
    
    @JsonProperty("pId")
    @ApiModelProperty(notes="父节点，默认为零",dataType="Long")
    private String parentId;
}
