package com.lhiot.mall.wholesale.user.domain;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @author zhangs on 2018/5/5.
 */
@Data
@ToString
@ApiModel
@NoArgsConstructor
public class SalesUserPerformance{
    @JsonProperty("id")
    private long id;

    @JsonProperty("openId")
    private String openId;

    @JsonProperty("unionId")
    private String unionId;

    @JsonProperty("salesmanName")
    private String salesmanName;

    @JsonProperty("salesmanPhone")
    private String salesmanPhone;

    @JsonProperty("salesStatus")
    private String salesStatus;

    @JsonProperty("perMonthPerformance")
    private String perMonthPerformance;

    @JsonProperty("thisMonthPerformance")
    private String thisMonthPerformance;

    @JsonProperty("performanceTotal")
    private String performanceTotal;

    @JsonProperty("perMonthNewShopNum")
    private int perMonthNewShopNum = 0;

    @JsonProperty("thisMonthNewShopNum")
    private int thisMonthNewShopNum = 0;

    @JsonProperty("newShopNumTotal")
    private int newShopNumTotal = 0;

    @JsonProperty("overDueTotal")
    private String overDueTotal;
}
