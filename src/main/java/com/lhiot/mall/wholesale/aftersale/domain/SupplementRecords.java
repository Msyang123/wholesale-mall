package com.lhiot.mall.wholesale.aftersale.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("补差额记录")
@NoArgsConstructor
public class SupplementRecords {
	@ApiModelProperty(notes="id",dataType="Long")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	
	@ApiModelProperty(notes="优惠金额",dataType="Integer")
	private Integer orderDiscountFee;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Timestamp createAt;
}
