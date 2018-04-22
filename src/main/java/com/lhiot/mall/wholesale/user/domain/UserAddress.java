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

    @JsonProperty("address_detail")
    private String addressDetail;

    @JsonProperty("address_area")
    private String addressArea;

    @JsonProperty("is_default")
    private int isDefault;

    @JsonProperty("user_id")
    private Long userId;
}
