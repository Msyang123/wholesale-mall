package com.lhiot.mall.wholesale.introduction.service;

import com.leon.microx.util.SnowflakeId;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.introduction.mapper.IntroductionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class IntroductionService {


    private final SnowflakeId snowflakeId;

    private final IntroductionMapper introductionMapper;


    @Autowired
    public IntroductionService(IntroductionMapper introductionMapper, SnowflakeId snowflakeId) {
        this.introductionMapper = introductionMapper;
        this.snowflakeId = snowflakeId;
    }


    public Introduction introduction(long id) {
        return introductionMapper.select(id);
    }

    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(IntroductionGridParam param){
        int count = introductionMapper.pageQueryCount(param);
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
        List<Introduction> introductionList = introductionMapper.pageQuery(param);
        PageQueryObject result = new PageQueryObject();
        result.setRows(introductionList);
        result.setPage(page);
        result.setRecords(rows);
         result.setTotal(totalPages);
        return result;
    }

    //新增/修改服务协议
    public int saveOrUpdateIntroduction(Introduction introduction) {
        if (introduction.getId()>0){
            return introductionMapper.updateIntroduction(introduction);
        }else {
            introduction.setId(10l);
            return introductionMapper.insertIntroduction(introduction);
        }
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
        introductionMapper.removeInbatch(list);
    }
}
