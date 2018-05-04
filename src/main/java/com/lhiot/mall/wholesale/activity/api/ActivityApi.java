package com.lhiot.mall.wholesale.activity.api;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.activity.domain.Activity;
import com.lhiot.mall.wholesale.activity.domain.ActivityType;
import com.lhiot.mall.wholesale.activity.domain.FlashActivityGoods;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.service.ActivityService;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@Api(description = "活动统一管理")
@RestController
@RequestMapping
public class ActivityApi {
	
	private final ActivityService activityService;
	
	@Autowired
	public ActivityApi(ActivityService activityService){
		this.activityService = activityService;
	}
	
    @PostMapping("/activity")
    @ApiOperation(value = "添加", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody Activity activity){
    	if(activityService.create(activity)){
    		return ResponseEntity.created(URI.create("/activity/"+activity.getId()))
    				.body(activity);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/activity/{id}")
    @ApiOperation(value = "根据ID修改", response = Advertise.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody Activity activity) {
        if (activityService.update(activity)) {
            return ResponseEntity.ok(activity);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/activity/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activity/{id}")
    @ApiOperation(value = "根据ID查询", response = Activity.class)
    public ResponseEntity<Activity> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(activityService.activity(id));
    }
    
    @PostMapping("/activity/gird")
    @ApiOperation(value = "新建一个查询，分页查询", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) ActivityGirdParam param) {
        return ResponseEntity.ok(activityService.pageQuery(param));
    }
    
	/*@GetMapping("/activity/trydelete/{ids}")
    @ApiOperation(value = "查询是否可以被删除")
    public ResponseEntity<Boolean> tryOperation(@PathVariable("ids") String ids) {
        return ResponseEntity.ok(activityService.canDelete(ids));
    }*/
	
	@PostMapping("/activity/tryoperation")
    @ApiOperation(value = "查询是否可以被修改或新增")
    public ResponseEntity<Boolean> tryoperation(@RequestBody(required = true) Activity activity) {
        return ResponseEntity.ok(activityService.allowOperation(activity));
    }
	
    @GetMapping("/activity/type/{type}")
    @ApiOperation(value = "根据活动类型查询当前开启的活动", response = Activity.class)
    public ResponseEntity<FlashActivityGoods> goodsUnit(@PathVariable("type") ActivityType type) {
        return ResponseEntity.ok(activityService.currentActivity(type));
    }
}
