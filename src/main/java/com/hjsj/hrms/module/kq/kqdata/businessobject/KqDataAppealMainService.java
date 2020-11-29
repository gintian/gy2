package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.HashMap;


/**
 * 数据上报 业务类 接口
 * create time 201811/15
 * @author wangbo
 *
 */
public interface KqDataAppealMainService {


	/**
	 * 获取 全部|待办  考勤上报数据   分页显示 
	 * @param status 考勤上报方案状态  全部 | 待办 01 not null
	 * @param scheme_id 考勤方案 允许为null
	 * @param kq_year 考勤年份  允许为null
	 * @param kq_duration 考勤区间  允许为null 
	 * @param org_id 考勤机构id 允许为null
	 * @param currentPage 当前页签 not null
	 * @param pageSize 每页显示数 not null
	 * @return 
	 * 数据格式 ：{
	 * 		kq_year:"",考勤年份
	 * 		org_list:[{},{...}]考勤上报数据
	 * 		year_list:[2018,.。。] 考勤年份
	 *    }
	 * @author wangbo
     * @date 11:29 2018/11/7
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
	 */
	HashMap  listKqDataAppeal(String status, String scheme_id ,String kq_year,String kq_duration,String org_id,int currentPage ,int pageSize) throws GeneralException;
	
	/**
	 * 确认考勤发布功能
	 * @param scheme_id  考勤方案id
	 * @param kq_year	考勤年份
	 * @param kq_duration 考勤期间 
	 * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
	 * @author wangbo
     * @date 11:29 2018/11/12
	 */
	void releaseKqData(int scheme_id, String kq_year, String kq_duration,String org_id) throws GeneralException;

	/**
	 * 更新考勤确认状态，和待办状态
	 * @param scheme_id 考勤方案id
	 * @param kq_year   考勤年份  
	 * @param kq_duration 考勤期间
	 * @param org_id   机构编号
	 * @param flag  更新的状态
	 * @return 更新成功true, false 更新失败
	 * @throws GeneralException
	 * @author wangbo
     * @date 11:29 2018/11/13
	 */
	boolean updateKqConfirm(int scheme_id, String kq_year, String kq_duration,String org_id,String flag,String confirmMemo) throws GeneralException;
	
	
	/**
	 * 获取具体某人的某月考勤数据明细
	 * @param scheme_id 考勤方案编号
	 * @param kq_year   考勤年份
	 * @param kq_duration 考勤期间
	 * @param org_id    考勤机构
	 * @return
	 * @throws GeneralException
	 * @author wangbo
     * @date 11:29 2018/11/13
	 */
	String getKqConfirmLetter(int scheme_id, String kq_year, String kq_duration,String org_id) throws GeneralException;
   /**
	    * 考勤月明细表 guidkey 字段查询 A01表用户 所在人员库，A0100 和 登陆账号和密码
     *
     * @param scheme_id   考勤方案编号
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      考勤机构编号
     * @return 人员Map  key  guidkey
     * @throws GeneralException
     */
	HashMap getA01Items(int scheme_id, String kq_year, String kq_duration, String org_id) throws GeneralException;
}
