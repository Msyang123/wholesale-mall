package com.lhiot.mall.wholesale.invoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
/**
 * 发票信息
 */
public class Invoice {

    @JsonProperty("id")
    private long id;

    @JsonProperty("invoiceTitleId")
    private long invoiceTitleId;

    @JsonProperty("taxpayerNumber")
    private String taxpayerNumber;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("contactName")
    private String contactName;

    @JsonProperty("contactPhone")
    private String contactPhone;

    @JsonProperty("addressArea")
    private String addressArea;

    @JsonProperty("addressDetail")
    private String addressDetail;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("bankCardCode")
    private String bankCardCode;

    @JsonProperty("invoiceFee")
    private int invoiceFee;

    @JsonProperty("invoiceTax")
    private BigDecimal invoiceTax;

    @JsonProperty("taxFee")
    private int taxFee;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createTime;

    @JsonProperty("invoiceStatus")
    private String invoiceStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("invoicePrintTime")
    private Timestamp invoicePrintTime;

    @JsonProperty("invoiceOrderIds")
    private String invoiceOrderIds;

    @JsonProperty("invoiceCode")
    private String invoiceCode;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("orderDetail")
    private OrderDetail orderDetail;

    @JsonProperty("orderNumber")
    private Integer orderNumber;
}
