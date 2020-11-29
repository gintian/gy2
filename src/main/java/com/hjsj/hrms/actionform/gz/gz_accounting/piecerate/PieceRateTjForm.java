package com.hjsj.hrms.actionform.gz.gz_accounting.piecerate;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 计件薪资统计报表信息
 * @date 2013-03-30
 * @author 田野
 *
 */
public class PieceRateTjForm extends FrameForm{
	//时间范围
	private String startDate;//开始日期
	private String endDate;//结束日期
	private ArrayList reportList = new ArrayList();//统计页面的报表下拉列表
	private String sql;//计件薪资统计信息的sql语句
	private String defId;//主键为系统默认生成的id
	private String moduleCode;//模块号：SAL_JJ=计件工资
	private String busiId;//业务id
	private String defName;//汇总表名
	private String  sortId;//顺序
	private String content;//内容
	private String cond;//条件公式
	private String useFlag;//使用标志（保留）;
	private String tableName;
	
	private String tjWhere="" ;//统计条件
	private ArrayList fieldlist = new ArrayList();// 字段列表 通用

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());
		this.getFormHM().put("sql", this.getSql());
		this.getFormHM().put("defId", this.getDefId());
		this.getFormHM().put("moduleCode", this.getModuleCode());
		this.getFormHM().put("defName", this.getDefName());
		this.getFormHM().put("sortId", this.getSortId());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("cond", this.getCond());
		this.getFormHM().put("useFlag", this.getUseFlag());
		this.getFormHM().put("reportList", this.getReportList());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("tableName", this.getTableName());        
		this.getFormHM().put("tjWhere", this.getTjWhere());        
		this.getFormHM().put("pagerows", this.getPagerows()==0?"10":(this.getPagerows()+""));
		
	}

	@Override
    public void outPutFormHM() {
		this.setStartDate((String)this.getFormHM().get("startDate"));
		this.setEndDate((String)this.getFormHM().get("endDate"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setReportList ((ArrayList)this.getFormHM().get("reportList"));
		this.setFieldlist ((ArrayList)this.getFormHM().get("fieldlist"));
		this.setModuleCode((String)this.getFormHM().get("moduleCode"));
		this.setDefName((String)this.getFormHM().get("defName"));
		this.setSortId((String)this.getFormHM().get("sortId"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setCond((String)this.getFormHM().get("cond"));
		this.setUseFlag((String)this.getFormHM().get("useFlag"));
		if (this.getFormHM().get("pagerows") != null)
	    	this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));
		this.setTableName((String) this.getFormHM().get("tableName"));
		this.setTjWhere((String) this.getFormHM().get("tjWhere"));
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ArrayList getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList reportList) {
		this.reportList = reportList;
	}


	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}


	public String getDefId() {
		return defId;
	}

	public void setDefId(String defId) {
		this.defId = defId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getDefName() {
		return defName;
	}

	public void setDefName(String defName) {
		this.defName = defName;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public String getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTjWhere() {
		return tjWhere;
	}

	public void setTjWhere(String tjWhere) {
		this.tjWhere = tjWhere;
	}
	
	
	

}
