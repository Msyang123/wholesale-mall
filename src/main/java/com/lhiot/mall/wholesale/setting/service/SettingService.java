package com.lhiot.mall.wholesale.setting.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamConfigGirdParam;
import com.lhiot.mall.wholesale.setting.mapper.SettingMapper;

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

	/**
	 * 新增
	 * @param param
	 * @return
	 */
	public boolean create(ParamConfig param){
		return settingMapper.insert(param)>0;
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
		settingMapper.delete(list);
	}

	/**
	 * 修改
	 * @param param
	 * @return
	 */
	public boolean update(ParamConfig param){
		return settingMapper.update(param)>0;
	}

	/**
	 * 根据id查询商品版块
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public ParamConfig paramConfig(Long id) {
		return settingMapper.select(id);
	}
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(ParamConfigGirdParam param){
		int count = settingMapper.pageQueryCount(param);
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
		List<ParamConfig> plateCategorys = settingMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(plateCategorys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
}
