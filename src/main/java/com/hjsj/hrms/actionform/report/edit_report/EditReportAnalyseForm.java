/**
 * 
 */
package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: 编辑报表中的报表归档数据分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 1, 2006:4:43:36 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class EditReportAnalyseForm extends FrameForm {

	private String  reportTabid;			  //表号	
	private int reportYearid;             //年号	
	private ArrayList reportYearidList =  null;  //报表年份集合
	private ArrayList reportList = null; // 报表集合
	private int reportCount;                    //报表年份对应信息（月份/季度/上下半年/次数）
	private ArrayList reportCountList  =  null;    //报表年份对应信息集合（月份/季度/上下半年/次数）
	private int weekid;
	private ArrayList reportWeekList=null;
	private String reportCountInfo;             //报表年份对应信息集合描述（月份/季度/上下半年/次数）
	private String reportTypes="";
	private String reportHtml;                 //报表表格显示信息HTML
	
	private String codeFlag;  //填报单位编码标识
	
	private String reportState; //报表状态 此表无可分析历史数据！
	private String use_scope_cond="";						//是否使用统计口径，0不使用(默认值), 1使用
	private ArrayList scopelist= new ArrayList();
	private String showFlag = "1"; // 1:单维分析 2:多维分析
	private String char_type = "1"; // 1:柱状图 2：线状图
	private String reportchangeTabid ="";	//列表表id
	private String scopeid = "0";
	private String scopeids = "";			//统计口径ids
	private String use_scope_cond2="0";		//区别统计口径，1：不是统计口径
	//图形显示
	private ArrayList list = null;
	private String chartTitle = "";
	private String chartType = "";
	private String chartFlag = "";
	private String chartWidth;
	private String chartHeight;
	private String columnflag="0";
	private HashMap dataMap = new HashMap();
	private String username = "";
	private String obj1 = "";
	private String tabid = "";
	
	private String editOrreport = "";  //报表管理中  编辑报表和报表汇总中的报表数据分析  对返回哪个页面做判断
	public String getEditOrreport() {
		return editOrreport;
	}


	public void setEditOrreport(String editOrreport) {
		this.editOrreport = editOrreport;
	}


	@Override
    public void outPutFormHM() {
	
		
		//填报单位编码
		this.setCodeFlag((String)this.getFormHM().get("codeFlag"));
		
		this.setReportYearidList((ArrayList)this.getFormHM().get("reportYearList"));
		this.setReportCountList((ArrayList)this.getFormHM().get("reportCounitidList"));
		this.setReportWeekList((ArrayList)this.getFormHM().get("reportWeekList"));
		this.setReportTypes((String)this.getFormHM().get("reportTypes"));
		this.setReportYearid(Integer.parseInt((String)this.getFormHM().get("reportYearid")));
		this.setReportCount(Integer.parseInt((String)this.getFormHM().get("reportCount")));
		
		//count描述信息
		this.setReportCountInfo((String)this.getFormHM().get("reportCountInfo"));
		
		//表格HTML信息
		this.setReportHtml((String)this.getFormHM().get("reportHtml"));
		
		//是否有可分析数据数据"null"或此表无可分析数据
		this.setReportState((String)this.getFormHM().get("reportState"));
		
		this.setChartFlag("no");
		//是否有归档数据
		String cf = (String)this.getFormHM().get("chartFlag");
		if(cf == null){
		}else{
			if("yes".equals(cf)){
				this.setDataMap((HashMap) this.getFormHM().get("dataMap"));
				this.setList((ArrayList)this.getFormHM().get("list"));
				this.setChartTitle((String)this.getFormHM().get("chartTitle"));
				this.setChartType("11");
				this.setChartFlag("yes");
			}else{
				this.setChartFlag("no");
			}
		}
	
		this.setReportTabid((String)this.getFormHM().get("reportTabid"));
		
		this.setChartWidth((String)this.getFormHM().get("chartWidth"));
		this.setChartHeight((String)this.getFormHM().get("chartHeight"));
		this.setColumnflag((String)this.getFormHM().get("columnflag"));
		this.setUse_scope_cond((String)this.getFormHM().get("use_scope_cond"));
		this.setShowFlag((String) this.getFormHM().get("showFlag"));
		// 下拉列表对应的List集合
		this.setReportList((ArrayList) this.getFormHM().get("reportList"));
		this.setReportchangeTabid((String) this.getFormHM().get("reportchangeTabid"));
		this.setScopelist((ArrayList) this.getFormHM().get("scopelist"));
		this.setScopeid((String) this.getFormHM().get("scopeid"));
		this.setUse_scope_cond2((String) this.getFormHM().get("use_scope_cond2"));
		this.setChartType((String) this.getFormHM().get("chartType"));
		this.setUsername((String) this.getFormHM().get("username"));
		this.setObj1((String) this.getFormHM().get("obj1"));
		this.setTabid((String) this.getFormHM().get("tabid"));
		this.setEditOrreport((String)this.getFormHM().get("editOrreport"));
	}


	@Override
    public void inPutTransHM() {
		String ucode = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("code"));
	/*	if(ucode != null){
			try {
				byte [] str = ucode.getBytes("ISO8859-1");
				ucode = new String(str);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}*/
		this.getFormHM().put("unitCode" , ucode );
		this.getFormHM().put("reportCount",String.valueOf(this.getReportCount()));
		this.getFormHM().put("reportYearid",String.valueOf(this.getReportYearid()));
		this.getFormHM().put("tabid" ,(String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("tabid")) );

		//this.getFormHM().put("tabid",String.valueOf(this.getReportTabid()));
		this.getFormHM().put("codeFlag" , this.getCodeFlag());
		//this.setChartFlag("no");
		this.getFormHM().put("showFlag", this.getShowFlag());
		this.getFormHM().put("char_type", this.getChar_type());
		this.getFormHM().put("scopeids", this.getScopeids());
		this.getFormHM().put("username", this.getUsername());
		this.getFormHM().put("obj1", this.getObj1());
		this.getFormHM().put("tabid", this.getTabid());
	}




	public String getReportTabid() {
		return reportTabid;
	}


	public void setReportTabid(String reportTabid) {
		this.reportTabid = reportTabid;
	}


	public int getReportCount() {
		return reportCount;
	}


	public void setReportCount(int reportCount) {
		this.reportCount = reportCount;
	}


	public ArrayList getReportCountList() {
		return reportCountList;
	}


	public void setReportCountList(ArrayList reportCountList) {
		this.reportCountList = reportCountList;
	}


	public int getReportYearid() {
		return reportYearid;
	}


	public void setReportYearid(int reportYearid) {
		this.reportYearid = reportYearid;
	}


	public ArrayList getReportYearidList() {
		return reportYearidList;
	}


	public void setReportYearidList(ArrayList reportYearidList) {
		this.reportYearidList = reportYearidList;
	}


	public String getReportCountInfo() {
		return reportCountInfo;
	}


	public void setReportCountInfo(String reportCountInfo) {
		this.reportCountInfo = reportCountInfo;
	}


	public String getReportHtml() {
		return reportHtml;
	}


	public void setReportHtml(String reportHtml) {
		this.reportHtml = reportHtml;
	}


	public String getCodeFlag() {
		return codeFlag;
	}


	public void setCodeFlag(String codeFlag) {
		this.codeFlag = codeFlag;
	}


	public String getReportState() {
		return reportState;
	}


	public void setReportState(String reportState) {
		this.reportState = reportState;
	}


	public String getChartTitle() {
		return chartTitle;
	}


	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}


	public ArrayList getList() {
		return list;
	}


	public void setList(ArrayList list) {
		this.list = list;
	}


	public String getChartType() {
		return chartType;
	}


	public void setChartType(String chartType) {
		this.chartType = chartType;
	}


	public String getChartFlag() {
		return chartFlag;
	}


	public void setChartFlag(String chartFlag) {
		this.chartFlag = chartFlag;
	}


	public String getChartHeight() {
		return chartHeight;
	}


	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}


	public String getChartWidth() {
		return chartWidth;
	}


	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}


	public String getReportTypes() {
		return reportTypes;
	}


	public void setReportTypes(String reportTypes) {
		this.reportTypes = reportTypes;
	}


	public ArrayList getReportWeekList() {
		return reportWeekList;
	}


	public void setReportWeekList(ArrayList reportWeekList) {
		this.reportWeekList = reportWeekList;
	}


	public int getWeekid() {
		return weekid;
	}


	public void setWeekid(int weekid) {
		this.weekid = weekid;
	}


	public String getColumnflag() {
		return columnflag;
	}


	public void setColumnflag(String columnflag) {
		this.columnflag = columnflag;
	}


	public String getUse_scope_cond() {
		return use_scope_cond;
	}


	public void setUse_scope_cond(String use_scope_cond) {
		this.use_scope_cond = use_scope_cond;
	}


	public String getShowFlag() {
		return showFlag;
	}


	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}


	public String getChar_type() {
		return char_type;
	}


	public void setChar_type(String char_type) {
		this.char_type = char_type;
	}

	public ArrayList getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList reportList) {
		this.reportList = reportList;
	}


	public String getReportchangeTabid() {
		return reportchangeTabid;
	}


	public void setReportchangeTabid(String reportchangeTabid) {
		this.reportchangeTabid = reportchangeTabid;
	}


	public ArrayList getScopelist() {
		return scopelist;
	}


	public void setScopelist(ArrayList scopelist) {
		this.scopelist = scopelist;
	}


	public String getScopeid() {
		return scopeid;
	}


	public void setScopeid(String scopeid) {
		this.scopeid = scopeid;
	}


	public String getScopeids() {
		return scopeids;
	}


	public void setScopeids(String scopeids) {
		this.scopeids = scopeids;
	}


	public String getUse_scope_cond2() {
		return use_scope_cond2;
	}


	public void setUse_scope_cond2(String use_scope_cond2) {
		this.use_scope_cond2 = use_scope_cond2;
	}


	public HashMap getDataMap() {
		return dataMap;
	}


	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getObj1() {
		return obj1;
	}


	public void setObj1(String obj1) {
		this.obj1 = obj1;
	}


	public String getTabid() {
		return tabid;
	}


	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

}
