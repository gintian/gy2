/**
 * 
 */
package com.hjsj.hrms.actionform.report.report_state;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 28, 2006:1:37:32 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportStateForm extends FrameForm {

	
	private String sql_str;
	private String where_str;
	private int current=1;
	private String reportStateSearchFlag; //报表检索标识(-2 全部,-1 未填 ,0 正在编辑,1 已上报 ,2 打回 ,3 封存)
	private String reportUnitCode;
	private PaginationForm reportTypeList = new PaginationForm();
	private ArrayList titleList=new ArrayList();
	private String content=new String();
	private String info=new String();
	private String title="";
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList getTitleList() {
		return titleList;
	}

	public void setTitleList(ArrayList titleList) {
		this.titleList = titleList;
	}

	public PaginationForm getReportTypeList() {
		return reportTypeList;
	}

	public void setReportTypeList(PaginationForm reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	public ReportStateForm(){
		super();
	}
	
	@Override
    public void outPutFormHM() {
		this.setSql_str((String)this.getFormHM().get("sql_str"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
		this.setReportStateSearchFlag((String)this.getFormHM().get("statu"));
		this.setReportUnitCode((String)this.getFormHM().get("unitcode"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.getReportTypeList().setList((ArrayList)this.getFormHM().get("reporttypelist"));
		this.getReportTypeList().getPagination().gotoPage(current);	
		this.setTitleList((ArrayList)this.getFormHM().get("titleList"));
		this.setInfo((String)this.getFormHM().get("info"));
	}


	@Override
    public void inPutTransHM() {
		HashMap map = ((HashMap)(this.getFormHM().get("requestPamaHM")));
		String unitCode = (String)(map.get("code")) ;
		//System.out.println("unitCode=" + unitCode);
		this.getFormHM().put("unitCode" , unitCode);

		//报表检索状态标识(-2 全部,-1 未填 ,0 正在编辑,1 已上报 ,2 打回 ,3 封存)
		String reportStateSearchFlag = (String)(map.get("rssf"));
		//System.out.println("reportStateSearchFlag="+reportStateSearchFlag);
		
		this.getFormHM().put("reportStateSearchFlag",reportStateSearchFlag);
		
		map.put("code","");
		if(unitCode == null || "".equals(unitCode)){
			map.put("rssf","");
			this.getFormHM().put("reportStateSearchFlag","");
		}

		if(reportStateSearchFlag == null || "".equals(reportStateSearchFlag)){
			this.setReportStateSearchFlag("");
		}else{
			this.setReportStateSearchFlag(reportStateSearchFlag);
		}

		//选中的报表集合
		if(this.getPagination()!=null){
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		}
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("title", this.getTitle());
	}


	public String getSql_str() {
		return sql_str;
	}


	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}


	public String getWhere_str() {
		return where_str;
	}


	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getReportUnitCode() {
		return reportUnitCode;
	}

	public void setReportUnitCode(String reportUnitCode) {
		this.reportUnitCode = reportUnitCode;
	}

	public String getReportStateSearchFlag() {
		return reportStateSearchFlag;
	}

	public void setReportStateSearchFlag(String reportStateSearchFlag) {
		this.reportStateSearchFlag = reportStateSearchFlag;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/report/report_state/reportstate".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().gotoPage(1);//?
        }
        
        return super.validate(arg0, arg1);
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}
}
