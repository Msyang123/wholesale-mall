package com.lhiot.mall.wholesale.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ShopResult {
    @JsonProperty("salesId")
    private long salesId;

    @JsonProperty("storeName")
    private String shopName;

    @JsonProperty("storeOwner")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createTime")
    private Timestamp registerTime;

    @JsonProperty("address")
    private String addressDetail;

    @JsonProperty("city")
    private String city;

    @JsonProperty("orderTotal")
    private Integer orderTotal;

    @JsonProperty("orderMoney")
    private Integer ordersTotalFee;

    @JsonProperty("latestMoney")
    private Integer lateOrdersFee;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("latestTime")
    private Timestamp lateTime;

    @JsonProperty("userId")
    private long userId;
}

