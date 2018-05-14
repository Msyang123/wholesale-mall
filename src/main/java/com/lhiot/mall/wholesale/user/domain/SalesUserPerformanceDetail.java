package com.lhiot.mall.wholesale.user.domain;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @author zhangs on 2018/5/9.
 */
@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SalesUserPerformanceDetail{
    @JsonProperty("id")
    private long id;

    @JsonProperty("phone")
    private String phone;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("registerTime")
    private Timestamp registerTime;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("openid")
    private String openid;

    @JsonProperty("unionid")
    private String unionid;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("userStatus")
    private String userStatus;

    @JsonProperty("auditStatus")
    private String auditStatus;
    //累计业绩
    @JsonProperty("performanceTotal")
    private String performanceTotal;
    //累计业绩频次
    @JsonProperty("performanceTotalFrequency")
    private String performanceTotalFrequency;
    //累计业绩/频次
    @JsonProperty("performanceTotalStr")
    private String performanceTotalStr;
    //账款
    @JsonProperty("overDue")
    private String overDueNum;
    //账款笔数
    @JsonProperty("overDueFrequency")
    private String overDueFrequency;
    //账款/账款笔数
    @JsonProperty("overDueStr")
    private String overDueStr;
    //最近下单时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastOneOrdertime")
    private Timestamp lastOneOrdertime;
}
