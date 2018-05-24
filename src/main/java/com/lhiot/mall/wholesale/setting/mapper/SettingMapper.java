package com.lhiot.mall.wholesale.setting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamConfigGirdParam;

@Mapper
public interface SettingMapper {

    ParamConfig searchConfigParam(String key);

    int insert(ParamConfig param);

    int update(ParamConfig param);

    int delete(List<Long> ids);

    ParamConfig select(Long id);

    //分页查询分类
    List<ParamConfig> pageQuery(ParamConfigGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(ParamConfigGirdParam param);
}
