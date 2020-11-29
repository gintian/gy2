/*
 * Created on 2006-3-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jul 21, 2006:10:17:30 AM
 * </p>
 * 
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class PrintReportForm extends FrameForm {

	private String dbsql; // <hrms:importgeneraldata/>标签所需的SQL语句参数

	private int sortId; // 改变报表类别下拉列表状态后传递类别ID

	private PaginationForm printReportForm = new PaginationForm(); // 显示数据的分页处理

	private int current = 1; // 分页处理的定位

	private String userID;

	private String userName;

	private String path;

	// 表达式分析结果
	private String reportExprAnalyseResult;

	// 指标分析结果
	private String reportFieldAnalyseResult;

	// 表内校验结果
	private String reportInnerCheckResult;
	private String reportInnerCheckResult_t;
	// 表间校验结果
	private String reportSpaceCheckResult;
	private String reportSpaceCheckResult_t;
	private String operateObject;

	private String unitcode;

	// private String reportDownLoadInfo;

	private String print;
	
	private String downLoadFlag;
	
	//报表总效验
	private String checkFlag;		//编辑报表 , 报表汇总 (1,2)
	private String checkUnitCode;   //报表汇总中的填报单位
	private String isCheck;         //是否是报表总效验 
	private String reportTypes;

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//去掉打印预演-批量打印报错返回按钮
		if("/report/edit_report/printReport".equals(arg0.getPath())
				&&arg1.getParameter("b_query2")!=null&&arg1.getParameter("reptype")!=null){
			arg1.setAttribute("targetWindow", "0");//0不显示按钮 |1关闭|默认为返回
		}
		return super.validate(arg0, arg1);
	}
	
	@Override
    public void outPutFormHM() {

		this.setDbsql((String) this.getFormHM().get("sql"));
		this.getPrintReportForm().setList(
				(ArrayList) this.getFormHM().get("reportlist"));
		this.getPrintReportForm().getPagination().gotoPage(current);

		this.setReportExprAnalyseResult((String) this.getFormHM().get(
				"reportExprAnalyseResult"));

		this.setUserID((String) this.getFormHM().get("userid"));
		this.setUserName((String) this.getFormHM().get("username"));
		this.setReportInnerCheckResult((String) this.getFormHM().get(
				"reportInnerCheckResult"));
		this.setReportInnerCheckResult_t((String)this.getFormHM().get("reportInnerCheckResult_t"));
		this.setReportSpaceCheckResult((String) this.getFormHM().get(
				"reportSpaceCheckResult"));
		this.setReportSpaceCheckResult_t((String)this.getFormHM().get("reportSpaceCheckResult_t"));
		this.setReportFieldAnalyseResult((String) this.getFormHM().get(
				"reportFieldAnalyseResult"));
		if (!((String) getFormHM().get("path") == null))
			this.setPath((String) getFormHM().get("path"));
		
		this.setCheckFlag((String)this.getFormHM().get("checkflag"));
		this.setCheckUnitCode((String)this.getFormHM().get("checkunitcode"));
		this.setIsCheck((String)this.getFormHM().get("ischeck"));
		
		this.setPrint((String)this.getFormHM().get("print"));
		this.setDownLoadFlag((String)this.getFormHM().get("downloadflag"));
		this.setSortId(Integer.parseInt(this.getFormHM().get("sortId").toString()));
		this.setReportTypes((String)this.getFormHM().get("reportTypes"));
		// System.out.println("-------->" +
		// (ArrayList)this.getFormHM().get("reportlist"));
		// this.setReportDownLoadInfo((String)this.getFormHM().get("downLoadInfo"));
	}

	@Override
    public void inPutTransHM() {
		this.setPath("");
		// 报表类别
		this.getFormHM().put(
				"sid",
				(String) (((HashMap) (this.getFormHM().get("requestPamaHM")))
						.get("sortId")));
		// 选中的报表集合
		this.getFormHM().put("selectedlist",
				this.getPrintReportForm().getSelectedList());
		// 填报单位编码
		getFormHM().put("unitcode", getUnitcode());
		// 报表标识
		getFormHM().put("operateObject", this.getOperateObject());
		
		this.getFormHM().put("checkflag","");
		this.getFormHM().put("checkunitcode","");
		this.getFormHM().put("ischeck","hidden");
		this.getFormHM().put("print","");
	}

	public String getReportFieldAnalyseResult() {
		return reportFieldAnalyseResult;
	}

	public void setReportFieldAnalyseResult(String reportFieldAnalyseResult) {
		this.reportFieldAnalyseResult = reportFieldAnalyseResult;
	}

	public String getReportSpaceCheckResult() {
		return reportSpaceCheckResult;
	}

	public void setReportSpaceCheckResult(String reportSpaceCheckResult) {
		this.reportSpaceCheckResult = reportSpaceCheckResult;
	}

	public String getReportInnerCheckResult() {
		return reportInnerCheckResult;
	}

	public void setReportInnerCheckResult(String reportInnerCheckResult) {
		this.reportInnerCheckResult = reportInnerCheckResult;
	}

	public String getDbsql() {
		return dbsql;
	}

	public void setDbsql(String dbsql) {
		this.dbsql = dbsql;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public PaginationForm getPrintReportForm() {
		return printReportForm;
	}

	public void setPrintReportForm(PaginationForm printReportForm) {
		this.printReportForm = printReportForm;
	}

	/*
	 * public ArrayList getReportAnalyseList() { return reportAnalyseList; }
	 * 
	 * public void setReportAnalyseList(ArrayList reportAnalyseList) {
	 * this.reportAnalyseList = reportAnalyseList; }
	 * 
	 */
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReportExprAnalyseResult() {
		return reportExprAnalyseResult;
	}

	public void setReportExprAnalyseResult(String reportExprAnalyseResult) {
		this.reportExprAnalyseResult = reportExprAnalyseResult;
	}

	public String getOperateObject() {
		return operateObject;
	}

	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	public String getCheckUnitCode() {
		return checkUnitCode;
	}

	public void setCheckUnitCode(String checkUnitCode) {
		this.checkUnitCode = checkUnitCode;
	}

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getPrint() {
		return print;
	}

	public void setPrint(String print) {
		this.print = print;
	}

	public String getDownLoadFlag() {
		return downLoadFlag;
	}

	public void setDownLoadFlag(String downLoadFlag) {
		this.downLoadFlag = downLoadFlag;
	}

	public String getReportInnerCheckResult_t() {
		return reportInnerCheckResult_t;
	}

	public void setReportInnerCheckResult_t(String reportInnerCheckResult_t) {
		this.reportInnerCheckResult_t = reportInnerCheckResult_t;
	}

	public String getReportSpaceCheckResult_t() {
		return reportSpaceCheckResult_t;
	}

	public void setReportSpaceCheckResult_t(String reportSpaceCheckResult_t) {
		this.reportSpaceCheckResult_t = reportSpaceCheckResult_t;
	}

	public String getReportTypes() {
		return reportTypes;
	}

	public void setReportTypes(String reportTypes) {
		this.reportTypes = reportTypes;
	}
	
	
	
	

	/*
	 * public String getReportDownLoadInfo() { return reportDownLoadInfo; }
	 * 
	 * 
	 * public void setReportDownLoadInfo(String reportDownLoadInfo) {
	 * this.reportDownLoadInfo = reportDownLoadInfo; }
	 */

}
