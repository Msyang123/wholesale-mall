package com.lhiot.mall.wholesale.faq.service;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.FaqCategoryTree;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.faq.mapper.FaqCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FaqCategoryService {

    private final FaqCategoryMapper faqCategoryMapper;

    @Autowired
    public FaqCategoryService(FaqCategoryMapper faqCategoryMapper) {
        this.faqCategoryMapper = faqCategoryMapper;
    }

    public int save(FaqCategory faqCategory) {
        return faqCategoryMapper.insertFaqCategory(faqCategory);
    }

    public int update(FaqCategory faqCategory) {
        return faqCategoryMapper.update(faqCategory);
    }
    
    /**
     * 批量删除
     * @param ids
     */
    public void delete(String ids){
        if(StringUtils.isBlank(ids)){
            return ;
        }
        List<Long> list = Arrays.asList(ids.split(",")).stream()
                .map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        faqCategoryMapper.delete(list);
    }
    
    //后台管理系统----FAQ分类下拉框
    public List<FaqCategory> searchFaqCategory() {
        return faqCategoryMapper.searchFaqCategory();
    }

    /**
     * 树结构
     * @return
     */
    public List<FaqCategoryTree> tree(){
    	return faqCategoryMapper.ztree();
    }
    
    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(FaqGridParam param){
        PageQueryObject result = new PageQueryObject();
        int count = faqCategoryMapper.pageQueryCount();
        int page = param.getPage();
        int rows = param.getRows();
        //起始行
        param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            page = 1;
            param.setPage(page);
            param.setStart(0);
        }
        List<FaqCategory> faqList = faqCategoryMapper.pageQuery(param);
        result.setRows(faqList);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }
}
