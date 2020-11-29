package com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface TableService {
	/**
	 * 获得用户权限范围内的分析表类别
	 * @param imodule0:薪资 1：保险
	 * @return
	 * @throws GeneralException
	 */
	ArrayList getReportCategoryList(int imodule) throws GeneralException;
	
	/**
	 * 获取人员库组件复选list
	 * @param nbases
	 * @return
	 */
	ArrayList getNbaseNameList(ArrayList nbases) throws GeneralException;
	
	/**
	 * 获取人员库组件复选list
	 * @param nbaselist getNbaseNameList方法获取到的人员库map的集合 例：[{Pre:Usr,DBName:在职人员库},{...}]
	 * @param checkednbase 例：Usr,Oth
	 * @return
	 */
	ArrayList getNbaseCompList(ArrayList nbaselist,String checkednbase);
	
	/**
	 * 获得某薪资账套下的薪资项
	 * @param rsid 账套id
	 * @param itemTypes指标类别，以逗号分隔可以是多个 举例 ： A,N,D,M,AC （AC-->代码型指标）
	 * @return
	 * @throws GeneralException
	 */
	ArrayList getSalaryItemList(int salaryid, String itemTypes,String rsid) throws GeneralException;
	
	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param module 0:薪资 1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @return
	 * @throws GeneralException
	 */
	ArrayList getSalarySetList(int module, String queryText) throws GeneralException;
	
	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param view userview
	 * @param module 0:薪资 1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @param checkedIds
	 * @param limit
	 * @param page
	 * @return
	 * @throws GeneralException
	 */
	ArrayList getSalarySetList(UserView view, int module, String queryText, String checkedIds, int limit, int page) throws GeneralException;
	
	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param view userview
	 * @param module 0:薪资 1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @param checkedIds
	 * @param limit
	 * @param page
	 * @return
	 * @throws GeneralException
	 */
	ArrayList<HashMap> getSalarySetList(UserView view, int module,String queryText, int limit, int page)  throws GeneralException;

	/**
	 * 获取薪资分析表已选指标
	 * @param rsdtlid
	 * @param userView
	 * @return
	 */
	ArrayList getReportItemlist(String rsdtlid,UserView userView) throws GeneralException;

	/**
	 * 删除薪资分析数据
	 * @param module
	 * @param rsid
	 * @param rsdtlid
	 * @return 
	 */
	boolean deleteReportdetail(int module,String rsid,String rsdtlid);

	/**
	 * 删除薪资分析数据
	 * @param module
	 * @param rsid
	 * @param rsdtlid
	 */
	boolean saveBelongUnit(int rsid,int rsdtlid,String B0110,String rawType);

	/**
	 * 新增薪资分析数据
	 * @param paramJson
	 * 			paramJson:{
	 * 						"imodule"://薪资和保险区分标识  1：保险  否则是薪资,
	 * 						"rsid" :" ", //报表种类编号(加密)
	 * 						"rsdtlid ":" ", //报表编号(加密)为空时表示新增，不为空时表示修改
	 * 						"name ":" ",//报表名称
	 * 						"items ":" ",//已选指标  (保存、编辑初始化时用到)
	 * 						"nbase ":" ",//人员库 Usr,Ret,Oth (保存、编辑初始化时用到)
	 * 						"salaryids ":" ",//薪资账套号  (保存、编辑初始化时用到)
	 * 						"verifying ": " "//含审批数据(保存、编辑初始化时用到)
	 * 					  }
	 * @return 返回新建后的rsdtlid;
	 */
	int insertReportdetail(JSONObject paramJson,UserView userview);
	
	/**
	 * 更新薪资分析数据
	 * @param paramJson
	 * 			paramJson:{
	 * 						"imodule"://薪资和保险区分标识  1：保险  否则是薪资,
	 * 						"rsid" :" ", //报表种类编号(加密)
	 * 						"rsdtlid ":" ", //报表编号(加密)为空时表示新增，不为空时表示修改
	 * 						"name ":" ",//报表名称
	 * 						"items ":" ",//已选指标  (保存、编辑初始化时用到)
	 * 						"nbase ":" ",//人员库 Usr,Ret,Oth (保存、编辑初始化时用到)
	 * 						"salaryids ":" ",//薪资账套号  (保存、编辑初始化时用到)
	 * 						"verifying ": " "//含审批数据(保存、编辑初始化时用到)
	 * 					  }
	 * @return
	 */
	public boolean updateReportdetail(JSONObject paramJson,UserView userview);
	
	/**
	 * 获取薪资分析页面设置内容
	 * @param userview
	 * @param ctrlParam 
	 * @param nbase
	 * @param salaryids
	 * @param verifying
	 * @return xml
	 */
	String getCtrlParamXml(UserView userview, String ctrlParam,String nbase,String salaryids,String verifying);
	
	/**
	 * 获取ctrlParam中的参数bean
	 * @return LazyDynaBean 
	 */
	LazyDynaBean getCtrlParamBean(int rsid,int rsdtlid);
	
	/**
	 * 从数据库中获取CtrlParam
	 * @return
	 */
	String getCtrlParamStr(int rsid,int rsdtlid);
	
	/**
	 * 获取Reportdetail中的参数bean
	 * @return LazyDynaBean 
	 * @throws GeneralException 
	 */
	LazyDynaBean getReportdetailBean(int module,int rsid,int rsdtlid) throws GeneralException;
	
	/**
	 * 删除已选指标
	 * @param rsdtlid 薪资分析表id
	 * @return boolean 成功true，否则false
	 * @throws SQLException
	 */
	boolean deleteSelectItems(int rsdtlid) throws SQLException;
	
	/**
	 * 新增已选指标
	 * @param items 指标，格式:"a0104,a0z23..."
	 * @param rsdtlid 薪资分析表id
	 * @param rsid 
	 * @return boolean 成功true，否则false
	 * @throws SQLException
	 */
	boolean insertSelectItems(String items,int rsdtlid, String rsid) throws SQLException;


	/**
	 * 得到表格列
	 * @param opt
	 * @return
	 */
	ArrayList<ColumnsInfo> getSalarySetColumnsInfo(int opt);
}
