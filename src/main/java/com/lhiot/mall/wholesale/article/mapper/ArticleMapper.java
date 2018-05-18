package com.lhiot.mall.wholesale.article.mapper;

import com.lhiot.mall.wholesale.article.domain.Article;
import com.lhiot.mall.wholesale.article.domain.gridparam.ArticleGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper {

    Article select(long id);

    //分页查询新品需求
    List<Article> pageQuery(ArticleGridParam param);

    //查询分类的总记录数
    int pageQueryCount(ArticleGridParam param);

    //查询分类的总记录数
    int pageQueryCount();

    int updateArticle(Article article);

    int insertArticle(Article article);

    //后台管理系统 批量删除
    void removeInbatch(List<Long> ids);

    List<Article> articleCategory();

    List<Article> articles(Article article);

}