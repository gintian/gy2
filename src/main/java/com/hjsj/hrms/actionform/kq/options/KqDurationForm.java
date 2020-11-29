package com.hjsj.hrms.actionform.kq.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqDurationForm extends FrameForm {
	
	
	 private String flag="0";
	 
	 private String kq_year;
	 
	 private String kq_duration;
	 
     private String count;
     
     private String coun;
	 
     private String kyear;
     
     private String radio;
     
     private String month;
     
     private String dat;
     
     private String box;
     
     private String start;
     
     private String end;
     
     private String yue;
     
     private String mess="2";   //用于控制提示信息
     
     private String text;   //用于考勤年度text输入框的控制
     
     private String one_len;
     
     private RecordVo duration=new RecordVo("kq_duration");
	 
	 private PaginationForm kqDurationForm=new PaginationForm(); 
	 private String returnvalue="1";
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		 this.getKqDurationForm().setList((ArrayList)this.getFormHM().get("durationlist"));
		 this.setDuration((RecordVo)this.getFormHM().get("duration"));
		 this.setKq_year((String)this.getFormHM().get("kq_year"));
		 this.setKq_duration((String)this.getFormHM().get("kq_duration"));
		 this.setKyear((String)this.getFormHM().get("kyear"));
		 this.setText((String)this.getFormHM().get("text"));
		 this.setMess((String)this.getFormHM().get("mess"));
		 this.setFlag((String)this.getFormHM().get("flag"));
		 this.setCount((String)this.getFormHM().get("count"));
		 this.setBox((String)this.getFormHM().get("box"));
		 
		 this.setStart((String)this.getFormHM().get("start"));
		 this.setEnd((String)this.getFormHM().get("end"));
		 
		 this.setYue((String)this.getFormHM().get("yue"));
		 this.setRadio((String)this.getFormHM().get("radio"));
	     this.setOne_len((String)this.getFormHM().get("one_len"));
	     this.setMonth((String)this.getFormHM().get("month"));
	     this.setDat((String)this.getFormHM().get("dat"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		 this.getFormHM().put("selectedlist",(ArrayList)this.getKqDurationForm().getSelectedList());
	     this.getFormHM().put("duration",this.getDuration());
		 this.getFormHM().put("flag",this.getFlag());
		 this.getFormHM().put("kq_year",this.getKq_year());
		 this.getFormHM().put("count",this.getCount());
		 this.getFormHM().put("kyear",this.getKyear());
		 this.getFormHM().put("text",this.getText());
		 
		 this.getFormHM().put("month",this.getMonth());
		 this.getFormHM().put("dat",this.getDat());
		 this.getFormHM().put("coun",this.getCoun());
		 this.getFormHM().put("box",this.getBox());
		 this.getFormHM().put("radio",this.getRadio());
		 
		 this.getFormHM().put("end",this.getEnd());
		 this.getFormHM().put("start",this.getStart());
		 this.getFormHM().put("yue",this.getYue());
		 this.getFormHM().put("one_len",this.getOne_len());

	}

	public RecordVo getDuration() {
		return duration;
	}

	public void setDuration(RecordVo duration) {
		this.duration = duration;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getKq_year() {
		return kq_year;
	}

	public void setKq_year(String kq_year) {
		this.kq_year = kq_year;
	}
	

	public PaginationForm getKqDurationForm() {
		return kqDurationForm;
	}

	public void setKqDurationForm(PaginationForm kqDurationForm) {
		this.kqDurationForm = kqDurationForm;
	}


	public String getKq_duration() {
		return kq_duration;
	}

	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}
	

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
	       
        if("/kq/options/duration_details".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
            this.getDuration().clearValues();            
            this.setFlag("0");
        }
        if("/kq/options/duration_details".equals(arg0.getPath())&&arg1.getParameter("b_usave")!=null)
        {         
            this.setFlag("0");
        }
        if("/kq/options/duration_details".equals(arg0.getPath())&&arg1.getParameter("b_dsave")!=null)
        {   
        	this.getDuration().clearValues();
            this.setFlag("1");
        }
        if("/kq/options/duration_details".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("1");
        }
        if("/kq/options/add_duration".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {          
            this.setFlag("1");
        	  
        }  
        if("/kq/options/add_batch_duration".equals(arg0.getPath())&&(arg1.getParameter("b_osave")!=null))
        {          
            this.setFlag("1");
        	  
        }  
      
        
        return super.validate(arg0, arg1);
    }

	public String getKyear() {
		return kyear;
	}

	public void setKyear(String kyear) {
		this.kyear = kyear;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getDat() {
		return dat;
	}

	public void setDat(String dat) {
		this.dat = dat;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getBox() {
		return box;
	}

	public void setBox(String box) {
		this.box = box;
	}

	public String getCoun() {
		return coun;
	}

	public void setCoun(String coun) {
		this.coun = coun;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getYue() {
		return yue;
	}

	public void setYue(String yue) {
		this.yue = yue;
	}

	public String getOne_len() {
		return one_len;
	}

	public void setOne_len(String one_len) {
		this.one_len = one_len;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

}
