package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:PerformancePlanForm.java</p>
 * <p>Description:考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-26 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PerformancePlanForm extends FrameForm
{
	
    private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
	private String name;
	private String status;
	private String method;
	private String object_type;
	private ArrayList dataList = new ArrayList();	
	private ArrayList statusList = new ArrayList();	
	private PaginationForm setlistform = new PaginationForm();
	private String jxmodul = "";  // 1:考核实施 2:绩效评估 3: 数据采集
	
	private String obtype = "";
	private String scrollValue = "";//记录事件列表滚动条的位置
	
	
	public String getScrollValue() {
		return scrollValue;
	}

	public void setScrollValue(String scrollValue) {
		this.scrollValue = scrollValue;
	}

	public String getObtype() {
		return obtype;
	}

	public void setObtype(String obtype) {
		this.obtype = obtype;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		try
		{
			if ("/performance/kh_plan/performPlanList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && ("link".equals(arg1.getParameter("b_query"))|| "query".equals(arg1.getParameter("b_query"))))
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

	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("busitype", this.getBusitype());
		this.getFormHM().put("statusList", this.getStatusList());
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("status", this.getStatus());
		this.getFormHM().put("method", this.getMethod());
		this.getFormHM().put("object_type", this.getObject_type());
		this.getFormHM().put("dataList", this.getDataList());
		this.getFormHM().put("jxmodul", this.getJxmodul());
		this.getFormHM().put("obtype", this.getObtype());
		this.getFormHM().put("scrollValue", this.getScrollValue());
	}

	@Override
    public void outPutFormHM()
	{
		this.setBusitype((String)this.getFormHM().get("busitype"));
		this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.setStatusList((ArrayList) this.getFormHM().get("statusList"));
		this.setName((String) this.getFormHM().get("name"));
		this.setStatus((String) this.getFormHM().get("status"));
		this.setMethod((String) this.getFormHM().get("method"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setDataList((ArrayList) this.getFormHM().get("dataList"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("dataList"));
		this.setJxmodul((String) this.getFormHM().get("jxmodul"));
		this.setObtype((String)this.getFormHM().get("obtype"));
		this.setScrollValue((String)this.getFormHM().get("scrollValue"));
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getObject_type()
	{
		return object_type;
	}

	public void setObject_type(String object_type)
	{
		this.object_type = object_type;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public ArrayList getDataList()
	{
		return dataList;
	}

	public void setDataList(ArrayList dataList)
	{
		this.dataList = dataList;
	}

	public PaginationForm getSetlistform()
	{
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform)
	{
		this.setlistform = setlistform;
	}

	public ArrayList getStatusList()
	{
		return statusList;
	}

	public void setStatusList(ArrayList statusList)
	{
		this.statusList = statusList;
	}

	public String getJxmodul()
	{
		return jxmodul;
	}

	public void setJxmodul(String jxmodul)
	{
		this.jxmodul = jxmodul;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

}
