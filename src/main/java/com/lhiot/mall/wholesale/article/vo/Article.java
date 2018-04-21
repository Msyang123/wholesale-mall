package com.lhiot.mall.wholesale.article.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by HuFan on 2018/4/21.
 */
@Data
@ToString
@ApiModel(description = "服务文章参数")
@NoArgsConstructor
public class Article {
    @ApiModelProperty(notes="文章ID",dataType="Long")
    private Long id;

    @ApiModelProperty(notes="文章类别",dataType="Integer")
    private Integer articleType;

    @ApiModelProperty(notes="文章类型",dataType="String")
    private String createType;

    @ApiModelProperty(notes="文章标题",dataType="String")
    private String articleTitle;

    @ApiModelProperty(notes="文章副标题",dataType="String")
    private String articleSubhead;

    @ApiModelProperty(notes="文章作者",dataType="String")
    private String articleAuthor;

    @ApiModelProperty(notes="文章内容",dataType="String")
    private String articleContent;

    @ApiModelProperty(notes="排序",dataType="Integer")
    private Integer rankNum;

    @ApiModelProperty(notes="资源地址",dataType="String")
    private String resouceUrl;

    @ApiModelProperty(notes="图片路径",dataType="String")
    private String articleUrl;
}
