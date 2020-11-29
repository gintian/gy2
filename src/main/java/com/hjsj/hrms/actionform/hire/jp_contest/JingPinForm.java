package com.hjsj.hrms.actionform.hire.jp_contest;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 
 *<p>Title:JingPinForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class JingPinForm extends FrameForm 
{
	/**表名称*/
	private String tablename;
	/**状态标识*/
	private String state;
	/**指标列表*/
    private ArrayList fieldlist=new ArrayList();
    /**查询语名*/
    private String sql;
    /**员工网上申请*/
    private ArrayList applylist=new ArrayList();
    
    private String selectsql;
    
    private String wheresql;
    
    private String column;
    /**申请标识*/
    private String applyflag;
    /**上传材料*/
    private FormFile uploadfile;
    /**申请职位*/
    private String postion;
    /**被申请职位的部门*/
    private String pos_parent;
    /**分页*/
    private int current=1;
    private PaginationForm recordListForm=new PaginationForm(); 
    
    /**申请材料*/
    private ArrayList apply_file_list; 
    /**上传材料文件名*/
    private String filetitle;
    /**上传材料路径*/
    private String filepath;
    
    private String z0700;
    /**申请职位*/
    private String posid;
    /**所有职位*/
    private ArrayList allApplyPos = new ArrayList();  
    /**申请状态*/
    private String applystate; 
    
    private String a0100;
    
    private String nbase;
    
    private String userpriv; 
    
    private String returnflag; 
    
    private String maxpos;
    
    private String choicepos;
    
    
	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("sql",this.getSql());
		this.getFormHM().put("uploadfile",this.getUploadfile());
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		this.getFormHM().put("filepath",this.getFilepath());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("returnflag",this.getReturnflag());
	}
	
	@Override
    public void outPutFormHM() {
		
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setState((String)this.getFormHM().get("state"));
		this.setApplylist((ArrayList)this.getFormHM().get("applylist"));
		this.setSelectsql((String)this.getFormHM().get("selectsql"));
		this.setWheresql((String)this.getFormHM().get("wheresql"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setApplyflag((String)this.getFormHM().get("applyflag"));
		this.setPostion((String)this.getFormHM().get("postion"));
		this.setPos_parent((String)this.getFormHM().get("pos_parent"));		
		this.setApply_file_list((ArrayList)this.getFormHM().get("apply_file_list"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("apply_file_list"));
		this.getRecordListForm().getPagination().gotoPage(current);
		this.setFilepath((String)this.getFormHM().get("filepath"));
		this.setFiletitle((String)this.getFormHM().get("filetitle"));
		this.setZ0700((String)this.getFormHM().get("z0700"));
		this.setPosid((String)this.getFormHM().get("posid"));
		this.setAllApplyPos((ArrayList)this.getFormHM().get("allApplyPos"));
		this.setApplystate((String)this.getFormHM().get("applystate"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setUserpriv((String)this.getFormHM().get("userpriv"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setMaxpos((String)this.getFormHM().get("maxpos"));
		this.setChoicepos((String)this.getFormHM().get("choicepos"));
	}
	

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
	    if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();
        }	
	    if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	 current=this.recordListForm.getPagination().getCurrent();
        }  
       
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
         return super.validate(arg0, arg1);
	}

	
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getApplylist() {
		return applylist;
	}

	public void setApplylist(ArrayList applylist) {
		this.applylist = applylist;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public String getWheresql() {
		return wheresql;
	}

	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getApplyflag() {
		return applyflag;
	}

	public void setApplyflag(String applyflag) {
		this.applyflag = applyflag;
	}

	public FormFile getUploadfile() {
		return uploadfile;
	}

	public void setUploadfile(FormFile uploadfile) {
		this.uploadfile = uploadfile;
	}

	public String getPostion() {
		return postion;
	}

	public void setPostion(String postion) {
		this.postion = postion;
	}

	public String getPos_parent() {
		return pos_parent;
	}

	public void setPos_parent(String pos_parent) {
		this.pos_parent = pos_parent;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getApply_file_list() {
		return apply_file_list;
	}

	public void setApply_file_list(ArrayList apply_file_list) {
		this.apply_file_list = apply_file_list;
	}

	public String getFiletitle() {
		return filetitle;
	}

	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getZ0700() {
		return z0700;
	}

	public void setZ0700(String z0700) {
		this.z0700 = z0700;
	}

	public String getPosid() {
		return posid;
	}

	public void setPosid(String posid) {
		this.posid = posid;
	}

	public ArrayList getAllApplyPos() {
		return allApplyPos;
	}

	public void setAllApplyPos(ArrayList allApplyPos) {
		this.allApplyPos = allApplyPos;
	}

	public String getApplystate() {
		return applystate;
	}

	public void setApplystate(String applystate) {
		this.applystate = applystate;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getUserpriv() {
		return userpriv;
	}

	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}

	@Override
    public String getReturnflag() {
		return returnflag;
	}

	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}

	public String getMaxpos() {
		return maxpos;
	}

	public void setMaxpos(String maxpos) {
		this.maxpos = maxpos;
	}

	public String getChoicepos() {
		return choicepos;
	}

	public void setChoicepos(String choicepos) {
		this.choicepos = choicepos;
	}




}
