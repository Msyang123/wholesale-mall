package com.lhiot.mall.wholesale.article.domain;

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
public class Article {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("articleType")
    private String articleType;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("createTime")
    private Timestamp createTime;

    @JsonProperty("publishStatus")
    private String publishStatus;

    @JsonProperty("rows")
    private Integer rows;

    @JsonProperty("page")
    private Integer page;

    private Integer start;

    private String sidx;

}
