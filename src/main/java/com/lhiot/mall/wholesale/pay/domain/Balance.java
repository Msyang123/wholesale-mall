package com.lhiot.mall.wholesale.pay.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Balance {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdTime")
    private Timestamp time;

    @JsonProperty("type")
    private String type;

    @JsonProperty("money")
    private Integer fee;
}
