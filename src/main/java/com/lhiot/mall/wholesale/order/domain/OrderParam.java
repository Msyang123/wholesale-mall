package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderParam {
    @JsonProperty("dayNum")
    private Integer dayNum;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("salesAmount")
    private Integer salesAmount;
}
