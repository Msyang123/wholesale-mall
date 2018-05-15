package com.lhiot.mall.wholesale.user.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
public class SalesUser {

    @JsonProperty("id")
    private long id;

    @JsonIgnore
    @JsonProperty("openId")
    private String openId;

    @JsonIgnore
    @JsonProperty("unionId")
    private String unionId;

    @JsonProperty("name")
    private String salesmanName;

    @JsonProperty("phone")
    private String salesmanPhone;

    @JsonProperty("salesmanPassword")
    private String salesmanPassword;

    @JsonProperty("src")
    private String salesmanHeadImage;

    @JsonIgnore
    @JsonProperty("createPerson")
    private int createPerson;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createTime")
    private Timestamp createAt;

    @JsonProperty("inviteCode")
    private String inviteCode;

    @JsonProperty("account")
    private String account;

    @JsonProperty("status")
    private String salesStatus;

}
