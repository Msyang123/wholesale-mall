package com.lhiot.mall.wholesale.user.domain.gridparam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @author zhangs on 2018/5/9.
 */
@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPerformanceGridParam{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("salesUserId")
    private Long salesUserId;

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

    @ApiModelProperty(notes="门店名称",dataType="String")
    private String shopName;

    @ApiModelProperty(notes="手机号",dataType="String")
    private String phone;

    @ApiModelProperty(notes="店长",dataType="String")
    private String userName;

}
