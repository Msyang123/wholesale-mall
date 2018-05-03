package com.lhiot.mall.wholesale.user.domain;


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

    @JsonProperty("openId")
    private String openId;

    @JsonProperty("unionId")
    private String unionId;

    @JsonProperty("salesmanName")
    private String salesmanName;

    @JsonProperty("salesmanPhone")
    private String salesmanPhone;

    @JsonProperty("salesmanPassword")
    private String salesmanPassword;

    @JsonProperty("salesmanHeadImage")
    private String salesmanHeadImage;

    @JsonProperty("createPerson")
    private int createPerson;

    @JsonProperty("createAt")
    private Timestamp createAt;

    @JsonProperty("inviteCode")
    private String inviteCode;

    @JsonProperty("acount")
    private String acount;

    @JsonProperty("salesStatus")
    private String salesStatus;

}
