/*
 * Created on 2005-7-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:QueryLogForm</p>
 * <p>Description:查询日志表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:July 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class QueryLogForm extends FrameForm {

	private String commitor;
	private String name;
	private String beginexectime;
	private String endexectime;
	private String strsql;
    /**需显示的指标串*/
    private String columns;	
	/**
     * 日志对象
     */
	private RecordVo queryLogvo = new RecordVo("fr_txlog");
	 /**
     * 日志对象列表
     */
    private PaginationForm queryLogForm=new PaginationForm(); 
    /**
     * 日志查询日期
     * jingq add 2014.09.15
     */
    private String beginexectimeend;
    private String endexectimeend;
    
    public String getBeginexectimeend() {
		return beginexectimeend;
	}

	public void setBeginexectimeend(String beginexectimeend) {
		this.beginexectimeend = beginexectimeend;
	}

	public String getEndexectimeend() {
		return endexectimeend;
	}

	public void setEndexectimeend(String endexectimeend) {
		this.endexectimeend = endexectimeend;
	}

	/* （非 Javadoc）
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		
		this.setQueryLogvo((RecordVo)this.getFormHM().get("queryLogvo")); 
        this.getQueryLogForm().setList((ArrayList)this.getFormHM().get("queryLoglist"));
        //this.setCommitor((String)this.getFormHM().get("commitor"));
        //this.setCommitor((String)this.getFormHM().get("name"));
        //this.setCommitor((String)this.getFormHM().get("beginexectime"));
        //this.setCommitor((String)this.getFormHM().get("endexectime"));
    
        this.setStrsql((String)this.getFormHM().get("strsql"));
        
        this.setBeginexectimeend((String) this.getFormHM().get("beginexectimeend"));
        this.setEndexectimeend((String) this.getFormHM().get("endexectimeend"));
	}

	/* （非 Javadoc）
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		    //this.getFormHM().put("selectedlist",(ArrayList)this.getQueryLogForm().getSelectedList());
            this.getFormHM().put("queryLogvo",this.getQueryLogvo());
        	this.getFormHM().put("commitor",this.getCommitor());
        	this.getFormHM().put("name",this.getName());
        	this.getFormHM().put("beginexectime",this.getBeginexectime());
        	this.getFormHM().put("endexectime",this.getEndexectime());
     	    if(this.getPagination()!=null)        
     	          this.getFormHM().put("selectedlist",this.getPagination().getSelectedList());
        	
     	    this.getFormHM().put("beginexectimeend", this.getBeginexectimeend());
     	    this.getFormHM().put("endexectimeend", this.getEndexectimeend());
    }
	/**
     * @return Returns the proposeForm.
     */
    public PaginationForm getQueryLogForm() {
        return this.queryLogForm;
    }
    /**
     * @param proposeForm The proposeForm to set.
     */
    public void setQueryLogForm(PaginationForm queryLogForm) {
        this.queryLogForm = queryLogForm;
    }
    /**
     * @return Returns the proposevo.
     */
    public RecordVo getQueryLogvo() {
        return this.queryLogvo;
    }
    /**
     * @param proposevo The proposevo to set.
     */
    public void setQueryLogvo(RecordVo queryLogvo) {
        this.queryLogvo = queryLogvo;
    }
    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	this.setCommitor("");
    	this.setName("");
    	this.setBeginexectime("");
    	this.setEndexectime("");
    	this.setBeginexectimeend("");
    	this.setEndexectimeend("");
       // super.reset(arg0, arg1);
        
    }
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if(this.getPagination()!=null)
            this.getPagination().firstPage();//?    	
        return super.validate(arg0, arg1);
    }

	/**
	 * @return Returns the beginexectime.
	 */
	public String getBeginexectime() {
		return beginexectime;
	}
	/**
	 * @param beginexectime The beginexectime to set.
	 */
	public void setBeginexectime(String beginexectime) {
		this.beginexectime = beginexectime;
	}
	/**
	 * @return Returns the commitor.
	 */
	public String getCommitor() {
		return commitor;
	}
	/**
	 * @param commitor The commitor to set.
	 */
	public void setCommitor(String commitor) {
		this.commitor = commitor;
	}
	/**
	 * @return Returns the endexectime.
	 */
	public String getEndexectime() {
		return endexectime;
	}
	/**
	 * @param endexectime The endexectime to set.
	 */
	public void setEndexectime(String endexectime) {
		this.endexectime = endexectime;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}
}
