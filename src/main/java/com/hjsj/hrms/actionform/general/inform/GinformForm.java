/**
 * 
 */
package com.hjsj.hrms.actionform.general.inform;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * <p>Title:GinformForm</p>
 * <p>Description:通用的信息维护接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-23:8:39:11</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class GinformForm extends FrameForm {

	/**字段列表*/
	private ArrayList fieldlist;
	/**应用库名称*/
	private String dbpre="usr";
	/**数据过滤SQL语句*/
	private String sql;
	/**表名*/
	private String tablename;
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
    public void outPutFormHM() {
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dbpre",this.getDbpre());
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

}
