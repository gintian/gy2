package com.hjsj.hrms.module.muster.showmuster.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 浏览花名册业务接口类
 * @category hjsj
 * @author wangb 2019-02-21
 * @version 1.0
 *
 */
public interface ShowManageService {	
	/**
	 * 获取花名册页面设置信息
	 * @param tabid 花名册id 加密
	 * @return
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	Map getMusterPageConfig(String tabid) throws GeneralException;
	
	/**
	 * 保存花名册页面设置信息
	 * @param data 页面设置参数
	 * @param userView
	 * @return
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	Map saveMusterPageConfig(Map data) throws GeneralException;
	
	/**
	 * 获取花名册展现页面的buttonlist
	 * @return 按钮的list集合
	 * @throws GeneralException
	 */
	ArrayList getButtonList(String musterType)throws GeneralException;
	/**
	 * 获取列头
	 * @param tabid 花名册的ID
	 * @param musterType 1:人员花名册 2：单位花名册 3：岗位花名册  4：基准岗位花名册
	 * @return ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 */
	ArrayList<ColumnsInfo> getColumnList(String tabid,String musterType)throws GeneralException;
	/**
	 * 获取展现/预览页面的sql
	 * @param tabid 花名册 id
	 * @param moduleId 0：员工管理 ;1:组织机构 
	 * @param musterType 1:人员花名册 2：单位花名册 3：岗位花名册  4：基准岗位花名册
	 * @param parMap 过滤数据的参数
	 * @return sql
	 * @throws GeneralException
	 */
	String getDataSql(String tabid,String moduleId,String musterType,HashMap<String, Object>  parMap)throws GeneralException;
	/**
	 * 获取排序的sql
	 * @param tabid 花名册id
	 * @param sortField 排序字段
	 * @param musterType 1:人员花名册 2：单位花名册 3：岗位花名册  4：基准岗位花名册
	 * @return sql  
	 * @throws GeneralException
	 */
	String getOrderBySql(String tabid,String sortField,String musterType)throws GeneralException;
	/**
	 * 根据tabid获取名称
	 * @param tabid 花名册的id
	 * @return 花名册名称
	 * @throws GeneralException
	 */
	String getMusterName(String tabid)throws GeneralException;

	/**
	 * 格式化处理ROW_NUMBER()函数排序条件语句
	 * @param fieldlist 已选指标
	 * @param sortField 排序指标
	 * @param sortFlag 是否排序标志，0：不排序；1：排序
	 * @param flag01 是否包含主集标志，true：包含；false：不包含
	 * @param musterType 访问类型
	 * @param readType 查看类型 0：打开查看 1：预览查看
	 * @return
	 */
	String orderBySqlFormat(ArrayList<String> fieldlist, String sortField, String sortFlag,boolean flag01,String musterType,String readType) throws GeneralException;

	/**
	 * 判断主集类型
	 * @param musterType
	 * @return
	 */
	String getSqlX0100(String musterType) throws GeneralException;
    /**
     * 导出常用花名册(简单花名册Excel）
     * 此方法为对其他模块提供的接口，
     * 调用时只控制子集的条件，其他的条件由whereSql控制，由其他模块传递
     * @Author xuchangshun
     * @param tabid 花名册模版id
     * @param musterType 花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
     * @param moduleId 模块id =0：员工管理；=1：组织机构；
     * @param nbase 人员库，目前考虑只传递一个
     * @param whereSql 过滤条件（人员过滤条件）举例 “ where A01......”
     * @param sortField 排序字段 不传递（人员花名册使用A0100,单位部门使用B0100,岗位使用E01A1）
     * @return java.lang.String
     * @throws
     * @Date 2020/2/4 15:47
     */
	String exportExcel(String tabid,String musterType,String moduleId,String nbase,String whereSql,String sortField) throws GeneralException;
}
