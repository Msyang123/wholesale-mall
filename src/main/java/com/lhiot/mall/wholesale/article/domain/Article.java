package com.lhiot.mall.wholesale.article.domain;

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
public class Article {
    @JsonProperty("id")
    private long id;

    @JsonProperty("articleType")
    private Integer articleType;

    @JsonProperty("createType")
    private String createType;

    @JsonProperty("articleTitle")
    private String articleTitle;

    @JsonProperty("articleSubhead")
    private String articleSubhead;

    @JsonProperty("articleAuthor")
    private String articleAuthor;

    @JsonProperty("articleContent")
    private String articleContent;

    @JsonProperty("rankNum")
    private Integer rankNum;

    @JsonProperty("resouceUrl")
    private String resouceUrl;

    @JsonProperty("articleUrl")
    private String articleUrl;

    @JsonProperty("createTime")
    private Timestamp createTime;

}
