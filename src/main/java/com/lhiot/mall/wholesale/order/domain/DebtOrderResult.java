package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Created by HuFan on 2018/4/25.
 */
@Data
@ApiModel
@NoArgsConstructor
public class DebtOrderResult {
    @JsonProperty("id")
    private long id;

    @JsonProperty("checkTime")
    private String checkTime;

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

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;
}
