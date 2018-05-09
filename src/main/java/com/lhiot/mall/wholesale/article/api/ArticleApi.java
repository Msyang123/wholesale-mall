package com.lhiot.mall.wholesale.article.api;

import com.lhiot.mall.wholesale.article.domain.Article;
import com.lhiot.mall.wholesale.article.domain.gridparam.ArticleGridParam;
import com.lhiot.mall.wholesale.article.service.ArticleService;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.introduction.service.IntroductionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@Api(description = "新闻文章接口")
@Slf4j
@RestController
public class ArticleApi {

    private final ArticleService articleService;

    @Autowired
    public ArticleApi(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/article/{id}")
    @ApiOperation(value = "后台管理系统--根据ID查询新闻文章", response = Introduction.class)
    public ResponseEntity<Article> introduction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(articleService.article(id));
    }

    @PostMapping("/article/grid")
    @ApiOperation(value = "后台管理系统--新建一个查询，分页查询新闻文章", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) ArticleGridParam param) {
        return ResponseEntity.ok(articleService.pageQuery(param));
    }

    @PutMapping("/article/addorupdate")
    @ApiOperation("后台管理系统---新增/修改新闻文章")
    public ResponseEntity saveAddress(@RequestBody Article article) {
        article.setCreateTime(new Timestamp(new Date().getTime()));
        if (articleService.saveOrUpdateArticle(article)>0) {
            return ResponseEntity.ok().body("新增/修改完成");
        }
        return ResponseEntity.badRequest().body("添加失败");
    }
}