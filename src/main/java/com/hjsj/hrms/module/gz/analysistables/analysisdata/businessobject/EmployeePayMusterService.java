package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface EmployeePayMusterService {
	
	/**
	 * 得到人员列表表格列
	 * @param flag 
	 * @param userView 
	 * @param rsdtlid 
	 * @param rsid 
	 * @return
	 */
	ArrayList<ColumnsInfo> getEmployeePayMusterColumnsInfo(UserView userView, String rsid, String rsdtlid);

	/**
	 * 得到人员列表的工具栏
	 * 
	 * @return
	 */
	ArrayList getEmployeePayMusterButtons();
	
	ArrayList getRecordList(HashMap paramMap);

	ArrayList<ColumnsInfo> getEmployeePersonColumnsInfo();
	/**
	 * 得到左侧人员的列头
	 * @return
	 */
	ArrayList<LazyDynaBean> getPersonHeadlist();
	/**
	 * 得到人员列表数据
	 * @param paramMap
	 * @return
	 */
	ArrayList<LazyDynaBean> getPersonDataList(HashMap paramMap);
	/**
	 * 得到人员列表的总人数
	 * @param paramMap
	 * @return
	 */
	int getDataCount(HashMap paramMap);
	/**
	 * 得到人员sql
	 * @param pre
	 * @param year
	 * @param salaryid
	 * @param tablename
	 * @param userView
	 * @param verifying
	 * @param flag
	 * @param condSql
	 * @return
	 * @throws GeneralException 
	 */
	String getPersonSql(String pre, String year, String salaryid, String tablename, UserView userView, String verifying,
			String flag, String condSql);
}
