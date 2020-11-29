package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetGzStructureTableService {
	/**
	 * 
	 * @param rsdtld 标号
	 * @param salaryids 薪资类别
	 * @param fieldid 分析项
	 * @param codeitemid 分类项
	 * @param nbases 人员库
	 * @param verifying 是否包含审批中的数据
	 * @param year 年
	 * @param lay 层级
	 * @return  result.put("fieldList", fieldid_list);//分析项集合 
				result.put("fieldid", fieldid_temp);
				result.put("codeItemList", codeitemid_list);//分类项集合
				result.put("codeitemid", codeitemid_temp);
				result.put("lay", lay);//层级，如果选择分类项时没有该层级会带过来，否则默认不传
				result.put("levelSum_list", levelSum_list);//层级的集合
	 * @throws GeneralException
	 */
	HashMap<String, Object> findAllSalaryItem(String rsdtld, String salaryids, String fieldid, String codeitemid, String nbases, String verifying, String year, int lay) throws GeneralException;
	
	/**
	 * 
	 * @param map_ 年份，薪资类别，分析项，分类项
	 * @param showNumberOfPeople 是否显示每月人数
	 * @param collect 是否按层级汇总 
	 * @param lay 层级值
	 * @param salaryids 薪资类别
	 * @param nbases 人员库
	 * @param verifying 是否包含审批过程（1：包含）
	 * @return
	 * @throws GeneralException
	 */
	HashMap getAllData(HashMap<String, Object> map_, boolean showNumberOfPeople, boolean collect, int lay, String salaryids, String nbases, 
			String verifying, String filterSql, String orderSql) throws GeneralException;
	
	/**
	 * 获取表格控件的config
	 * @param dataList
	 * @param columns
	 * @return
	 * @throws GeneralException
	 */
	String getTableConfig(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columns) throws GeneralException;
	
	/**
	 * 导出excel
	 * @param rsid 
	 * @param rsdtlid 标号
	 * @param columns_list 栏目集合
	 * @param dataList 数据集合
	 * @param tableName 
	 * @return
	 */
	String export_data(String rsid, String rsdtlid, ArrayList<ColumnsInfo> columns_list, ArrayList<LazyDynaBean> dataList, String tableName);
	
	/**
	 * 查找出对应的薪资账套的集合
	 * @param queryText
	 * @return
	 */
	ArrayList<HashMap> getSalarySetList(String queryText) throws GeneralException;
}
