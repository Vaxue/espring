package com.wfx.espring.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wfx.espring.demo.service.IQueryService;
import com.wfx.espring.framework.annotation.EService;

/**
 * 查询业务
 * @author Tom
 *
 */
@EService
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		//log.info("这是在业务方法中打印的：" + json);
		return json;
	}

}
