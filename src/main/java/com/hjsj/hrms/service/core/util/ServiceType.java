package com.hjsj.hrms.service.core.util;

/**
 * 定义服务标识,同表t_sys_reg_services中字段servicedetail相同 
 *
 */
public interface ServiceType {

	/**
	 * 获取机构数据
	 */
	public static final String ORG = "getOrg";
	
	/**
	 * 获取岗位数据
	 */
	public static final String POST = "getPost";
	
	/**
	 * 获取人员数据
	 */
	public static final String EMP = "getEmp";
	
	/**
	 * 获取待办数据
	 */
	public static final String MATTER = "getMatter";
	
	/**
	 * 获取公告数据
	 */
	public static final String BOARD = "getBoard";
	
	/**
	 * 获取预警数据
	 */
	public static final String WARN = "getWarn";
	
	/**
	 * 获取常用统计数据
	 */
	public static final String STATICS = "getStatics";
	
	/**
	 * 获取报表数据
	 */
	public static final String REPORT = "getReport";
	
	/**
	 * 更新信息集
	 */
	public static final String PROCESS = "syncProcess";
	
	/**
	 * 获取考勤报批数据
	 */
	public static final String KQINFO = "getKqInfo";
	
	/**
	 * 获取年假天数（已休、可休）
	 */
	public static final String GET_HOLIDAY = "getHoliday";
	
	/**
	 * 更新年假天数
	 */
	public static final String UPD_HOLIDAY = "updHoliday";
	
	/**
	 * 获取用户登录标识
	 */
	public static final String USERETOKEN = "getUserEtoken";
}
