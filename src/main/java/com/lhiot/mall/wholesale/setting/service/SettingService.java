package com.lhiot.mall.wholesale.setting.service;

import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingService {

    private final SettingMapper settingMapper;

    @Autowired
    public SettingService(SettingMapper settingMapper) {
        this.settingMapper = settingMapper;
    }

    public ParamConfig searchConfigParam(String key){
        return settingMapper.searchConfigParam(key);
    }
}
