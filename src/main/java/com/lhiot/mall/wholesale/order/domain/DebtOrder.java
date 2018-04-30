package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DebtOrder {

    @JsonProperty("id")
    private long id;

    @JsonProperty("checkTime")
    private Timestamp checkTime;

    @JsonProperty("orderDebtCode")
    private String orderDebtCode;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("createTime")
    private Timestamp createTime;

    @JsonProperty("debtFee")
    private int debtFee;

    @JsonProperty("checkStatus")
    private String checkStatus;

    @JsonProperty("paymentEvidence")
    private String paymentEvidence;

    @JsonProperty("orderIds")
    private String orderIds;

    @JsonProperty("remarks")
    private String remarks;
}
