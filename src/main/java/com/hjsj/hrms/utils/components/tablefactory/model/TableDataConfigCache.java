package com.hjsj.hrms.utils.components.tablefactory.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 表格控件存储数据对象
 * @author guodd
 *2015-07-24
 */
public class TableDataConfigCache extends HashMap implements Serializable{
    
	/**
	 * 查询sql相对应的字段
	 */
	private String[] dataFields;
	/**
	 * 表格column集合
	 */
	private ArrayList tableColumns;
	/**
	 * 经过栏目设置转后column集合
	 */
	private ArrayList displayColumns;
	
	private ArrayList defaultSchemeColumnList;
	
	/**
	 * fielditem对象集合，与tableColumns对应
	 */
	private HashMap queryFields;
	/**
	 * 每页条数
	 */
	private int pageSize;
	/**
	 * 表格column的key-value集合，用于方便查找
	 */
	private LinkedHashMap  columnMap;
	/**
	 * 表格分页对象
	 */
	private Pageable pageable;
	/**
	 * 表格数据
	 */
	private ArrayList tableData;
	/**
	 * 表格具体查询语句，不带order by 的
	 */
	private String tableSql;
	/**
	 * 表格排序语句
	 */
	private String sortSql;
	/**
	 * 查询条件语句，会将此语句拼加到 tableSql上
	 */
	private String querySql;
	/**
	 * 过滤条件语句
	 */
	private String filterSql;
	/**
	 * sql查询使用的主键（索引列）
	 */
	private String indexkey;
	
	private ArrayList columnDisplayConfig;
	
	
	private String schemePrivFields = null;
	/**
	 *表格加载前走的交易类号
	 */
	private String beforeLoadFunctionId = null;
	/**
	 * 自定义参数集合
	 */
	private HashMap customParamHM = new HashMap();
	
	public String[] getDataFields() {
		return dataFields;
	}
	public void setDataFields(String[] dataFields) {
		this.dataFields = dataFields;
	}
	public ArrayList getTableColumns() {
		return tableColumns;
	}
	public void setTableColumns(ArrayList tableColumns) {
		this.tableColumns = tableColumns;
	}
	public ArrayList getDisplayColumns() {
		return displayColumns;
	}
	public void setDisplayColumns(ArrayList displayColumns) {
		this.displayColumns = displayColumns;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public LinkedHashMap getColumnMap() {
		return columnMap;
	}
	public void setColumnMap(LinkedHashMap columnMap) {
		this.columnMap = columnMap;
	}
	public Pageable getPageable() {
		return pageable;
	}
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	public ArrayList getTableData() {
		return tableData;
	}
	public void setTableData(ArrayList tableData) {
		this.tableData = tableData;
	}
	public String getTableSql() {
		return tableSql;
	}
	public void setTableSql(String tableSql) {
		this.tableSql = tableSql;
	}
	public String getSortSql() {
		return sortSql;
	}
	public void setSortSql(String sortSql) {
		this.sortSql = sortSql;
	}
	public String getIndexkey() {
		return indexkey;
	}
	public void setIndexkey(String indexkey) {
		this.indexkey = indexkey;
	}
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public HashMap getCustomParamHM() {
		return customParamHM;
	}
	public void setCustomParamHM(HashMap customParamHM) {
		this.customParamHM = customParamHM;
	}
	public String getFilterSql() {
		return filterSql;
	}
	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}
	public HashMap getQueryFields() {
		return queryFields;
	}
	public void setQueryField(HashMap items){
		this.queryFields = items;
	}
	
	
	public ArrayList getColumnDisplayConfig() {
		return columnDisplayConfig;
	}
	public void setColumnDisplayConfig(ArrayList columnDisplayConfig) {
		this.columnDisplayConfig = columnDisplayConfig;
	}
	
	
	
	public String getSchemePrivFields() {
		return schemePrivFields;
	}
	public void setSchemePrivFields(String schemePrivFields) {
		this.schemePrivFields = schemePrivFields;
	}
	
	
	
	public String getBeforeLoadFunctionId() {
		return beforeLoadFunctionId;
	}
	public void setBeforeLoadFunctionId(String beforeLoadFunctionId) {
		this.beforeLoadFunctionId = beforeLoadFunctionId;
	}
	
	public ArrayList getDefaultSchemeColumnList() {
		return defaultSchemeColumnList;
	}
	public void setDefaultSchemeColumnList(ArrayList defaultSchemeColumnList) {
		this.defaultSchemeColumnList = defaultSchemeColumnList;
	}
	
	@Override
	/**
	 * 为兼容旧程序HashMap创建的方法，请使用对应的get方法
	 * @param key
	 * @return
	 */
	public Object get(Object key) {
			try {
				String param = key.toString();
				String methodName = "get"+param.substring(0, 1).toUpperCase()+ param.substring(1);
				Method method = this.getClass().getMethod(methodName);
				return method.invoke(this);
			} catch (Exception e) {
				return super.get(key);
			}
			
	}


	@Override
	/**
	 * 为兼容旧程序HashMap创建的方法，请使用对应的set方法
	 * @param key
	 * @return
	 */
	public Object put(Object key, Object value) {
		try {
			String param = key.toString();
			Field field = this.getClass().getDeclaredField(param);
			String methodName = "set"+param.substring(0, 1).toUpperCase()+ param.substring(1);
			Method method = this.getClass().getMethod(methodName,field.getType());
			return method.invoke(this,value);
		} catch (Exception e) {
			return super.put(key, value);
		}
	}
	
	@Override
	/**
	 * 为兼容旧程序HashMap创建的方法
	 * @param key
	 * @return
	 */
	public boolean containsKey(Object key) {
		try {
			Field field = this.getClass().getDeclaredField(key.toString());
			return true;
		} catch (Exception e) {
			return super.containsKey(key);
		}
	}
	
}
