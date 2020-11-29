/**
 * 
 */
package com.hjsj.hrms.actionform.report.report_analyse;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:报表归档数据分析
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jul 3, 2006:10:46:29 AM
 * </p>
 * 
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportAnalyseForm extends FrameForm {

	private String reportTabid; // 表号
	private ArrayList reportList = null; // 报表集合
	private ArrayList years2 = new ArrayList(); // 报表集合
	private ArrayList reportSortList = new ArrayList();

	private String reportSortID = "";

	private int reportYearid; // 年号
	private ArrayList reportYearidList = null; // 报表年份集合

	private int reportCount; // 报表年份对应信息（月份/季度/上下半年/次数）
	private ArrayList reportCountList = null; // 报表年份对应信息集合（月份/季度/上下半年/次数）
	private int weekid;
	private ArrayList reportWeekList = null;
	private String reportCountInfo; // 报表年份对应信息集合描述（月份/季度/上下半年/次数）
	private String reportTypes = "";

	private String reportHtml; // 报表表格显示信息HTML
	private String reportTitle = "";

	private String codeFlag; // 填报单位编码标识

	private String reportState; // 报表状态 此表无可分析历史数据！

	private String reportExist;

	private String currentReport;

	// 图形显示
	private ArrayList list = null;
	private HashMap dataMap = new HashMap();
	private String char_type = "1"; // 1:柱状图 2：线状图
	private String years = "1";//近几年，最多近五年
	private ArrayList yearList =null;
	private String chartTitle = "";
	private String chartType = "";
	private String chartFlag = "";
	private String chartWidth = "750";
	private String chartHeight = "120";

	private String showFlag = "1"; // 1:单维分析 2:多维分析
	private String unitcodes = ""; // 多选的单位编码
	private String unitcode = ""; 
	private String rows = "";
	private String cols = "";
	/** 选中的数组 */
	private String right_fields[];
	private String rightfields="";
	private String condition = "";
	private ArrayList provisionTermList = new ArrayList(); // 待选条件列表
	private ArrayList schemeList = new ArrayList(); // 方案列表

	private ArrayList defaultItemList = new ArrayList(); // 默认单位选项
	private String nums = "";
	private String rowSerialNo = "";
	private String colSerialNo = "";
	private String integrateValues = "";
	private String html = "";
	private ChartParameter chartParameter = new ChartParameter();
	private String selfUnitcode = "";
	 //人员库
    private ArrayList dbnamelist = new ArrayList();  
    private String checkbase="";
    private String isclose="";
    private String yearid="";
    private String countid="";
    private String weekid2="";
    private String  totalnum="";
	private String columnflag="0";
	private String backdate="";
	private String backdate2="";
	private String reportHeight="300";
	private String colFlag="false";//判断报表是否有编号列  =false，没有  =true，有
	private String rowFlag="false";//判断报表是否有甲行  =false，没有  =true，有
	private String flag="0";//是否给予反查 0:不反查
	
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getColFlag() {
		return colFlag;
	}

	public void setColFlag(String colFlag) {
		this.colFlag = colFlag;
	}

	public String getRowFlag() {
		return rowFlag;
	}

	public void setRowFlag(String rowFlag) {
		this.rowFlag = rowFlag;
	}

	public String getReportHeight() {
		return reportHeight;
	}

	public void setReportHeight(String reportHeight) {
		this.reportHeight = reportHeight;
	}
	public ArrayList getYears2() {
		return years2;
	}

	public void setYears2(ArrayList years2) {
		this.years2 = years2;
	}
	public String getWeekid2() {
		return weekid2;
	}

	public void setWeekid2(String weekid2) {
		this.weekid2 = weekid2;
	}

	public String getSelfUnitcode() {
		return selfUnitcode;
	}

	public void setSelfUnitcode(String selfUnitcode) {
		this.selfUnitcode = selfUnitcode;
	}

	@Override
    public void outPutFormHM() {
		this.setReportTitle((String) this.getFormHM().get("reportTitle"));

		this.setShowFlag((String) this.getFormHM().get("showFlag"));
		this.setYears((String)this.getFormHM().get("years"));
		this.setReportSortList((ArrayList) this.getFormHM().get(
				"reportSortList"));
		this.setReportSortID((String) this.getFormHM().get("reportSortID"));
		this.setReportTabid((String) this.getFormHM().get("reportTabid"));
		// 设置下拉列表的默认值如果是操纵树则默认为1否则不变
		String temp1 = (String) this.getFormHM().get("optionFlag");
		if (temp1 == null) {
		} else {
			if (!"no".equals(temp1)) {
				// this.setReportTabid(1);
				this.setReportYearid(1);
				this.setReportCount(1);
			}else{
				if(this.getFormHM().get("reportCount")!=null&&this.getFormHM().get("reportYearid")!=null&&this.getFormHM().get("reportCount").toString().length()>0&&this.getFormHM().get("reportYearid").toString().length()>0){
				this.setReportCount(Integer.parseInt(""+this.getFormHM().get("reportCount")));
				this.setReportYearid(Integer.parseInt(""+this.getFormHM().get("reportYearid")));
				}
			}
		}

		// 填报单位编码
		this.setCodeFlag((String) this.getFormHM().get("codeFlag"));

		// 下拉列表对应的List集合
		this.setReportList((ArrayList) this.getFormHM().get("reportList"));
		this.setYears2((ArrayList) this.getFormHM().get("years2"));
		this.setReportYearidList((ArrayList) this.getFormHM().get(
				"reportYearList"));
		this.setReportCountList((ArrayList) this.getFormHM().get(
				"reportCounitidList"));
		this.setReportWeekList((ArrayList) this.getFormHM().get(
				"reportWeekList"));
		this.setYearList((ArrayList) this.getFormHM().get("yearList"));
		this.setReportTypes((String) this.getFormHM().get("reportTypes"));
		// count描述信息
		this.setReportCountInfo((String) this.getFormHM()
				.get("reportCountInfo"));

		// 表格HTML信息
		this.setReportHtml((String) this.getFormHM().get("reportHtml"));

		// 是否有可分析数据数据"null"或此表无可分析数据
		this.setReportState((String) this.getFormHM().get("reportState"));

		this.setChartFlag("no");
		// 是否有归档数据
		String cf = (String) this.getFormHM().get("chartFlag");
		if (cf == null) {
		} else {
			if ("yes".equals(cf)) {
				this.setDataMap((HashMap) this.getFormHM().get("dataMap"));
				this.setList((ArrayList) this.getFormHM().get("list"));
				this.setChartTitle((String) this.getFormHM().get("chartTitle"));
				this.setChartType((String) this.getFormHM().get("chartType"));
				this.setChartFlag("yes");
			} else {
				this.setChartFlag("no");
			}
		}

		this.setReportExist((String) this.getFormHM().get("reportExist"));

		this.setCurrentReport((String) this.getFormHM().get("currentReport"));

		String cw = (String) this.getFormHM().get("chartWidth");
		String ch = (String) this.getFormHM().get("chartHeight");

		if (cw == null || "".equals(cw) || ch == null || "".equals(ch)) {
			this.setChartWidth("750");
			this.setChartHeight("120");
		} else {
			this.setChartWidth(cw);
			this.setChartHeight(ch);
		}
		this.setRows((String) this.getFormHM().get("rows"));
		this.setCols((String) this.getFormHM().get("cols"));
		this.setProvisionTermList((ArrayList) this.getFormHM().get(
				"provisionTermList"));
		this.setSchemeList((ArrayList) this.getFormHM().get("schemeList"));
		this.setDefaultItemList((ArrayList) this.getFormHM().get(
				"defaultItemList"));
		this.setNums((String) this.getFormHM().get("nums"));
		this.setHtml((String) this.getFormHM().get("html"));
		this.setIntegrateValues((String) this.getFormHM()
				.get("integrateValues"));
		this.setRowSerialNo((String) this.getFormHM().get("rowSerialNo"));
		this.setColSerialNo((String) this.getFormHM().get("colSerialNo"));
		this.setSelfUnitcode((String) this.getFormHM().get("selfUnitcode"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setDbnamelist((ArrayList) this.getFormHM().get("dbnamelist"));
		this.setCheckbase((String) this.getFormHM().get("checkbase"));
		this.setIsclose((String) this.getFormHM().get("isclose"));
		this.setYearid((String) this.getFormHM().get("yearid"));
		this.setCountid((String) this.getFormHM().get("countid"));
		this.setWeekid2((String)this.getFormHM().get("weekid2") );
		this.setTotalnum((String)this.getFormHM().get("totalnum") );
		this.setColumnflag((String)this.getFormHM().get("columnflag") );
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setBackdate((String)this.getFormHM().get("backdate"));
		this.setBackdate2((String)this.getFormHM().get("backdate2"));
		this.setReportHeight((String)this.getFormHM().get("reportHeight"));
		this.setColFlag((String)this.getFormHM().get("colFlag"));
		this.setRowFlag((String)this.getFormHM().get("rowFlag"));
		this.setFlag((String) this.getFormHM().get("flag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("showFlag", this.getShowFlag());
		this.getFormHM().put("char_type", this.getChar_type());
		this.getFormHM().put("years", this.getYears());

		String ucode = (String) (((HashMap) (this.getFormHM()
				.get("requestPamaHM"))).get("code"));
		String unitcodes = (String) ((HashMap) this.getFormHM().get(
				"requestPamaHM")).get("unitcodes");
		/*
		 * if(ucode != null){ try { byte [] str = ucode.getBytes("ISO8859-1");
		 * ucode = new String(str); } catch (UnsupportedEncodingException e1) {
		 * e1.printStackTrace(); } }
		 */
		this.getFormHM().put("unitcodes", unitcodes);
		this.getFormHM().put("reportSortID", this.getReportSortID());
		this.getFormHM().put("unitCode", ucode);
		this.getFormHM().put("reportCount",
				String.valueOf(this.getReportCount()));
		this.getFormHM().put("reportYearid",
				String.valueOf(this.getReportYearid()));
		this.getFormHM().put(
				"tabid",
				(String) (((HashMap) (this.getFormHM().get("requestPamaHM")))
						.get("tabid")));
		this.getFormHM().put(
				"row",
				(String) (((HashMap) (this.getFormHM().get("requestPamaHM")))
						.get("row")));
		this.getFormHM().put(
				"col",
				(String) (((HashMap) (this.getFormHM().get("requestPamaHM")))
						.get("col")));
		// this.getFormHM().put("tabid",String.valueOf(this.getReportTabid()));
		this.getFormHM().put("codeFlag", this.getCodeFlag());
		this.setChartFlag("no");
		this.setReportExist("no");
		this.getFormHM().put("rightfields", this.rightfields);
		// this.getFormHM().put("currentReport",(String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("tabid"))
		// );

		this.getFormHM().put("chartWidth", null);
		this.getFormHM().put("chartHeight", null);
		this.getFormHM().put("reportTabid", this.getReportTabid());
		condition = "";
		this.getFormHM().put("right_fields", this.getRight_fields());
		if (right_fields != null) {
			for (int i = 0; i < right_fields.length; i++) {
				if (i == 0) {
					condition += right_fields[i];
				} else {
					condition += '`' + right_fields[i];
				}
			}
		}
		this.getFormHM().put("dbnamelist", this.getDbnamelist());
		this.getFormHM().put("backdate","");
		this.getFormHM().put("reportHeight", this.getReportHeight());
		this.getFormHM().put("colFlag", this.colFlag);
		this.getFormHM().put("rowFlag", this.rowFlag);
	}
	//liuy 2015-1-29 7080：报表分析/报表浏览/反查：对定义了计算公式的单元格单位，提示不能反查后点返回报404页面  start
	@Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		if("/report/report_analyse/reportanalyse".equals(mapping.getPath())){
			request.setAttribute("targetWindow", "");//0不显示按钮 |1关闭|默认为返回
		}
		return super.validate(mapping, request);
	}
	//liuy 2015-1-29 end
	public ArrayList getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList reportList) {
		this.reportList = reportList;
	}

	public void setReportTabid(String reportTabid) {
		this.reportTabid = reportTabid;
	}

	public String getReportTabid() {
		return reportTabid;
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

	public String getReportExist() {
		return reportExist;
	}

	public void setReportExist(String reportExist) {
		this.reportExist = reportExist;
	}

	public String getCurrentReport() {
		return currentReport;
	}

	public void setCurrentReport(String currentReport) {
		this.currentReport = currentReport;
	}

	public ChartParameter getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
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

	public String getReportSortID() {
		return reportSortID;
	}

	public void setReportSortID(String reportSortID) {
		this.reportSortID = reportSortID;
	}

	public ArrayList getReportSortList() {
		return reportSortList;
	}

	public void setReportSortList(ArrayList reportSortList) {
		this.reportSortList = reportSortList;
	}

	public String getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}

	public String getUnitcodes() {
		return unitcodes;
	}

	public void setUnitcodes(String unitcodes) {
		this.unitcodes = unitcodes;
	}

	public HashMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	public String getChar_type() {
		return char_type;
	}

	public void setChar_type(String char_type) {
		this.char_type = char_type;
	}
	
	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}
	
	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
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

	public String getReportTypes() {
		return reportTypes;
	}

	public void setReportTypes(String reportTypes) {
		this.reportTypes = reportTypes;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public ArrayList getProvisionTermList() {
		return provisionTermList;
	}

	public void setProvisionTermList(ArrayList provisionTermList) {
		this.provisionTermList = provisionTermList;
	}

	public ArrayList getSchemeList() {
		return schemeList;
	}

	public void setSchemeList(ArrayList schemeList) {
		this.schemeList = schemeList;
	}

	public ArrayList getDefaultItemList() {
		return defaultItemList;
	}

	public void setDefaultItemList(ArrayList defaultItemList) {
		this.defaultItemList = defaultItemList;
	}

	public String getNums() {
		return nums;
	}

	public void setNums(String nums) {
		this.nums = nums;
	}

	public String getRowSerialNo() {
		return rowSerialNo;
	}

	public void setRowSerialNo(String rowSerialNo) {
		this.rowSerialNo = rowSerialNo;
	}

	public String getColSerialNo() {
		return colSerialNo;
	}

	public void setColSerialNo(String colSerialNo) {
		this.colSerialNo = colSerialNo;
	}

	public String getIntegrateValues() {
		return integrateValues;
	}

	public void setIntegrateValues(String integrateValues) {
		this.integrateValues = integrateValues;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public ArrayList getDbnamelist() {
		return dbnamelist;
	}

	public void setDbnamelist(ArrayList dbnamelist) {
		this.dbnamelist = dbnamelist;
	}

	public String getCheckbase() {
		return checkbase;
	}

	public void setCheckbase(String checkbase) {
		this.checkbase = checkbase;
	}

	public String getIsclose() {
		return isclose;
	}

	public void setIsclose(String isclose) {
		this.isclose = isclose;
	}

	public String getYearid() {
		return yearid;
	}

	public void setYearid(String yearid) {
		this.yearid = yearid;
	}

	public String getCountid() {
		return countid;
	}

	public void setCountid(String countid) {
		this.countid = countid;
	}

	public String getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(String totalnum) {
		this.totalnum = totalnum;
	}
	public String getRightfields() {
		return rightfields;
	}

	public void setRightfields(String rightfields) {
		this.rightfields = rightfields;
	}

	public String getColumnflag() {
		return columnflag;
	}

	public void setColumnflag(String columnflag) {
		this.columnflag = columnflag;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public String getBackdate2() {
		return backdate2;
	}

	public void setBackdate2(String backdate2) {
		this.backdate2 = backdate2;
	}




}
