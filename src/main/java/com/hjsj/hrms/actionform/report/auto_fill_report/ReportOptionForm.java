/*
 * Created on 2006-4-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.report.auto_fill_report;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author 
 * 设置扫描库及截止日期
 */
public class ReportOptionForm extends FrameForm {
	
	//人员库列表
	private PaginationForm dbNameListForm=new PaginationForm();
	private ArrayList dbprelist = new ArrayList();
	
	private String result;     
	private String appDate;		//截止日期
	private String startdate;   //启始日期
	private String checkbox;
	private String setResult;  //设置扫描库结果
	private String updateflag="0"; //设置没条件的单元格重新取数
	String operateObject="1";					     // 1：编辑没上报表 2：编辑上报后的表
	String home="";
	
	@Override
    public void outPutFormHM() {
		/*System.out.println("进入JSP");*/
		this.getDbNameListForm().setList((ArrayList)(this.getFormHM().get("dbnamelist")));
		this.setResult((String)this.getFormHM().get("result"));
		this.setAppDate((String)this.getFormHM().get("appdate"));
		this.setStartdate((String)this.getFormHM().get("startdate"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setHome((String)this.getFormHM().get("home"));
		this.setOperateObject((String)this.getFormHM().get("operateObject"));
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
	}


	@Override
    public void inPutTransHM() {
		/*System.out.println("进入交易类");*/
		this.getFormHM().put("selectedlist",this.getDbNameListForm().getSelectedList());
		this.getFormHM().put("checked",this.getSetResult());	
		this.getFormHM().put("appdate",this.getAppDate());
		this.getFormHM().put("startdate",this.getStartdate());
		this.getFormHM().put("updateflag",this.getUpdateflag());
		this.getFormHM().put("dbprelist",this.getDbprelist());
	}
	/**
	 * 界面接到异常的时候是显示返回还是关闭按钮
	 * zhaoxg add 2014-1-7
	 */
	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		if("/report/auto_fill_report/options".equals(mapping.getPath())&&request.getParameter("b_query2")!=null){//报表取数 zhaoxg add
			request.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }
		return super.validate(mapping, request);
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public PaginationForm getDbNameListForm() {
		return dbNameListForm;
	}
	public void setDbNameListForm(PaginationForm dbNameListForm) {
		this.dbNameListForm = dbNameListForm;
	}
		
	/**
	 * @return Returns the checkbox.
	 */
	public String getCheckbox() {
		return checkbox;
	}
	/**
	 * @param checkbox The checkbox to set.
	 */
	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}
		
	/**
	 * @return Returns the setResult.
	 */
	public String getSetResult() {
		return setResult;
	}
	/**
	 * @param setResult The setResult to set.
	 */
	public void setSetResult(String setResult) {
		this.setResult = setResult;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getUpdateflag() {
		return updateflag;
	}

	public void setUpdateflag(String updateflag) {
		this.updateflag = updateflag;
	}
	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
	public String getOperateObject() {
		return operateObject;
	}

	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}

	public ArrayList getDbprelist() {
		return dbprelist;
	}

    public String getDbpreStr() {
        String s="";
        for(int i = 0 ; i< dbprelist.size(); i++){
            RecordVo vo = (RecordVo)dbprelist.get(i);
            if("1".equals(vo.getString("flag")))
                s+=vo.getString("pre")+",";
        }
        return s;
    }
    
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	
}
