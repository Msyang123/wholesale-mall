package com.lhiot.mall.wholesale.user.domain;

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
public class UserAddress {

    @JsonProperty("id")
    private long id;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String addressDetail;

    @JsonProperty("city")
    private String addressArea;

    @JsonProperty("isDefault")
    private String isDefault;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("name")
    private String userName;
}
