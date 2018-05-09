package com.lhiot.mall.wholesale.activity.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.activity.domain.RewardConfig;
import com.lhiot.mall.wholesale.activity.domain.RewardCoupon;
import com.lhiot.mall.wholesale.activity.domain.gridparam.RewardConfigGridParam;
import com.lhiot.mall.wholesale.activity.service.RewardService;
import com.lhiot.mall.wholesale.base.PageQueryObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "活动奖励")
@RestController
@RequestMapping
public class RewardApi {
	
	private final RewardService rewardService;
	@Autowired
	public RewardApi(RewardService rewardService){
		this.rewardService = rewardService;
	}
	
    @PostMapping("/reward")
    @ApiOperation(value = "添加", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody RewardConfig rewardConfig){
    	if(rewardService.create(rewardConfig)){
    		return ResponseEntity.created(URI.create("/reward/"+rewardConfig.getId()))
    				.body(rewardConfig);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/reward/{id}")
    @ApiOperation(value = "根据ID修改", response = RewardCoupon.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody RewardConfig rewardConfig) {
        if (rewardService.update(rewardConfig)) {
            return ResponseEntity.ok(rewardConfig);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/reward/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	rewardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reward/{id}")
    @ApiOperation(value = "根据ID查询", response = RewardCoupon.class)
    public ResponseEntity<RewardCoupon> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rewardService.reward(id));
    }
    
    @PostMapping("/reward/grid")
    @ApiOperation(value = "新建一个查询，分页查询", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) RewardConfigGridParam param) {
        return ResponseEntity.ok(rewardService.pageQuery(param));
    }
}
