package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel
@NoArgsConstructor
public class InventoryResult {

    @JsonProperty("inventoryList")
    private List<GoodsInfo> inventoryList;

    @JsonProperty("recommendList")
    private List<GoodsInfo> recommendList;
}
