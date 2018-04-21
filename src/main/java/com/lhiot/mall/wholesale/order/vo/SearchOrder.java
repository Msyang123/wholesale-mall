package com.lhiot.mall.wholesale.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.service.ApiListing;

import java.sql.Timestamp;

/**
 * Created by HuFan on 2018/4/21.
 */
@Data
@ToString
@ApiModel
public class SearchOrder {
    @ApiModelProperty(notes = "订单号", dataType = "String")
    private String orderCode;

    @ApiModelProperty(notes = "手机号码", dataType = "String")
    private String phone;

    @ApiModelProperty(notes = "订单状态", dataType = "Integer")
    private int orderStatus;

    @ApiModelProperty(notes = "类型", dataType = "Integer")
    private int order_type;

    @ApiModelProperty(notes = "支付状态", dataType = "Integer")
    private int payStatus;

    //TODO dataType is datetime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(notes = "创建时间起", dataType = "Timestamp")
    private Timestamp createTimeBegin;

    //TODO dataType is datetime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(notes = "创建时间止", dataType = "Timestamp")
    private Timestamp createTimeEnd;
}
