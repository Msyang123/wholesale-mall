package com.lhiot.mall.wholesale.invoice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("invoiceType")
    private int invoiceType;

    @JsonProperty("invoiceTax")
    private BigDecimal invoiceTax;

    @JsonProperty("taxFee")
    private int taxFee;

    @JsonProperty("taxFee")
    private Timestamp createTime;

    @JsonProperty("invoiceStatus")
    private int invoiceStatus;

    @JsonProperty("invoicePrintTime")
    private Timestamp invoicePrintTime;

    @JsonProperty("invoiceOrderIds")
    private String invoiceOrderIds;

    @JsonProperty("invoiceCode")
    private String invoiceCode;
}
