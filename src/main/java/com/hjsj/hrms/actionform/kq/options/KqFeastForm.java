/*
 * Created on 2006-12-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.kq.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KqFeastForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
       
        if("/kq/options/feast_type_list".equals(arg0.getPath())&&arg1.getParameter("br_add")!=null)
        {
            this.getFeast().clearValues();            
            this.setFlag("1");
        }
        if("/kq/options/add_feast_type".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
        }
        if("/kq/options/add_feast_type".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
        	  if(kqFeastForm.getPagination()!=null)
              {
              	if("1".equals(this.flag))
              		kqFeastForm.getPagination().lastPage();
                   current=kqFeastForm.getPagination().getCurrent(); 
              }
        	  
        }  
      
        
        return super.validate(arg0, arg1);
    }
  
    private RecordVo feast=new RecordVo("kq_feast");
	 private String  feast_id;
	 private String  feast_name;
	 private String  fmonth;
	 private String  fday;
	 private String fyear;
	 
	 private String sdate;
	 private String edate;
	 private String gw_flag;
     private String flag="0";
     private int current=1;
	 private ArrayList feastList =new ArrayList();
	 private ArrayList tlist =new ArrayList();
	 private PaginationForm kqFeastForm=new PaginationForm();
	 private String returnvalue="1";
	 
	 @Override
     public void outPutFormHM() {
		
		this.setFeast_id((String)this.getFormHM().get("feast_id"));
		this.setFeast_name((String)this.getFormHM().get("feast_name"));
		this.setFeastList((ArrayList)this.getFormHM().get("feastList"));
		this.setTlist((ArrayList)this.getFormHM().get("tlist"));
		this.getKqFeastForm().setList((ArrayList)this.getFormHM().get("feastList"));
		this.getKqFeastForm().getPagination().gotoPage(current);
		this.setFeast((RecordVo)this.getFormHM().get("feast"));
		this.setFlag((String)this.getFormHM().get("flag"));

		this.setSdate((String)this.getFormHM().get("sdate"));
		this.setEdate((String)this.getFormHM().get("edate"));
		this.setGw_flag((String)this.getFormHM().get("gw_flag"));
	}
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",(ArrayList)this.getKqFeastForm().getSelectedList());
		this.getFormHM().put("feast_id",this.feast_id);
		this.getFormHM().put("feast_name",this.feast_name);
		this.getFormHM().put("feastList",this.feastList);
		this.getFormHM().put("fday",this.fday);
		this.getFormHM().put("fmonth",this.fmonth);
		this.getFormHM().put("fyear",this.fyear);
		this.getFormHM().put("feast",this.feast);
		this.getFormHM().put("flag",this.flag);
		
		this.getFormHM().put("sdate",this.sdate);
		this.getFormHM().put("edate",this.edate);
		
		this.getFormHM().put("gw_flag", this.gw_flag);
	}

	/**
	 * @return Returns the fmonth.
	 */
	public String getFmonth() {
		return fmonth;
	}
	

	/**
	 * @param fmonth The fmonth to set.
	 */
	public void setFmonth(String fmonth) {
		this.fmonth = fmonth;
	}

	
	/**
	 * @param fday The fday to set.
	 */
	public  void setFday(String fday) {
		this.fday = fday;
		
	}
	/**
	 * @return Returns the fday.
	 */
	public String getFday() {
		return fday;
	}
	/**
	 * @return Returns the feast_name.
	 */
	public String getFeast_name() {
		return feast_name;
	}
	/**
	 * @param feast_name The feast_name to set.
	 */
	public void setFeast_name(String feast_name) {
		this.feast_name = feast_name;
	}
	/**
	 * @return Returns the feastList.
	 */
	public ArrayList getFeastList() {
		return feastList;
	}
	/**
	 * @param feastList The feastList to set.
	 */
	public void setFeastList(ArrayList feastList) {
		this.feastList = feastList;
	}

	public PaginationForm getKqFeastForm() {
		return kqFeastForm;
	}

	public void setKqFeastForm(PaginationForm kqFeastForm) {
		this.kqFeastForm = kqFeastForm;
	}

	public String getFeast_id() {
		return feast_id;
	}

	public void setFeast_id(String feast_id) {
		this.feast_id = feast_id;
	}


	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public RecordVo getFeast() {
		return feast;
	}

	public void setFeast(RecordVo feast) {
		this.feast = feast;
	}
	public String getFyear() {
		return fyear;
	}
	public void setFyear(String fyear) {
		this.fyear = fyear;
	}
	public ArrayList getTlist() {
		return tlist;
	}
	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}
	public String getEdate() {
		return edate;
	}
	public void setEdate(String edate) {
		this.edate = edate;
	}
	public String getSdate() {
		return sdate;
	}
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getGw_flag() {
		return gw_flag;
	}
	public void setGw_flag(String gwFlag) {
		gw_flag = gwFlag;
	}
	
}
