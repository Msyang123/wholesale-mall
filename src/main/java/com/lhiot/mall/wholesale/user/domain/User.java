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

    @JsonProperty("address_detail")
    private String addressDetail;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("register_time")
    private String registerTime;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("balance")
    private int balance;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_status")
    private String userStatus;

    @JsonProperty("profile_photo")
    private String profilePhoto;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("city")
    private String city;

    @JsonProperty("namePhone")
    private String namePhone;

    /*public enum Sex {
        MALE, FEMALE, UNKNOWN
    }*/
}
