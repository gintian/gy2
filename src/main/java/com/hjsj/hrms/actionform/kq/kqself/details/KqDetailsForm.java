package com.hjsj.hrms.actionform.kq.kqself.details;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class KqDetailsForm extends FrameForm {
	
	private String kq_year;
	
	private String flag="1";
	
	private String kq_years;
	
	private String tem;
	
    private String sql;
	
	private String where;
	
	private String com;
	
	private String mess;
	
	private String check;
	
	private ArrayList plist=new ArrayList();
	
	private ArrayList elist=new ArrayList();
	
	private ArrayList flist=new ArrayList();
	
    private ArrayList slist=new ArrayList();
    
    private ArrayList tlist =new ArrayList();
    
	private RecordVo duration=new RecordVo("Q05");
	 
	private PaginationForm kqDetailsForm=new PaginationForm(); 
	private String orderby;
	/** 考勤规则**/
	private HashMap kqItem_hash=new HashMap();
	
	private String have_accepted;
	
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getKqDetailsForm().setList((ArrayList)this.getFormHM().get("detailslist"));
		 this.setDuration((RecordVo)this.getFormHM().get("duration"));
		 this.setKq_year((String)this.getFormHM().get("kq_year"));
		 this.setKq_years((String)this.getFormHM().get("kq_years"));
		 this.setSlist((ArrayList)this.getFormHM().get("slist"));
		 this.setTlist((ArrayList)this.getFormHM().get("tlist"));
		 this.setWhere((String)this.getFormHM().get("where"));
		 this.setCom((String)this.getFormHM().get("com"));
		 this.setMess((String)this.getFormHM().get("mess"));
		 this.setSql((String)this.getFormHM().get("sql"));
		 this.setFlist((ArrayList)this.getFormHM().get("flist"));
		 this.setElist((ArrayList)this.getFormHM().get("elist"));
		 this.setPlist((ArrayList)this.getFormHM().get("plist"));
		 this.setCheck((String)this.getFormHM().get("check"));
		// System.out.println("==00888=="+((ArrayList)this.getFormHM().get("slist")).toString());
		 this.setOrderby((String)this.getFormHM().get("orderby"));
		 this.setTem((String)this.getFormHM().get("tem"));
		 this.setKqItem_hash((HashMap)this.getFormHM().get("kqItem_hash"));
		 this.setHave_accepted((String)this.getFormHM().get("have_accepted"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",(ArrayList)this.getKqDetailsForm().getSelectedList());
	     this.getFormHM().put("duration",this.getDuration());
		 this.getFormHM().put("kq_year",this.getKq_year());
		 this.getFormHM().put("kq_years",this.getKq_years());
		 this.getFormHM().put("tem",(String)this.getTem());
		 this.getFormHM().put("flag",(String)this.getFlag());
		 this.getFormHM().put("have_accepted", this.getHave_accepted());

	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
	       
        if("/kq/kqself/details/month_details".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            //this.getDuration().clearValues();   
        	if(this.getPagination()!=null)
  	          this.getPagination().firstPage();//?	
            this.setFlag("1");
        }
        if("/kq/kqself/details/month_details".equals(arg0.getPath())&&arg1.getParameter("b_more")!=null)
        {         
            this.setFlag("2");
        }
        if("/kq/kqself/details/kq_details".equals(arg0.getPath())&&arg1.getParameter("b_more")!=null)
        {         
        	if(this.getPagination()!=null)
  	          this.getPagination().firstPage();//?	
            this.setFlag("3");
        }
        if("/kq/kqself/details/kq_details".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {         
        	if(this.getPagination()!=null)
  	          this.getPagination().firstPage();//?	            
        }       
        return super.validate(arg0, arg1);
    }

	
	public String getHave_accepted() {
		return have_accepted;
	}

	public void setHave_accepted(String haveAccepted) {
		have_accepted = haveAccepted;
	}

	public RecordVo getDuration() {
		return duration;
	}

	public void setDuration(RecordVo duration) {
		this.duration = duration;
	}

	public String getKq_year() {
		return kq_year;
	}

	public void setKq_year(String kq_year) {
		this.kq_year = kq_year;
	}

	public PaginationForm getKqDetailsForm() {
		return kqDetailsForm;
	}

	public void setKqDetailsForm(PaginationForm kqDetailsForm) {
		this.kqDetailsForm = kqDetailsForm;
	}

	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	}

	public String getTem() {
		return tem;
	}

	public void setTem(String tem) {
		this.tem = tem;
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}

	public String getKq_years() {
		return kq_years;
	}

	public void setKq_years(String kq_years) {
		this.kq_years = kq_years;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public ArrayList getFlist() {
		return flist;
	}

	public void setFlist(ArrayList flist) {
		this.flist = flist;
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

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public ArrayList getElist() {
		return elist;
	}

	public void setElist(ArrayList elist) {
		this.elist = elist;
	}

	public ArrayList getPlist() {
		return plist;
	}

	public void setPlist(ArrayList plist) {
		this.plist = plist;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public HashMap getKqItem_hash() {
		return kqItem_hash;
	}

	public void setKqItem_hash(HashMap kqItem_hash) {
		this.kqItem_hash = kqItem_hash;
	}

}
