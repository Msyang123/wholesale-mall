package com.lhiot.mall.wholesale.base;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "分页查询结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageQueryObject {
    @ApiModelProperty(notes = "记录的总条数", dataType = "Integer")
    private Integer total;

    @ApiModelProperty(notes = "记录页数", dataType = "Integer")
    private Integer page;

    @ApiModelProperty(notes = "展示行数", dataType = "Integer")
    private Integer records;

    @ApiModelProperty(notes = "记录详情", dataType = "List")
    private List<?> rows;
}
