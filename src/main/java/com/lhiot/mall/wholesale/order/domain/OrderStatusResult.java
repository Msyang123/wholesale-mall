package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by HuFan on 2018/5/5.
 */
@Data
@ApiModel
@NoArgsConstructor
public class OrderStatusResult {
    @JsonProperty("orderStatus")
    private String orderStatus;

    @JsonProperty("settlementType")
    private String settlementType;

    @JsonProperty("payStatus")
    private String payStatus;

}
