/*
 * Created on 2005-9-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:InfoMlrframeForm</p>
 * <p>Description:面试材料维护表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 03, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class InfoMlrframeForm extends FrameForm {
	
	private FormFile file;
	 /**查询条件串*/
    private String cond_str="";
    /**查询串*/
    private String sql_str;
    /**需显示的指标串*/
    private String columns;
    private String dbpre="usr";
    private ArrayList dblist=new ArrayList();
    /**员工自助的表名*/
    private String tablename="UsrA01";
    
    public ArrayList getDblist() {
        return dblist;
    }
    public void setDblist(ArrayList dblist) {
        this.dblist = dblist;
    }
    /**登录用户对象*/
    private RecordVo user_vo=new RecordVo("UsrA01");
    /**面试材料对象*/
    private RecordVo testQuestionvo=new RecordVo("ZP_POS_TEST");
    /**面试材料对象列表*/
    private PaginationForm testQuestionForm=new PaginationForm();
    /**用户号标识*/
    private String user_id;
    /**状态标识，用于标识返回的页面的*/
    private String status="0";
    
    private String pos_id = "";
    
    private String flag = "0";
    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
       this.setUser_vo((RecordVo)this.getFormHM().get("user_vo"));
       this.setCond_str((String)this.getFormHM().get("cond_str"));
       this.getTestQuestionForm().setList((ArrayList)this.getFormHM().get("testQuestionlist"));
       this.setTestQuestionvo((RecordVo)this.getFormHM().get("testQuestionvo"));
       this.setUser_id((String)this.getFormHM().get("user_id"));
       this.setStatus((String)this.getFormHM().get("userflag"));
       this.setSql_str((String)this.getFormHM().get("sql_str"));
       this.setColumns((String)this.getFormHM().get("columns"));
       this.setFlag((String)this.getFormHM().get("flag"));
       this.setDblist((ArrayList)this.getFormHM().get("dblist"));
       this.setTablename((String)this.getFormHM().get("tablename"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("selectedlist",this.getTestQuestionForm().getSelectedList());
        this.getFormHM().put("testQuestionvo",this.getTestQuestionvo());
        this.getFormHM().put("user_id",this.getUser_id());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("flag",flag);
        this.getFormHM().put("file",this.getFile());
    }

    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public String getSql_str() {
        return sql_str;
    }
    public void setSql_str(String sql_str) {
        this.sql_str = sql_str;
    }    
    /**
     * @return Returns the cond_str.
     */
    public String getCond_str() {
        return cond_str;
    }
    /**
     * @param cond_str The cond_str to set.
     */
    public void setCond_str(String cond_str) {
        this.cond_str = cond_str;
    }
    /**
     * @return Returns the user_vo.
     */
    public RecordVo getUser_vo() {
        return user_vo;
    }
    /**
     * @param user_vo The user_vo to set.
     */
    public void setUser_vo(RecordVo user_vo) {
        this.user_vo = user_vo;
        
    }
    public String getDbpre() {
        return dbpre;
    }
    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
    }    
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
    	 /**
         * 新建面试材料
         */
        if("/hire/zp_options/informationlist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.getTestQuestionvo().clearValues();
        }
        /**
         * 编辑面试材料
         */
        if("/hire/zp_options/upload_file".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
        }
        return super.validate(arg0, arg1);
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	/**
	 * @return Returns the testQuestionForm.
	 */
	public PaginationForm getTestQuestionForm() {
		return testQuestionForm;
	}
	/**
	 * @param testQuestionForm The testQuestionForm to set.
	 */
	public void setTestQuestionForm(PaginationForm testQuestionForm) {
		this.testQuestionForm = testQuestionForm;
	}
	/**
	 * @return Returns the pos_id.
	 */
	public String getPos_id() {
		return pos_id;
	}
	/**
	 * @param pos_id The pos_id to set.
	 */
	public void setPos_id(String pos_id) {
		this.pos_id = pos_id;
	}
	/**
	 * @return Returns the testQuestionvo.
	 */
	public RecordVo getTestQuestionvo() {
		return testQuestionvo;
	}
	/**
	 * @param testQuestionvo The testQuestionvo to set.
	 */
	public void setTestQuestionvo(RecordVo testQuestionvo) {
		this.testQuestionvo = testQuestionvo;
	}
	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return Returns the file.
	 */
	public FormFile getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(FormFile file) {
		this.file = file;
	}
}
