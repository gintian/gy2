package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ScoreAjustForm.java</p>
 * <p>Description:评分调整</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-05 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public final class ScoreAjustForm extends FrameForm
{
	private String object_type;

	private ArrayList dataList = new ArrayList();
	
	private ArrayList yearList = new ArrayList();
	
	private String year;
	
	private String month="";
	
	private String quarter="";
	
	private String scoreAjustHtml="";
	
	private String plan_id="";
	
	private String object_id="";
	
	private String ajustOper="";
	
	private String showGrpOrder="";
	private String adjustEvalRange="0";
	private String objectname = "";//按姓名或唯一指标模糊查找
	//当前考核对象的评分主体
	private ArrayList mainBodyList = new ArrayList();
	
	private PaginationForm setlistform = new PaginationForm();
	
	
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("object_id", this.getObject_id());
		this.getFormHM().put("object_type", this.getObject_type());
		this.getFormHM().put("dataList", this.getDataList());
		this.getFormHM().put("yearList", this.getYearList());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("setlistform", this.getSetlistform());
		this.getFormHM().put("month", this.getMonth());
		this.getFormHM().put("quarter", this.getQuarter());
		this.getFormHM().put("scoreAjustHtml", this.getScoreAjustHtml());
		this.getFormHM().put("ajustOper", this.getAjustOper());
		this.getFormHM().put("showGrpOrder", this.getShowGrpOrder());
		this.getFormHM().put("mainBodyList", this.getMainBodyList());
		this.getFormHM().put("objectname", this.getObjectname());
	}

	@Override
    public void outPutFormHM()
	{ 
		
		this.setAdjustEvalRange((String)this.getFormHM().get("adjustEvalRange"));
		this.setMainBodyList((ArrayList) this.getFormHM().get("mainBodyList"));
		this.setAjustOper((String) this.getFormHM().get("ajustOper"));
		this.setPlan_id((String) this.getFormHM().get("plan_id"));
		this.setObject_id((String) this.getFormHM().get("object_id"));
		this.setScoreAjustHtml((String) this.getFormHM().get("scoreAjustHtml"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setDataList((ArrayList) this.getFormHM().get("dataList"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("dataList"));
		this.setYear((String) this.getFormHM().get("year"));
		this.setYearList((ArrayList) this.getFormHM().get("yearList"));
		this.setMonth((String) this.getFormHM().get("month"));
		this.setQuarter((String) this.getFormHM().get("quarter"));
		this.setShowGrpOrder((String) this.getFormHM().get("showGrpOrder"));
		this.setObjectname((String) this.getFormHM().get("objectname"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		try
		{
			if ("/selfservice/performance/scoreAjust".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && ("link".equals(arg1.getParameter("b_query"))|| "query".equals(arg1.getParameter("b_query"))))
			{
				if (this.setlistform.getPagination() != null)
				{
					this.setlistform.getPagination().firstPage();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}
	
	public ArrayList getDataList()
	{
		return dataList;
	}

	public void setDataList(ArrayList dataList)
	{
		this.dataList = dataList;
	}

	public String getObject_type()
	{
		return object_type;
	}

	public void setObject_type(String object_type)
	{
		this.object_type = object_type;
	}

	public PaginationForm getSetlistform()
	{
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform)
	{
		this.setlistform = setlistform;
	}

	public String getYear()
	{
		return year;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public ArrayList getYearList()
	{
		return yearList;
	}

	public void setYearList(ArrayList yearList)
	{
		this.yearList = yearList;
	}

	public String getMonth()
	{
		return month;
	}

	public void setMonth(String month)
	{
		this.month = month;
	}

	public String getQuarter()
	{
		return quarter;
	}

	public void setQuarter(String quarter)
	{
		this.quarter = quarter;
	}

	public String getScoreAjustHtml()
	{
		return scoreAjustHtml;
	}

	public void setScoreAjustHtml(String scoreAjustHtml)
	{
		this.scoreAjustHtml = scoreAjustHtml;
	}

	public String getObject_id()
	{
		return object_id;
	}

	public void setObject_id(String object_id)
	{
		this.object_id = object_id;
	}

	public String getPlan_id()
	{
		return plan_id;
	}

	public void setPlan_id(String plan_id)
	{
		this.plan_id = plan_id;
	}

	public String getAjustOper()
	{
		return ajustOper;
	}

	public void setAjustOper(String ajustOper)
	{
		this.ajustOper = ajustOper;
	}

	public String getShowGrpOrder() {
		return showGrpOrder;
	}

	public void setShowGrpOrder(String showGrpOrder) {
		this.showGrpOrder = showGrpOrder;
	}

	public ArrayList getMainBodyList() {
		return mainBodyList;
	}

	public void setMainBodyList(ArrayList mainBodyList) {
		this.mainBodyList = mainBodyList;
	}

	public String getAdjustEvalRange() {
		return adjustEvalRange;
	}

	public void setAdjustEvalRange(String adjustEvalRange) {
		this.adjustEvalRange = adjustEvalRange;
	}

	public String getObjectname() {
		return objectname;
	}

	public void setObjectname(String objectname) {
		this.objectname = objectname;
	}

}
