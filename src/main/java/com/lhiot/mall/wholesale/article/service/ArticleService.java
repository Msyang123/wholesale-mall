package com.lhiot.mall.wholesale.article.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.article.domain.Article;
import com.lhiot.mall.wholesale.article.domain.gridparam.ArticleGridParam;
import com.lhiot.mall.wholesale.article.mapper.ArticleMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.introduction.mapper.IntroductionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ArticleService {


    private final SnowflakeId snowflakeId;

    private final ArticleMapper articleMapper;


    @Autowired
    public ArticleService(ArticleMapper articleMapper, SnowflakeId snowflakeId) {
        this.articleMapper = articleMapper;
        this.snowflakeId = snowflakeId;
    }


    public Article article(long id) {
        return articleMapper.select(id);
    }

    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(ArticleGridParam param){
        int count = articleMapper.pageQueryCount(param);
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
        List<Article> introductionList = articleMapper.pageQuery(param);
        PageQueryObject result = new PageQueryObject();
        result.setRows(introductionList);
        result.setPage(page);
        result.setRecords(rows);
         result.setTotal(totalPages);
        return result;
    }

    //新增/修改服务协议
    public int saveOrUpdateArticle(Article article) {
        if (article.getId()>0){
            return articleMapper.updateArticle(article);
        }else {
            article.setId(articleMapper.pageQueryCount()+1);
            return articleMapper.insertArticle(article);
        }
    }

}
