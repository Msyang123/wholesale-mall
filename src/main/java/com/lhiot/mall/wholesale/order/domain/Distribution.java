package com.lhiot.mall.wholesale.order.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Distribution {

    private Integer minPrice;

    private Integer maxPrice;

    private Integer distributionFee;
}
