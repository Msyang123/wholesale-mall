package com.lhiot.mall.wholesale.goods.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.lhiot.mall.wholesale.goods.domain.SearchHistory;
import com.lhiot.mall.wholesale.goods.domain.SearchHistoryParam;
import com.lhiot.mall.wholesale.goods.mapper.SearchHistoryMapper;

/**SearchHistoryService
 * 搜索历史记录中心
 * @author lynn
 *
 */
@Service
@Transactional
public class SearchHistoryService {
	
	private final SearchHistoryMapper searchHistoryMapper;
	
	@Autowired
	public SearchHistoryService(SearchHistoryMapper searchHistoryMapper){
		this.searchHistoryMapper = searchHistoryMapper;
	}
	
	/**
	 * 写入搜索历史
	 * @param searchHistory
	 * @return
	 */
	public boolean create(SearchHistoryParam searchHistory){
		boolean success = true;
		int count = searchHistoryMapper.searchByKeyword(searchHistory);
		if(count==0){
			success = searchHistoryMapper.insert(searchHistory)>0;
		}
		return success;
	}
	
	/**
	 * 删除
	 * @param userId
	 */
	public boolean delete(Long userId){
		return searchHistoryMapper.remove(userId)>0;
	}
	
	public List<SearchHistory> searchHistories(Long userId){
		return searchHistoryMapper.search(userId);
	}
}
