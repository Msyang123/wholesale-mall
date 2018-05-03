package com.lhiot.mall.wholesale.goods.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsDetailResult;
import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsInfo;
import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;
import com.lhiot.mall.wholesale.goods.domain.InventoryResult;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;
import com.lhiot.mall.wholesale.goods.service.GoodsPriceRegionService;
import com.lhiot.mall.wholesale.goods.service.GoodsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品信息")
@RestController
@RequestMapping
public class GoodsApi {
	
	private final GoodsService goodsService;

	private final GoodsPriceRegionService goodsPriceRegionService;
	
	@Autowired
	public GoodsApi(GoodsService goodsService, GoodsPriceRegionService goodsPriceRegionService){
		this.goodsService = goodsService;
        this.goodsPriceRegionService = goodsPriceRegionService;
    }
	
    @PostMapping("/goods")
    @ApiOperation(value = "添加商品单位", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody Goods goods){
    	if(goodsService.create(goods)){
    		return ResponseEntity.created(URI.create("/goods/"+goods.getId()))
    				.body(goods);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/goods/{id}")
    @ApiOperation(value = "根据ID修改商品", response = Goods.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody Goods goods) {
        if (goodsService.update(goods)) {
            return ResponseEntity.ok(goods);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/goods/{id}")
    @ApiOperation(value = "根据id批量删除商品")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/goods/{id}")
    @ApiOperation(value = "根据ID查询商品", response = Goods.class)
    public ResponseEntity<Goods> goods(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.goods(id));
    }
    
    @PostMapping("/goods/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) GoodsGirdParam param) {
        return ResponseEntity.ok(goodsService.pageQuery(param));
    }

    @PostMapping("/goodsDetail/{id}")
    @ApiOperation(value = "商品详情页面")
    public  ResponseEntity<GoodsDetailResult> goodsDetail(@PathVariable("id") Long id){
	    //商品详情信息
        GoodsInfo goodsInfo = goodsService.goodsInfo(id);
        //商品价格区间信息
        List<GoodsPriceRegion> goodsPriceRegions =goodsPriceRegionService.selectPriceRegion(goodsInfo.getGoodsStandardId());
        goodsInfo.setGoodsPriceRegionList(goodsPriceRegions);
        GoodsFlashsale goodsFlashsale = goodsService.goodsFlashsale(goodsInfo.getGoodsStandardId());
        //商品详情信息和抢购信息存放到GoodsDetailResult
        GoodsDetailResult goodsDetailResult = new GoodsDetailResult();
        goodsDetailResult.setGoodsInfo(goodsInfo);
        goodsDetailResult.setGoodsFlashsale(goodsFlashsale);
        return ResponseEntity.ok(goodsDetailResult);
    }

    @PostMapping("/inventoryList")
    @ApiOperation(value = "常用清单商品列表")
    public ResponseEntity<InventoryResult> inventoryList(@RequestParam("userId") Long userId, @RequestParam("plateId") Long plateId){
	    InventoryResult inventoryResult = new InventoryResult();
	    List<GoodsInfo> inventoryList = goodsService.inventoryList(userId);
        inventoryResult.setRecommendList(goodsService.recommendList(plateId));
        inventoryResult.setInventoryList(inventoryList);
	    return ResponseEntity.ok(inventoryResult);
    }

	@PostMapping("/goods/try-operation")
    @ApiOperation(value = "查询商品分类是否可以被修改或新增")
    public ResponseEntity<Boolean> tryOperation(@RequestBody(required = true) Goods goods) {
        return ResponseEntity.ok(goodsService.allowOperation(goods));
    }
	
	@GetMapping("/goods/keyword")
    @ApiOperation(value = "根据关键词查询商品列表" ,response= Goods.class,responseContainer="list")
    public ResponseEntity<List<Goods>> findGoodsByKeyword(@RequestParam(required = false) Long id,
    		@RequestParam(required = true) String keyword) {
        return ResponseEntity.ok(goodsService.findGoodsByKeyword(keyword, id));
    }
	
	@GetMapping("/goods/plate/{plateId}")
    @ApiOperation(value = "根据版块获取商品列表" ,response= PlateCategory.class)
    public ResponseEntity<PlateCategory> findGoodsByKeyword(@PathVariable("plateId") Long plateId) {
        return ResponseEntity.ok(goodsService.plateGoods(plateId));
    }
}
