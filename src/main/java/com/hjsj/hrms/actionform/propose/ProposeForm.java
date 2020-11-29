package com.hjsj.hrms.actionform.propose;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author chenmengqing
 */
public class ProposeForm extends FrameForm {
    
	String userAdmin="false";
	String manager="false";
	String replayCheck="";
	String ctrl_return="1";
	String bread="";
	String start_date="";
	String end_date="";
	String date_flag="0"; //是否需要按日期查询。"0"为否，"1"为是
	public String getDate_flag() {
		return date_flag;
	}
	public void setDate_flag(String dateFlag) {
		date_flag = dateFlag;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String startDate) {
		start_date = startDate;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String endDate) {
		end_date = endDate;
	}
	public String getCtrl_return() {
		return ctrl_return;
	}
	public void setCtrl_return(String ctrl_return) {
		this.ctrl_return = ctrl_return;
	}
	/**
	 * @return 返回 replayCheck。
	 */
	public String getReplayCheck() {
		return replayCheck;
	}
	/**
	 * @param replayCheck 要设置的 replayCheck。
	 */
	public void setReplayCheck(String replayCheck) {
		this.replayCheck = replayCheck;
	}
	/**
	 * @return 返回 manager。
	 */
	public String getManager() {
		return manager;
	}
	/**
	 * @param manager 要设置的 manager。
	 */
	public void setManager(String manager) {
		this.manager = manager;
	}
	/**
	 * 新建及编辑功能控制
	 */
    private String flag="0";
	/**
	 * @return 返回 userAdmin。
	 */
    private String check="";
	/**
	 * @return 返回 check。
	 */
	public String getCheck() {
		return check;
	}
	/**
	 * @param check 要设置的 check。
	 */
	public void setCheck(String check) {
		this.check = check;
	}
	public String getUserAdmin() {
		return userAdmin;
	}
	/**
	 * @param userAdmin 要设置的 userAdmin。
	 */
	public void setUserAdmin(String userAdmin) {
		this.userAdmin = userAdmin;
	}
    /**
     * 匿名
     */
    private String annoymous="1";
    /**
     * 建议对象
     */
    private RecordVo proposevo=new RecordVo("SUGGEST");
    /**
     * 建议对象列表
     */
    private PaginationForm proposeForm=new PaginationForm();     
    /**
     * 
     */
    
    
    public ProposeForm() {
    	
    }
    
  
    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
	public void outPutFormHM()
    {
        this.setProposevo((RecordVo)this.getFormHM().get("proposevo"));
        this.getProposeForm().setList((ArrayList)this.getFormHM().get("proposelist"));
        this.setUserAdmin(Boolean.toString(this.userView.isSuper_admin()));
        this.setManager(Boolean.toString(this.userView.isManager()));
        this.setCheck(this.getFormHM().get("check").toString());
        this.setReplayCheck(this.getFormHM().get("replayCheck").toString());
        this.setStart_date(this.getFormHM().get("start_date").toString());
        this.setEnd_date(this.getFormHM().get("end_date").toString());
        this.setDate_flag(this.getFormHM().get("date_flag").toString());
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
	public void inPutTransHM() {
	    this.getFormHM().put("selectedlist",(ArrayList)this.getProposeForm().getSelectedList());
        this.getFormHM().put("proposevo",this.getProposevo());
	    this.getFormHM().put("flag",this.getFlag());
	    this.getFormHM().put("userAdmin",this.getUserAdmin());
	    this.getFormHM().put("check",this.getCheck());
	    this.getFormHM().put("replayCheck",this.getReplayCheck());
	    this.getFormHM().put("bread", this.getBread());
	   this.getFormHM().put("start_date", this.getStart_date());
	   this.getFormHM().put("end_date", this.getEnd_date());
	   this.getFormHM().put("date_flag", this.getDate_flag());
    }

    /**
     * @return Returns the proposeForm.
     */
    public PaginationForm getProposeForm() {
        return proposeForm;
    }
    /**
     * @param proposeForm The proposeForm to set.
     */
    public void setProposeForm(PaginationForm proposeForm) {
        this.proposeForm = proposeForm;
    }
    /**
     * @return Returns the proposevo.
     */
    public RecordVo getProposevo() {
        return proposevo;
    }
    /**
     * @param proposevo The proposevo to set.
     */
    public void setProposevo(RecordVo proposevo) {
        this.proposevo = proposevo;
    }
    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
	public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	
    	this.annoymous="";
    	this.ctrl_return="1";
       // super.reset(arg0, arg1);
        
    }
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
    	
    	
        /**
         * 新建
         */
        if("/selfservice/propose/searchpropose".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.getProposevo().clearValues();
        }
        /**
         * 编辑
         */
        if("/selfservice/propose/addpropose".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
        }
        /**
         * 答复
         */
        if("/selfservice/propose/replypropose".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("2");
        }
        /**
         * 查阅答复
         */
        if("/selfservice/propose/viewpropose".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("2");
        }         
        return super.validate(arg0, arg1);
    }
    /**
     * @return Returns the annoymous.
     */
    public String getAnnoymous() {
        return annoymous;
    }
    /**
     * @param annoymous The annoymous to set.
     */
    public void setAnnoymous(String annoymous) {
        this.annoymous = annoymous;
    }
    /**
     * @return Returns the flag.
     */
    public String getFlag() {
        return date_flag;
    }
    /**
     * @param flag The flag to set.
     */
    public void setFlag(String flag) {
        this.date_flag = flag;
    }
	public String getBread() {
		return bread;
	}
	public void setBread(String bread) {
		this.bread = bread;
	}
    
}
