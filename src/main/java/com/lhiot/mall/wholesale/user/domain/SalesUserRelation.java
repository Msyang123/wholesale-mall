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
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("salesman_id")
    private Long salesmanId;

    @JsonProperty("is_check")
    private Integer check;
}
