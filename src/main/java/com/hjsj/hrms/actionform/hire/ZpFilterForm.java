/*
 * Created on 2005-9-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ZpFilterForm</p>
 * <p>Description:人员筛选表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 23, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ZpFilterForm extends FrameForm {
    private String flag;
    /**查询字段列表*/
    private ArrayList fieldlist=new ArrayList();
    /**应用过滤条件*/
    private String dbcond="''";
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**查询条件*/
    private String strsql;
    /**查询条件*/
    private String strwhere;
    /**显示的字段名称*/
    private String columns;
    /**常用条件列表*/
    private ArrayList condlist=new ArrayList();
    /**选中的常用条件号*/
    private String curr_id;
    /**对历史记录进行查询*/
    private String history;
    /**人员单位及职位标识*/
    private String type;
    /**查询结果字段列表*/
    private String dbpre = "Usr";
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
    private String userbase = "Usr";
    private ArrayList resultlist=new ArrayList();
    
    private PaginationForm zpFilterForm=new PaginationForm();
    private PaginationForm zpBrowseForm=new PaginationForm();
    
    private String pos_id_value = "";
    
    String[] fieldsetvalue = null;
    
    String deptpossql = "";
    ArrayList zpsetlist=new ArrayList();
	ArrayList zpfieldlist=new ArrayList();
	ArrayList zpfieldvaluelist=new ArrayList();
    private String a0100;
    
    public ArrayList getResultlist() {
		return resultlist;
	}
	public void setResultlist(ArrayList resultlist) {
		this.resultlist = resultlist;
	}
	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getHistory() {
        return history;
    }
    public void setHistory(String history) {
        this.history = history;
    }

    
    public ZpFilterForm() {
        super();
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setDbcond((String)this.getFormHM().get("dbcond"));
        this.setStrsql((String)this.getFormHM().get("cond_sql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));
        //gquer常用查询*/
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setType((String)this.getFormHM().get("type"));
        this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
        this.setDbpre((String)this.getFormHM().get("dbpre"));
        this.setUserbase((String)this.getFormHM().get("userbase"));
        this.like="0";
        this.history="0";
        this.getZpFilterForm().setList((ArrayList)this.getFormHM().get("zpFilterlist"));
        this.setDeptpossql((String)this.getFormHM().get("deptpossql"));
	     this.setZpsetlist((ArrayList)this.getFormHM().get("zpsetlist"));
	     this.setZpfieldlist((ArrayList)this.getFormHM().get("zpfieldlist"));
	     this.setA0100((String)this.getFormHM().get("a0100"));
	     if(this.getFormHM().get("pos_id_value")!=null)
	     	this.setPos_id_value((String)this.getFormHM().get("pos_id_value"));
	    this.getZpBrowseForm().setList((ArrayList)this.getFormHM().get("zpfieldvaluelist"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("fieldlist",this.getFieldlist());
        this.getFormHM().put("like",this.getLike());
        this.getFormHM().put("curr_id",this.getCurr_id());
        this.getFormHM().put("history",this.getHistory());
        this.getFormHM().put("selectedlist",(ArrayList)this.getZpFilterForm().getSelectedList());
        if(this.getPagination()!=null)
 		   this.getFormHM().put("selecteda0100list",(ArrayList)this.getPagination().getSelectedList());
        this.getFormHM().put("fieldsetvalue",this.getFieldsetvalue());
        this.getFormHM().put("pos_id_value",this.getPos_id_value());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("a0100",this.getA0100());
    }
    public ArrayList getFieldlist() {
        return fieldlist;
    }
    public void setFieldlist(ArrayList fieldlist) {
        this.fieldlist = fieldlist;
    }
    public String getDbcond() {
        return dbcond;
    }
    public void setDbcond(String dbcond) {
        this.dbcond = dbcond;
    }
    public String getLike() {
        return like;
    }
    public void setLike(String like) {
        this.like = like;
    }

    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public String getStrsql() {
        return strsql;
    }
    public void setStrsql(String strsql) {
        this.strsql = strsql;
    }
    public String getStrwhere() {
        return strwhere;
    }
    public void setStrwhere(String strwhere) {
        this.strwhere = strwhere;
    }
    public ArrayList getCondlist() {
        return condlist;
    }
    public void setCondlist(ArrayList condlist) {
        this.condlist = condlist;
    }
    public String getCurr_id() {
        return curr_id;
    }
    public void setCurr_id(String curr_id) {
        this.curr_id = curr_id;
    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/hire/zp_filter/query".equals(arg0.getPath())&&arg1.getParameter("b_mquery")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }       
		return super.validate(arg0, arg1);
	}

	/**
	 * @return Returns the fieldsetvalue.
	 */
	public String[] getFieldsetvalue() {
		return fieldsetvalue;
	}
	/**
	 * @param fieldsetvalue The fieldsetvalue to set.
	 */
	public void setFieldsetvalue(String[] fieldsetvalue) {
		this.fieldsetvalue = fieldsetvalue;
	}
	/**
	 * @return Returns the zpFilterForm.
	 */
	public PaginationForm getZpFilterForm() {
		return zpFilterForm;
	}
	/**
	 * @param zpFilterForm The zpFilterForm to set.
	 */
	public void setZpFilterForm(PaginationForm zpFilterForm) {
		this.zpFilterForm = zpFilterForm;
	}
	/**
	 * @return Returns the pos_id_value.
	 */
	public String getPos_id_value() {
		return pos_id_value;
	}
	/**
	 * @param pos_id_value The pos_id_value to set.
	 */
	public void setPos_id_value(String pos_id_value) {
		this.pos_id_value = pos_id_value;
	}
	/**
	 * @return Returns the dbpre.
	 */
	public String getDbpre() {
		return dbpre;
	}
	/**
	 * @param dbpre The dbpre to set.
	 */
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	/**
	 * @return Returns the deptpossql.
	 */
	public String getDeptpossql() {
		return deptpossql;
	}
	/**
	 * @param deptpossql The deptpossql to set.
	 */
	public void setDeptpossql(String deptpossql) {
		this.deptpossql = deptpossql;
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
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the zpfieldlist.
	 */
	public ArrayList getZpfieldlist() {
		return zpfieldlist;
	}
	/**
	 * @param zpfieldlist The zpfieldlist to set.
	 */
	public void setZpfieldlist(ArrayList zpfieldlist) {
		this.zpfieldlist = zpfieldlist;
	}
	/**
	 * @return Returns the zpsetlist.
	 */
	public ArrayList getZpsetlist() {
		return zpsetlist;
	}
	/**
	 * @param zpsetlist The zpsetlist to set.
	 */
	public void setZpsetlist(ArrayList zpsetlist) {
		this.zpsetlist = zpsetlist;
	}
	public ArrayList getZpfieldvaluelist() {
		return zpfieldvaluelist;
	}
	public void setZpfieldvaluelist(ArrayList zpfieldvaluelist) {
		this.zpfieldvaluelist = zpfieldvaluelist;
	}
	public PaginationForm getZpBrowseForm() {
		return zpBrowseForm;
	}
	public void setZpBrowseForm(PaginationForm zpBrowseForm) {
		this.zpBrowseForm = zpBrowseForm;
	}
}
