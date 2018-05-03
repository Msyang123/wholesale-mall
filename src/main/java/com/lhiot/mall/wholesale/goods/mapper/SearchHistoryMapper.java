package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.SearchHistory;
import com.lhiot.mall.wholesale.goods.domain.SearchHistoryParam;

@Mapper
public interface SearchHistoryMapper {

    int insert(SearchHistoryParam searchHistory);

    int remove(Long id);
    
    List<SearchHistory> search(Long userId);
    
    int searchByKeyword(SearchHistoryParam searchHistory);
}
