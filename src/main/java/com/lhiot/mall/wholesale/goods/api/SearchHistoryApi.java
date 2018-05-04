package com.lhiot.mall.wholesale.goods.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.goods.domain.SearchHistory;
import com.lhiot.mall.wholesale.goods.domain.SearchHistoryParam;
import com.lhiot.mall.wholesale.goods.service.SearchHistoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "搜索历史记录")
@RestController
@RequestMapping
public class SearchHistoryApi {
	
	private final SearchHistoryService searchHistoryService;
	
	@Autowired
	public SearchHistoryApi(SearchHistoryService searchHistoryService){
		this.searchHistoryService = searchHistoryService;
	}
	
    @PostMapping("/history")
    @ApiOperation(value = "添加搜索历史", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody SearchHistoryParam searchHistory){
    	if(searchHistoryService.create(searchHistory)){
    		return ResponseEntity.created(URI.create("/history/"+searchHistory.getId()))
    				.body(searchHistory);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }

    @DeleteMapping("/history/{id}")
    @ApiOperation(value = "根据用户id删除搜索记录")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(searchHistoryService.delete(userId));
    }

    @GetMapping("/history/search/{userId}")
    @ApiOperation(value = "根据用户id查询搜索列表", response = SearchHistory.class,responseContainer="list")
    public ResponseEntity<List<SearchHistory>> goodsUnit(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(searchHistoryService.searchHistories(id));
    }
}
