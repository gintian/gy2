package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetGzAmountStructureTableService {
	
	/**
	 * 找到薪资项和代码值
	 * @param rsdtlid
	 * @param salaryids
	 * @param fieldid
	 * @param codevalue
	 * @return  result.put("fieldListId", fieldListId);//薪资项
				result.put("fieldid", fieldid);//代码
				result.put("codeitemList", codeitemListId);//代码值
				result.put("codevalue", codevalue);//代码
	 * @throws GeneralException
	 */
	HashMap<String, Object> findAllSalaryItem(String rsdtlid, String salaryids, String fieldid, String codevalue) throws GeneralException;
	
	/**
	 * 获取所有数据和列头的集合，data和column
	 * @param selectAll 是否选择全部
	 * @param rsdtld 标号
	 * @param year 年
	 * @param endmonth 截止月份
	 * @param fieldid 薪资项
	 * @param codevalue 代码项
	 * @param salaryids 薪资账套（以逗号分隔）
	 * @param nbases 人员库（以逗号分隔）
	 * @param verifying 是否含审批过程（1包含）
	 * @return  result.put("list_data", list_data);//数据ArrayList<LazyDynaBean>
				result.put("list_column", list_column);//栏目ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 */
	HashMap getAllData(boolean selectAll,String rsdtld, int year, int endmonth, String fieldid, String codevalue, String salaryids, String nbases, String verifying) throws GeneralException;
	
	/**
	 * 获取表格控件的config
	 * @param dataList
	 * @param columns
	 * @return
	 * @throws GeneralException
	 */
	String getTableConfig(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columns, String enc_rsdtlid) throws GeneralException;
	
	/**
	 * 导出excel
	 * @param rsid 
	 * @param rsdtlid 标号
	 * @param columns_list 栏目集合
	 * @param dataList 数据集合
	 * @param tableName 文件名
	 * @return
	 */
	String export_data(String rsid, String rsdtlid, ArrayList<ColumnsInfo> columns_list, ArrayList<LazyDynaBean> dataList, String tableName);
}
