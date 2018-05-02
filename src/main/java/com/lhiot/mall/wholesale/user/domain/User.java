package com.lhiot.mall.wholesale.user.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class User {

    @JsonProperty("id")
    private long id;

    @ApiModelProperty(notes = "性别:MALE(男),FEMALE(女),UNKNOWN(未知)", dataType = "String")
    @JsonProperty("sex")
    private String sex;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String addressDetail;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("registerTime")
    private String registerTime;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("balance")
    private int balance;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userStatus")
    private int userStatus;

    @JsonProperty("profilePhoto")
    private String profilePhoto;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("START")
    private int START;

    @JsonProperty("ROWS")
    private int ROWS;

    @JsonProperty("city")
    private String city;

    @JsonProperty("namePhone")
    private String namePhone;

    @JsonProperty("debtFee")
    private Integer debtFee;
    /*public enum Sex {
        MALE, FEMALE, UNKNOWN
    }*/
}
