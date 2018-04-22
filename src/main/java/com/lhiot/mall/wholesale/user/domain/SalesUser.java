package com.lhiot.mall.wholesale.user.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
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

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("union_id")
    private String unionId;

    @JsonProperty("salesman_name")
    private String salesmanName;

    @JsonProperty("salesman_phone")
    private String salesmanPhone;

    @JsonProperty("salesman_password")
    private String salesmanPassword;

    @JsonProperty("salesman_head_image")
    private String salesmanHeadimage;

    @JsonProperty("create_person")
    private int createPerson;

    @JsonProperty("create_at")
    private Timestamp createAt;

    @JsonProperty("invite_code")
    private String inviteCode;
}
