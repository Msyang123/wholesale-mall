package com.lhiot.mall.wholesale.invoice.domain;

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
public class InvoiceTitle {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("taxpayerNumber")
    private String taxpayerNumber;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("addressArea")
    private String addressArea;

    @JsonProperty("addressDetail")
    private String addressDetail;

    @JsonProperty("contactName")
    private String contactName;

    @JsonProperty("contactPhone")
    private String contactPhone;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("bankCardCode")
    private String bankCardCode;

}
