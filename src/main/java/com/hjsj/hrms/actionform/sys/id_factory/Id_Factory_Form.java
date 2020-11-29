package com.hjsj.hrms.actionform.sys.id_factory;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class Id_Factory_Form extends FrameForm {
	private RecordVo idvo = new RecordVo("id_factory");
	private String old_sequence_name;
	/**
	 * 查询方式
	 * =1系统
	 * =2用户自定
	 */
	private String sysorclient="1";
	/***
	 * 新增＼及更新标志
	 * =add
	 * =update
	 */
	private String updateflag="add";
	/**序号名*/
	private String sequence_name;
	
	private String searchflag;
//	分叶显示
	  private String sql;
	  private String where;
	  private String column;
	  private String orderby;

	  private PaginationForm pageListForm = new PaginationForm();
	  private String edition;
	  private ArrayList dblist = new ArrayList();

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	@Override
    public void outPutFormHM() {
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setIdvo((RecordVo) this.getFormHM().get("idvo"));
		this.setOld_sequence_name((String)this.getFormHM().get("old_sequence_name"));
//		add new code
		this.setSearchflag("");
		this.setEdition((String)this.getFormHM().get("edition"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
	}

	@Override
    public void inPutTransHM() {
		/**chenmengqing added*/
		this.getFormHM().put("sysorclient",this.getSysorclient());
		this.getFormHM().put("updateflag",this.getUpdateflag());
		this.getFormHM().put("idvo", this.getIdvo());
		this.getFormHM().put("old_sequence_name", old_sequence_name);
		this.getFormHM().put("sequence_name",this.getSequence_name());
		if(this.getPagination()!=null)
			this.getFormHM().put("sel_and_del",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("searchflag",this.getSearchflag());

	}

	public RecordVo getIdvo() {
		return idvo;
	}

	public void setIdvo(RecordVo idvo) {
		this.idvo = idvo;
	}


	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String  path=arg0.getPath();
		String  add=arg1.getParameter("b_add");
		if("/system/id_factory/seq_show".equalsIgnoreCase(path)&&add!=null)
			this.setUpdateflag("add");
		if("/system/id_factory/id_factoryupdateoradd".equalsIgnoreCase(path)&&arg1.getParameter("b_query")!=null)
			this.setUpdateflag("update");	
		if("/system/id_factory/seq_show".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}


	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}


	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getSysorclient() {
		return sysorclient;
	}

	public void setSysorclient(String sysorclient) {
		this.sysorclient = sysorclient;
	}

	public String getUpdateflag() {
		return updateflag;
	}

	public void setUpdateflag(String updateflag) {
		this.updateflag = updateflag;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		//this.setUpdateflag("");
		super.reset(arg0, arg1);
	}

	public String getSequence_name() {
		return sequence_name;
	}

	public void setSequence_name(String sequence_name) {
		this.sequence_name = sequence_name;
	}

	public String getSearchflag() {
		return searchflag;
	}

	public void setSearchflag(String searchflag) {
		this.searchflag = searchflag;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getOld_sequence_name() {
		return old_sequence_name;
	}

	public void setOld_sequence_name(String old_sequence_name) {
		this.old_sequence_name = old_sequence_name;
	}


}
