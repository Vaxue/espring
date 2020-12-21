package com.wfx.espring.demo.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wfx.espring.demo.service.IModifyService;
import com.wfx.espring.demo.service.IQueryService;
import com.wfx.espring.demo.service.impl.ModifyService;
import com.wfx.espring.demo.service.impl.QueryService;
import com.wfx.espring.framework.annotation.EAutowired;
import com.wfx.espring.framework.annotation.EContoller;
import com.wfx.espring.framework.annotation.ERequestMapping;
import com.wfx.espring.framework.annotation.ERequestParam;
import com.wfx.espring.framework.webmvc.servlet.EModelAndView;

/**
 * 公布接口url
 * @author Tom
 *
 */
@EContoller
@ERequestMapping("/web")
public class MyAction {

	@EAutowired
	IQueryService queryService;
	@EAutowired
	IModifyService modifyService;

	@ERequestMapping("/query.json")
	public EModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@ERequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@ERequestMapping("/add*.json")
	public EModelAndView add(HttpServletRequest request, HttpServletResponse response,
							 @ERequestParam("name") String name, @ERequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new EModelAndView("500",model);
		}

	}
	
	@ERequestMapping("/remove.json")
	public EModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @ERequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@ERequestMapping("/edit.json")
	public EModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@ERequestParam("id") Integer id,
			@ERequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private EModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
