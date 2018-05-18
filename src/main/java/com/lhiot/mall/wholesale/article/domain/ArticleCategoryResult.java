package com.lhiot.mall.wholesale.article.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ArticleCategoryResult {
    @JsonProperty("industryList")
    private List<Article> industryList;

    @JsonProperty("perdayList")
    private List<Article> perdayList;

}
