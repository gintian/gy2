package com.hjsj.hrms.module.gz.mysalary.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的薪酬配置业务接口类
 * @author wangb 
 * @category hjsj 2019-02-13
 * @version 1.0
 *
 */
public interface MySalaryService {

	
	
	
	/**
	 * 获取权限范围内的薪酬方案
	 * @param userView 
	 * @return 薪酬方案集合
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	List listMySalaryScheme(UserView userView) throws GeneralException;
	
	/**
	 * 获取我的薪酬方案
	 * @param id 方案id号   加密
	 * @param userView 
	 * @return 薪酬方案详细信息
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	HashMap getMySalaryScheme(String id, UserView userView) throws GeneralException;
	
	
	
	/**
	 * 保存我的薪酬方案
	 * @param data 我的薪酬方案 参数数据
	 * @return 成功返回 数据  失败 抛出异常
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	HashMap saveMySalaryScheme(Map data, UserView userView) throws GeneralException;
	
	
	/**
	 * 删除我的薪酬方案
	 * @param ids 多个方案 中间逗号间隔  加密的id
	 * @return   successs 成功 | 抛出异常
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	String deleteMySalaryScheme(String id) throws GeneralException;
	
	/**
	 * 获取薪酬视图指标
	 * @param 薪酬视图表名
	 * @return 指标集合
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	HashMap getMySalaryViewField(String salary_table) throws GeneralException;
	
	/**
	 * 校验薪酬视图视图表是否存在   
	 * @param salary_table_name 视图名称
	 * @param salary_table  视图表名
	 * @return
	 * @author wangb 2019-02-14
	 * @throws GeneralException
	 */
	String checkSalaryViewTable(String salary_table_name, String salary_table) throws GeneralException;
	
	/**
	 * 创建视图获取子集和人员库信息
	 * @return
	 * @author wangb 2019-02-14
	 * @throws GeneralException
	 */
	HashMap getSalaryViewParam(UserView userView) throws GeneralException;
	
	
	/**
	 * 保存视图
	 * @param data 视图参数
	 * @param type 操作类型   add 新增视图 update 修改视图
	 * @param userView
	 * @return 返回视图 表和指标数据
	 * @author wangb 2018-02-14
	 * @throws GeneralException
	 */
	HashMap saveSalaryView(HashMap data, String type, UserView userView) throws GeneralException, SQLException;
	

	/**
	 * 获取视图
	 * @param view
	 * @return
	 * @throws GeneralException
	 */
	HashMap getViewData(String view) throws GeneralException;
	
	/**
	 * 获取我的薪酬信息（年|月）  北辰2开接口
	 * @param userView
	 * @param id   方案id号  加密
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 * @return 薪资信息
	 * @author wangb 2019-02-13
	 * @throws GeneralException
	 */
	HashMap getMySalaryInfo(UserView userView, String id, String startDate, String endDate) throws GeneralException;

	/**
	 * 获取我的薪酬信息（年）
	 * @param nbase
	 * @param a0100
	 * @return 薪资信息
	 * @parem state  self | emply  本人 | 员工 区分查看自己薪酬 还是员工薪酬   查看本人薪酬走 员工特征角色指标权限 
	 * @throws GeneralException
	 */
	HashMap getMySalaryInfo(UserView userView, String nbase, String a0100, String year, String schemeId, String state) throws GeneralException;
	
	/**
	 * 获取我的薪酬主页面数据
	 * @param userView 
	 * @return
	 * @author wangb 2019-02-25
	 * @throws GeneralException
	 */
	Map getMySalaryData(UserView userView, String id) throws GeneralException;
	
	/**
	 * 获取某年每月主页面数据 
	 * @param id 我的薪酬方案 id 加密
	 * @param userView
	 * @param year 年份
	 * @return
	 * @author wangb 2019-02-25
	 * @throws GeneralException
	 */
	Map getMySalaryMonthData(UserView userView, String id, String year) throws GeneralException;
	
	/**
	 * 获取我的薪酬月明细
	 * @param userView
	 * @param id   方案id号  加密
	 * @param year 年份
	 * @param month 月份
	 * @return 薪资信息
	 * @author wangb 2019-02-25
	 * @throws GeneralException
	 */
	Map getMySalaryMonthInfo(UserView userView, String id, String year, String month) throws GeneralException;
	
	/**
	 * 获取我的薪酬年度各月工资
	 * @param userView
	 * @param id 方案编号 id 加密
	 * @param year 年份
	 * @return
	 * @author wangb 2019-02-25
	 * @throws GeneralException
	 */
	Map getMySalaryYearInfo(UserView userView, String id, String year) throws GeneralException;
	
	/**
	 * 获取历史年度工资明细
	 * @param userView
	 * @param id 方案编号
	 * @return
	 * @author wangb 2019-02-25
	 * @throws GeneralException
	 */
	Map getMySalaryHistoryInfo(UserView userView, String id) throws GeneralException;
	
	/**
	 * 获取员工信息
	 * @param nbase 人员库
	 * @param a0100 人员编号
	 * @return
	 * @throws GeneralException
	 */
	UserView getEmployeeSalaryInfo(String nbase, String a0100) throws GeneralException;

	/**
	* @Description: 薪酬方案排序
	* @Param: [sortItem]
	* @return: void
	* @Author: Liuyd
	* @Date: 2020/7/16
	*/
	void saveNorder(String sortItem)throws GeneralException;

	/**
	* @Description: 根据子集id获取该子集下的数值型指标
	* @Param: [fieldSetId]
	* @return: java.util.List
	* @Author: Liuyd
	* @Date: 2020/7/17
	*/
	List searchNumberFieldItem(String fieldSetId, String flag)throws GeneralException;

	/**
	* @Description: 校验计算公式内容
	* @Param: [userView, c_expr, itemType, fieldSetId]
	* @return: java.lang.String
	* @Author: Liuyd
	* @Date: 2020/7/20
	*/
	String checkFormula(UserView userView, String c_expr, String itemType, String fieldSetId)  throws GeneralException;
	
	/**
	 * 获取我的薪酬最大和最小值日期
	 * @throws GeneralException
	 * @author wangbo
	 * @Date: 2020/08/07
	 */
	HashMap getMySalarySchemeMaxAndMinDate(String salary_table, String salary_date, String nbase, String a0100) throws GeneralException;
}
