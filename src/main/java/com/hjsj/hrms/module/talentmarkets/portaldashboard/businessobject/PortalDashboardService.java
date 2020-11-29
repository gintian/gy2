package com.hjsj.hrms.module.talentmarkets.portaldashboard.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

/**
 * @Title PortalDashboardService
 * @Description 人才市场门户页面接口类
 * @Company hjsj
 * @Author wangbs、hanqh
 * @Date 2019/7/30
 * @Version 1.0.0
 */
public interface PortalDashboardService{
	/** 初始化查询所有数据 */
	String ALL = "all";
	/** 查询某些机构下的数据 */
	String ORG_DATA = "orgData";

	/**
	 * 初始化查询所有数据
	 * @author wangbs
	 * @return Map
	 * @throws GeneralException 抛出异常
	 */
	Map getAllData() throws GeneralException;

	/**
	 * 获取竞聘岗位chart的option
	 * @author wangbs
	 * @param orgIds 机构id
	 * @param conditionsSql 机构权限sql
	 * @return Map
	 * @throws GeneralException 抛出异常
	 */
	Map getCompePosChartOption(String orgIds, String conditionsSql) throws GeneralException;
}
