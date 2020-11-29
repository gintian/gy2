package com.hjsj.hrms.module.system.regothersys;

import com.hjsj.hrms.service.core.util.ServiceType;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysRegGetService extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		List<Map<String, String>> beanList= new ArrayList<Map<String,String>>();
		Map<String, String> orgMap = new HashMap<String, String>();
		orgMap.put("serviceMethod", ServiceType.ORG);
		orgMap.put("serviceName", "获取机构数据");
		orgMap.put("dataScope", "1");
		beanList.add(orgMap);
		
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("serviceMethod", ServiceType.POST);
		postMap.put("serviceName", "获取岗位数据");
		postMap.put("dataScope", "1");
		beanList.add(postMap);
		
		Map<String, String> empMap = new HashMap<String, String>();
		empMap.put("serviceMethod", ServiceType.EMP);
		empMap.put("serviceName", "获取人员数据");
		empMap.put("dataScope", "1");
		beanList.add(empMap);
		
		Map<String, String> matterMap = new HashMap<String, String>();
		matterMap.put("serviceMethod", ServiceType.MATTER);
		matterMap.put("serviceName", "获取待办数据");
		matterMap.put("dataScope", "0");
		beanList.add(matterMap);
		
		Map<String, String> boardMap = new HashMap<String, String>();
		boardMap.put("serviceMethod", ServiceType.BOARD);
		boardMap.put("serviceName", "获取公告数据");
		boardMap.put("dataScope", "0");
		beanList.add(boardMap);
		
		Map<String, String> warnMap = new HashMap<String, String>();
		warnMap.put("serviceMethod", ServiceType.WARN);
		warnMap.put("serviceName", "获取预警数据");
		warnMap.put("dataScope", "0");
		beanList.add(warnMap);
		
		Map<String, String> staticsMap = new HashMap<String, String>();
		staticsMap.put("serviceMethod", ServiceType.STATICS);
		staticsMap.put("serviceName", "获取常用统计数据");
		staticsMap.put("dataScope", "0");
		beanList.add(staticsMap);
		
		Map<String, String> reportMap = new HashMap<String, String>();
		reportMap.put("serviceMethod", ServiceType.REPORT);
		reportMap.put("serviceName", "获取报表数据");
		reportMap.put("dataScope", "0");
		beanList.add(reportMap);
		
		Map<String, String> processMap = new HashMap<String, String>();
		processMap.put("serviceMethod", ServiceType.PROCESS);
		processMap.put("serviceName", "更新信息集");
		processMap.put("dataScope", "0");
		beanList.add(processMap);
		
		Map<String, String> kqInfoMap = new HashMap<String, String>();
		kqInfoMap.put("serviceMethod", ServiceType.KQINFO);
		kqInfoMap.put("serviceName", "获取考勤报批数据");
		kqInfoMap.put("dataScope", "0");
		beanList.add(kqInfoMap);
		
		Map<String, String> getHolidayMap = new HashMap<String, String>();
		getHolidayMap.put("serviceMethod", ServiceType.GET_HOLIDAY);
		getHolidayMap.put("serviceName", "获取年假天数（已休、可休）");
		getHolidayMap.put("dataScope", "0");
		beanList.add(getHolidayMap);
		
		Map<String, String> updHolidayMap = new HashMap<String, String>();
		updHolidayMap.put("serviceMethod", ServiceType.UPD_HOLIDAY);
		updHolidayMap.put("serviceName", "更新年假天数");
		updHolidayMap.put("dataScope", "0");
		beanList.add(updHolidayMap);
		
		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("serviceMethod", ServiceType.USERETOKEN);
		tokenMap.put("serviceName", "获取用户登录标识");
		tokenMap.put("dataScope", "0");
		beanList.add(tokenMap);
		
		net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(beanList);//将封装结果转换json格式
		this.getFormHM().put("data", jsonArray);//最终指标json串
	}
}
