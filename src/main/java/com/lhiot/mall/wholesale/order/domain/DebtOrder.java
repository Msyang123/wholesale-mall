package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long id;

    @JsonProperty("checkTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp checkTime;

    @JsonProperty("orderDebtCode")
    private String orderDebtCode;

    @JsonProperty("userId")
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("createTime")
    private Timestamp createTime;

    @JsonProperty("debtFee")
    private Integer debtFee;

    @JsonProperty("checkStatus")
    private String checkStatus;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("paymentEvidence")
    private String paymentEvidence;

    @JsonProperty("orderIds")
    private String orderIds;

    @JsonProperty("remarks")
    private String remarks;
}
