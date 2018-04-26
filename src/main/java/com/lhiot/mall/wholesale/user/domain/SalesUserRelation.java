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
public class SalesUserRelation {

    @JsonProperty("id")
    private long id;

    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("salesman_id")
    private long salesmanId;

    @JsonProperty("is_check")
    private Integer check;
}
