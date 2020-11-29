package com.hjsj.hrms.actionform.train.plan;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class TranEvaluationFrame extends FrameForm {
	String analyseHtml="";
	String gradeHtml="";
	String lay="";
	String isNull="";      // 1:某些指标项的标度上下限没有设定
	String scoreflag="";   //=2混合，=1标度
	String dataArea="";
	String objectID="";
	String status="";		//权重分值标识  0:分值  1:权重
	String titleName="";
	/**是否出现关闭按钮*/
	private String isClose;
	/**区分返回地址*/
	private String enteryType;
	/**版本*/
	private String home;
	@Override
    public void outPutFormHM() {
		this.setIsClose((String)this.getFormHM().get("isClose"));
		this.setEnteryType((String)this.getFormHM().get("enteryType"));
		this.setHome((String)this.getFormHM().get("home"));
		this.setTitleName((String)this.getFormHM().get("titleName"));
		this.setAnalyseHtml((String)this.getFormHM().get("analyseHtml"));
		this.setGradeHtml((String)this.getFormHM().get("gradeHtml"));
		this.setLay((String)this.getFormHM().get("lay"));
		this.setIsNull((String)this.getFormHM().get("isNull"));
		this.setScoreflag((String)this.getFormHM().get("scoreflag"));
		this.setDataArea((String)this.getFormHM().get("dataArea"));
		this.setObjectID((String)this.getFormHM().get("objectID"));
		this.setStatus((String)this.getFormHM().get("status"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isClose", this.getIsClose());
		this.getFormHM().put("enteryType", this.getEnteryType());
		this.getFormHM().put("home", this.getHome());
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        
        if("/train/evaluatingStencil".equals(arg0.getPath()) && arg1.getParameter("b_analyse")!=null) {
            arg1.setAttribute("targetWindow", "1");
        }
        return super.validate(arg0, arg1);
   }
	
	public String getGradeHtml() {
		return gradeHtml;
	}

	public void setGradeHtml(String gradeHtml) {
		this.gradeHtml = gradeHtml;
	}

	public String getLay() {
		return lay;
	}

	public void setLay(String lay) {
		this.lay = lay;
	}

	public String getDataArea() {
		return dataArea;
	}

	public void setDataArea(String dataArea) {
		this.dataArea = dataArea;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getScoreflag() {
		return scoreflag;
	}

	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAnalyseHtml() {
		return analyseHtml;
	}

	public void setAnalyseHtml(String analyseHtml) {
		this.analyseHtml = analyseHtml;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getIsClose() {
		return isClose;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public String getEnteryType() {
		return enteryType;
	}

	public void setEnteryType(String enteryType) {
		this.enteryType = enteryType;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
}
