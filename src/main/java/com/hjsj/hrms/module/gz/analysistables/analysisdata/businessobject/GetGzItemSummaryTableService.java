package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetGzItemSummaryTableService {
	
	/**
	 * 获取所有数据的集合
	 * @param result map集合
	 * 			key：year 年
	 * 				month 月
	 * 				fromYear 开始时间
	 * 				endYear 截止时间
	 * 				appointtime 是否是勾选统计区间
	 * 				not_enc_rsdtlid 非加密表号
	 * @param salaryids 薪资类别（以逗号分隔）
	 * @param nbases 人员库（以逗号分隔）
	 * @param verifying 是否勾选审批过程（1：包含）
	 * @param limit 每页条数
	 * @param page 当前页
	 * @return  result.put("list_data", list_data);//数据ArrayList<LazyDynaBean>
				result.put("list_column", list_column);//栏目ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 */
	HashMap getAllData(HashMap result, String salaryids, String nbases, String verifying, int limit, int page, String filterSql, String orderSql) throws GeneralException;
	
	/**
	 * 获取表格控件的config
	 * @param dataList
	 * @param columns
	 * @return
	 * @throws GeneralException
	 */
	String getTableConfig(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columns, String rsdtlid) throws GeneralException;
	
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
