package com.lhiot.mall.wholesale.util;

import java.util.List;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.PageObject;

public class PageUtil {

	@SuppressWarnings("unchecked")
	public static <T> ArrayObject<PageObject> query(PageObject param,int count,List<T> list){
		int page = param.getPage();
		int rows = param.getRows();
		int totalPages = (count%rows==0?count/rows:count/rows+1);
		if(totalPages < page){
			param.setPage(1);//前段展示从第一页开始
		}
		PageObject obj = new PageObject();
		obj.setPage(param.getPage());
		obj.setRows(param.getRows());
		obj.setSidx(param.getSidx());
		obj.setSord(param.getSord());
		return ArrayObject.of(list, obj);
	}
}
