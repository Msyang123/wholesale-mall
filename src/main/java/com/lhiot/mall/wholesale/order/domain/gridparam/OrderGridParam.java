package com.lhiot.mall.wholesale.order.domain.gridparam;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leon.microx.common.wrapper.PageObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderGridParam extends PageObject{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("orderStatus")
    private String orderStatus;

    @JsonProperty("payStatus")
    private String payStatus;

    @JsonProperty("createTimeBegin")
    private String createTimeBegin;

    @JsonProperty("createTimeEnd")
    private String createTimeEnd;

    @JsonProperty("settlementType")
    private String settlementType;

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

    @ApiModelProperty(notes="用户ids",dataType="Long")
    private List<Long> userIds;

    @ApiModelProperty(notes="订单codes",dataType="String")
    private List<String> orderIds;

}
