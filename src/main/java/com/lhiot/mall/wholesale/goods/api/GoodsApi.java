package com.lhiot.mall.wholesale.goods.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhiot.mall.wholesale.goods.service.GoodsService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "测试")
@RestController
@RequestMapping("/good")
public class GoodsApi {
	
	private GoodsService goodsService;
	
	public GoodsApi(GoodsService goodsService){
		this.goodsService = goodsService;
	}
}
