package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 
 * <p>Title:ChangeCardForm.java</p>
 * <p>Description>:ChangeCardForm.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 30, 2011 6:10:22 PM</p>
 * <p>@version: 5.0</p>
 * 
 * <p>@author: s.xin
 */
public class ChangeCardForm extends FrameForm 
{
	 private ArrayList card_list=new ArrayList();
	 private String lost_flag;
	 private String new_card;
	 private String old_card;
	 private String kq_cardno;
	 private String a0100;
	 private String nbase;
	 private String s_flag;
	 private String flag="";
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
	public String getS_flag() {
		return s_flag;
	}
	public void setS_flag(String s_flag) {
		this.s_flag = s_flag;
	}
	public ArrayList getCard_list() {
		return card_list;
	}
	public void setCard_list(ArrayList card_list) {
		this.card_list = card_list;
	}
	public String getLost_flag() {
		return lost_flag;
	}
	public void setLost_flag(String lost_flag) {
		this.lost_flag = lost_flag;
	}
	public String getNew_card() {
		return new_card;
	}
	public void setNew_card(String new_card) {
		this.new_card = new_card;
	}
	public String getOld_card() {
		return old_card;
	}
	public void setOld_card(String old_card) {
		this.old_card = old_card;
	}
	@Override
    public void outPutFormHM()
	 { 
		 this.setCard_list((ArrayList)this.getFormHM().get("card_list")); 
		 this.setOld_card((String)this.getFormHM().get("old_card"));
		 this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));
		 this.setFlag((String)this.getFormHM().get("flag"));
	 }
	 @Override
     public void inPutTransHM()
	 {
		 this.getFormHM().put("lost_flag",this.getLost_flag());
		 this.getFormHM().put("new_card",this.getNew_card());
		 this.getFormHM().put("old_card",this.getOld_card());
		 this.getFormHM().put("kq_cardno",this.getKq_cardno());
		 this.getFormHM().put("a0100",this.getA0100());
		 this.getFormHM().put("nbase",this.getNbase());
		 this.getFormHM().put("s_flag",this.getS_flag());
	 }
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
     {
			if("/kq/options/manager/changecard".equals(arg0.getPath())&&arg1.getParameter("b_change")!=null)
		    {
		        if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		        this.setLost_flag("");
		        this.setNew_card("");
		        this.setOld_card("");
		        this.setA0100("");
		        this.setNbase("");
		        this.setS_flag("");
		    }		
			arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
			 return super.validate(arg0, arg1);
	 }
	public String getKq_cardno() {
		return kq_cardno;
	}
	public void setKq_cardno(String kq_cardno) {
		this.kq_cardno = kq_cardno;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}


