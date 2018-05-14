package com.lhiot.mall.wholesale.setting.mapper;

import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SettingMapper {

    ParamConfig searchConfigParam(String key);
}
