package com.hjsj.hrms.actionform.kq.kqself.plan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqPlanInfoForm extends FrameForm {
   private String sql;
	
	private String where;
	private ArrayList approvelist= new ArrayList();	
	private String a_code;
	
	
	private String com;
	
	private String year;
	private String param;
	private ArrayList tlist =new ArrayList();
	private ArrayList flist=new ArrayList();
	private ArrayList slist =new ArrayList();
	private ArrayList onelist =new ArrayList();
	private PaginationForm recordListForm=new PaginationForm();
	private ArrayList q29z0list=new ArrayList();
    private ArrayList kq_list=new ArrayList();//人员库
	private String select_name;//筛选名字
	private String select_flag;//筛选表示
	private String select_pre;
	private ArrayList kq_dbase_list=new ArrayList();
	private String plan_id;
	private String apply_id;
	public String getApply_id() {
		return apply_id;
	}

	public void setApply_id(String apply_id) {
		this.apply_id = apply_id;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}

	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFlist((ArrayList)this.getFormHM().get("flist"));
		 this.setWhere((String)this.getFormHM().get("where"));
		 this.setCom((String)this.getFormHM().get("com"));
		 this.setSql((String)this.getFormHM().get("sql"));
		 this.setTlist((ArrayList)this.getFormHM().get("tlist"));
		 this.setSlist((ArrayList)this.getFormHM().get("slist"));
         this.setYear((String)this.getFormHM().get("year"));
         this.setOnelist((ArrayList)this.getFormHM().get("onelist"));
         this.setApprovelist((ArrayList)this.getFormHM().get("approvelist"));
         this.setQ29z0list((ArrayList)this.getFormHM().get("q29z0list"));
         this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
         this.setSelect_name((String)this.getFormHM().get("select_name"));
         this.setSelect_flag((String)this.getFormHM().get("select_flag"));
         this.setSelect_pre((String)this.getFormHM().get("select_pre"));
         this.setA_code((String)this.getFormHM().get("a_code"));
         this.setPlan_id((String)this.getFormHM().get("plan_id"));
         this.setApply_id((String)this.getFormHM().get("apply_id"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("year",(String)this.getYear());
        this.getFormHM().put("onelist",this.getOnelist());
        this.getFormHM().put("approvelist",this.getApprovelist());
        this.getFormHM().put("param",this.getParam());
        if(this.getPagination()!=null)			
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
        this.getFormHM().put("select_name",this.getSelect_name());
        this.getFormHM().put("select_flag",this.getSelect_flag());
        this.getFormHM().put("select_pre",this.getSelect_pre());
        this.getFormHM().put("a_code",this.getA_code());
        this.getFormHM().put("plan_id",this.getPlan_id());
        this.getFormHM().put("apply_id",this.getApply_id());
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}


	

	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getFlist() {
		return flist;
	}

	public void setFlist(ArrayList flist) {
		this.flist = flist;
	}

	public ArrayList getOnelist() {
		return onelist;
	}

	public void setOnelist(ArrayList onelist) {
		this.onelist = onelist;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ArrayList getApprovelist() {
		return approvelist;
	}

	public void setApprovelist(ArrayList approvelist) {
		this.approvelist = approvelist;
	}

	public ArrayList getQ29z0list() {
		return q29z0list;
	}

	public void setQ29z0list(ArrayList q29z0list) {
		this.q29z0list = q29z0list;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getKq_list() {
		return kq_list;
	}

	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}

	public String getSelect_flag() {
		return select_flag;
	}

	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public String getSelect_pre() {
		return select_pre;
	}

	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	 	 if("/kq/kqself/plan/annual_plan_institute".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	     {
	            if(this.getPagination()!=null)
	            this.getPagination().firstPage();
	     }
	 	if("/kq/kqself/plan/searchone".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	            if(this.getPagination()!=null)
	            this.getPagination().firstPage();
	            this.setSelect_flag("");
	            this.setSelect_name("");
	            this.setSelect_pre("");
	            this.getFormHM().put("select_name","");
	            this.getFormHM().put("select_flag","");
	            this.getFormHM().put("select_pre","");
	            this.getFormHM().put("kq_list",null);
	            this.setKq_list(null);
	            
	    }
	 	if("/kq/kqself/plan/searchoneplan".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	            if(this.getPagination()!=null)
	            this.getPagination().firstPage();	            
	    }
	 	if("/kq/kqself/plan/searchone_noput".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	            if(this.getPagination()!=null)
	            this.getPagination().firstPage();
	            this.setSelect_flag("");
	            this.setSelect_name("");
	            this.setSelect_pre("");
	            this.getFormHM().put("select_name","");
	            this.getFormHM().put("select_flag","");
	            this.getFormHM().put("select_pre","");
	            this.getFormHM().put("kq_list",null);
	            this.setKq_list(null);
	    }
	 	if("/kq/kqself/plan/searchone_noput_data".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	            if(this.getPagination()!=null)
	            this.getPagination().firstPage();	            
	    }
      return super.validate(arg0, arg1);
  }

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
}
