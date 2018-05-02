package com.lhiot.mall.wholesale.article.domain.gridparam;

import com.leon.microx.common.wrapper.PageObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by HuFan on 2018/4/23.
 */
@Data
@ToString
@ApiModel
public class ArticleGridParam extends PageObject {
    @ApiModelProperty(notes="文章标题",dataType="String")
    private String articleTitle;

    @ApiModelProperty(notes="文章类别",dataType="String")
    private Integer articleType;

    @ApiModelProperty(notes="分页查询开始页面",dataType="Integer")
    private Integer start;

    @ApiModelProperty(notes="页数",dataType="Integer")
    private Integer page;

    @ApiModelProperty(notes="每页行数",dataType="Integer")
    private Integer rows;

    @ApiModelProperty(notes="排序字段",dataType="String")
    private String sidx;

    @ApiModelProperty(notes="排序",dataType="String")
    private String sord;
}
