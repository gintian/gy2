package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class EditKqCardForm extends FrameForm 
{
	private String a0100;
	private String nbase;
	private String cardno;
	private String kq_cardno;
	private String card_message;
	private String new_cardno;
	private String id_len;
	private String old_cardno;
	private String flag="";
	 private ArrayList card_list=new ArrayList();
	public ArrayList getCard_list() {
		return card_list;
	}
	public void setCard_list(ArrayList card_list) {
		this.card_list = card_list;
	}
	@Override
    public void outPutFormHM()
	{ 
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setCardno((String)this.getFormHM().get("cardno"));
		this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));
		this.setCard_list((ArrayList)this.getFormHM().get("card_list"));
		this.setId_len((String)this.getFormHM().get("id_len"));
		this.setCard_message((String)this.getFormHM().get("card_message"));
		this.setNew_cardno((String)this.getFormHM().get("new_cardno"));
		this.setOld_cardno((String)this.getFormHM().get("old_cardno"));
		this.setFlag((String)this.getFormHM().get("flag"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("kq_cardno",this.getKq_cardno());
		this.getFormHM().put("new_cardno",this.getNew_cardno());
		this.getFormHM().put("cardno",this.getCardno());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
			
			arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
			 return super.validate(arg0, arg1);
	 }
	
	public String getCard_message() {
		return card_message;
	}
	public void setCard_message(String card_message) {
		this.card_message = card_message;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getKq_cardno() {
		return kq_cardno;
	}
	public void setKq_cardno(String kq_cardno) {
		this.kq_cardno = kq_cardno;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}	
	public String getNew_cardno() {
		return new_cardno;
	}
	public void setNew_cardno(String new_cardno) {
		this.new_cardno = new_cardno;
	}
	public String getId_len() {
		return id_len;
	}
	public void setId_len(String id_len) {
		this.id_len = id_len;
	}
	public String getOld_cardno() {
		return old_cardno;
	}
	public void setOld_cardno(String old_cardno) {
		this.old_cardno = old_cardno;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
