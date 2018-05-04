package com.lhiot.mall.wholesale.faq.service;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.faq.mapper.FaqMapper;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FaqService {

    private final FaqMapper faqMapper;

    @Autowired
    public FaqService(FaqMapper faqMapper) {
        this.faqMapper = faqMapper;
    }

    public List<Faq> searchFaq(long faqCategoryId){
        return faqMapper.searchFaq(faqCategoryId);
    }

    /**
     * 查询详情
     * @return
     */
    public Faq detail(Long id) {
        return faqMapper.select(id);
    }

    public int saveOrUpdateFaq(Faq faq) {
        if (faq.getId()>0){
            return faqMapper.updateFaq(faq);
        }else {
            //FIXME 自增后不需要再setId
            //faq.setId(faqMapper.pageQueryCount()+1);
            return faqMapper.insertFaq(faq);
        }
    }

    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(FaqGridParam param){
        PageQueryObject result = new PageQueryObject();
        int count = faqMapper.pageQueryCount();
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
        List<Faq> faqList = faqMapper.pageQuery(param);
        result.setRows(faqList);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }


}
