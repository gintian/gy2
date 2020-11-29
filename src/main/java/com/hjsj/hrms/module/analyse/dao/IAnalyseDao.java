package com.hjsj.hrms.module.analyse.dao;

import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * 统计分析 数据库接口
 * @author wangbo 
 * @category hjsj 2019-12-16
 * @version 1.0
 */
public interface IAnalyseDao {

	/**
	 * 获取统计年份集合
	 * @param viewTable 数据视图表名
	 * @param b0110  xxx,xxx 机构id编号 中间逗号间隔
	 * @return
	 * @throws GeneralException
	 */
	List listViewYear(String viewTable, String b0110) throws GeneralException;
	
	/**
	 * 获取机构集合
	 * @param viewTable 数据视图表名
	 * @param b0110 xxx,xxx 机构id编号 中间逗号间隔
	 * @return
	 * [{
	 * 	value:xxx, // 机构编号 加密  PubFunc 加密
 	 * 	text:xxx  // 机构名称
	 * },{...}]
	 * @throws GeneralException
	 */
	List<Map> listOrgData(String viewTable, String b0110) throws GeneralException;
	
	
	/**
	 * 获取占比统计数据集合
	 * @param viewTable 数据视图表名
	 * @param items   xxx,xxx  分子指标， 分母指标
	 * @param b0110   xxx,xxx 机构id编号，多个机构逗号间隔 解密
	 * @param year    xxxx  当前年份  值为null 取最近年份
	 * @return 
	 * [{
	 *   b0110:xxx,//加密
	 *   itemname:xxx, //机构名称
	 *   xxx:xxx,  //分子指标值
	 *   xxx:xxx,  //分母指标值
	 * },...]
	 * @throws GeneralException
	 */
	List<Map> listZhanBiViewData(String viewTable, String items, String b0110, String year) throws GeneralException;
	
	
	/**
	 * 获取当前年度和上个年度的同比统计数据
	 * @param viewTable  数据视图表名
	 * @param items xxx  指标值
	 * @param b0110 xxx,xxx 机构id编号，多个机构逗号间隔 解密
	 * @param year 当前年份  值为null 取最近年份
	 * @return
	 * [{
	 *   itemname:'xxx',//机构名称
	 *   b0110:'xxx',//机构编号 加密
	 *   dataList:[{
	 *   	name:'xxxx',// 当前年度
	 *      value:'xxxx'
	 *   },{
	 *      name:'xxxx',// 上个年度
	 *      value:'xxxx'
	 *   }] 
	 * },...]
	 * @throws GeneralException
	 */
	List<Map> listTongBiViewData(String viewTable, String items, String b0110, String year) throws GeneralException;
	
	
	/**
	 * 获取当前年度和上个年度的平均统计数据
	 * @param viewTable  数据视图表名
	 * @param b0110 xxx,xxx 机构id编号，多个机构逗号间隔 解密
	 * @param year 当前年份  值为null 取最近年份
	 * @return 
	 * [{
	 *   itemid:'xxx',//分类id
	 *   itemname:'xxx',//分类名称
	 *   dataList:[{
	 *   	name:'xxxx',// 当前年度
	 *      value:'xxxx'
	 *   },{
	 *      name:'xxxx',// 上个年度
	 *      value:'xxxx'
	 *   }] 
	 * },...]
	 * @throws GeneralException
	 */
	List<Map> listPingJunViewData(String viewTable, String b0110, String year) throws GeneralException;
	
	/**
	 * 获取当前年度累计统计数据
	 * @param viewTable  数据视图表名
	 * @param b0110 xxx,xxx 机构id编号，多个机构逗号间隔 解密
	 * @param year 当前年份  值为null 取最近年份
	 * @return
	 * {
	 * sum:xx,
	 * data:[{
	 *     itemid:'xxx',//分类id
	 *     itemname:'xxx',//分类名称
	 *     value:'xxx' // 累计值
	 * },...]
	 * }
	 * @throws GeneralException
	 */
	Map listLeiJiViewData(String viewTable,String b0110,String year) throws GeneralException;
	
	
	/**
	 * 获取多项目多累计统计数据
	 * @param viewTable  数据视图表名
	 * @param b0110 xxx,xxx 机构id编号，多个机构逗号间隔 解密
	 * @param year 当前年份  值为null 取最近年份
	 * @return
	 * [{
	 *   itemname:'xxx',// 项目名称
	 *   dataList:[{
	 *   	name:'xxxx',// 分类名称
	 *      value:'xxxx'  
	 *   },{
	 *      name:'xxxx',
	 *      value:'xxxx'
	 *   }] 
	 * },...] 
	 * @throws GeneralException
	 */
	List<Map> listMoreItemAndMoreTypeViewData(String viewTable, String b0110, String year) throws GeneralException;
	
}
