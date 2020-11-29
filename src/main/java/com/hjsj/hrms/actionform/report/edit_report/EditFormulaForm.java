/**
 * 
 */
package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 15, 2006:4:36:37 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class EditFormulaForm extends FrameForm {

	private String expid;       //公式ID
	private String tabid;		//报表ID
	private String formulatype; //公式类别
	private String exprName;    //公式名称
	private String leftExpr;    //左表达式
	private String rightExpr;   //右表达式
	
	private String flag;
	private String typeFlag;
	private String reportType;  //1和2  实际tb或tt_
	private String npercent;
	//关闭后刷新父页面使用
	private String returnFlag;
	private String status;
	private String excludeexpr;	//排除行或者列
	public String getExcludeexpr() {
		return excludeexpr;
	}


	public void setExcludeexpr(String excludeexpr) {
		this.excludeexpr = excludeexpr;
	}


	@Override
    public void outPutFormHM() {
		
		this.setExpid((String)this.getFormHM().get("expid"));
		//this.setTabid((String)this.getFormHM().get("tid"));
		this.setFormulatype((String)this.getFormHM().get("colrow"));
		this.setExprName((String)this.getFormHM().get("cname"));
		this.setLeftExpr((String)this.getFormHM().get("lexpr"));
		this.setRightExpr((String)this.getFormHM().get("rexpr"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		
		//System.out.println("tabid=" + (String)this.getFormHM().get("tabid"));
		
		String temp=(String)this.getFormHM().get("colrow");
		if(temp == null || "".equals(temp)){
			this.setTypeFlag("-1");
		}else{
			this.setTypeFlag(temp);
		}
		
		this.setFlag((String)this.getFormHM().get("rt"));
		this.setReportType((String)this.getFormHM().get("reportType"));
		
		this.setReturnFlag((String)this.getFormHM().get("returnflag"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setNpercent((String)this.getFormHM().get("npercent"));
		this.setExcludeexpr((String)this.getFormHM().get("excludeexpr"));
	}

	
	@Override
    public void inPutTransHM() {
		
	}


	public String getExprName() {
		return exprName;
	}


	public void setExprName(String exprName) {
		this.exprName = exprName;
	}



	public String getFormulatype() {
		return formulatype;
	}


	public void setFormulatype(String formulatype) {
		this.formulatype = formulatype;
	}


	public String getLeftExpr() {
		return leftExpr;
	}


	public void setLeftExpr(String leftExpr) {
		this.leftExpr = leftExpr;
	}


	public String getRightExpr() {
		return rightExpr;
	}


	public void setRightExpr(String rightExpr) {
		this.rightExpr = rightExpr;
	}


	public String getTabid() {
		return tabid;
	}


	public void setTabid(String tabid) {
		this.tabid = tabid;
	}


	public String getExpid() {
		return expid;
	}


	public void setExpid(String expid) {
		this.expid = expid;
	}


	public String getTypeFlag() {
		return typeFlag;
	}


	public void setTypeFlag(String typeFlag) {
		this.typeFlag = typeFlag;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}


	public String getReturnFlag() {
		return returnFlag;
	}


	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getNpercent() {
		return npercent;
	}


	public void setNpercent(String npercent) {
		this.npercent = npercent;
	}


	
}
