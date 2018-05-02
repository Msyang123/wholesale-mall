package com.lhiot.mall.wholesale.index.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhiot.mall.wholesale.index.domain.Index;
import com.lhiot.mall.wholesale.index.service.IndexService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "首页数据")
@RestController
@RequestMapping
public class IndexApi {
	
	private final IndexService indexService;
	
	@Autowired
	public IndexApi(IndexService indexService){
		this.indexService = indexService;
	}
    
    @GetMapping("/index")
    @ApiOperation(value = "根据类型查询广告", response = Index.class)
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(indexService.index());
    }
}
