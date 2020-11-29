package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqCardForm extends FrameForm 
{
   private String treeCode;//树形菜单，在HtmlMenu中
   private String a_code;
   private String nbase;
   private ArrayList card_list=new ArrayList();
   private String card_no;
   private String r_code[];
   private String r_card[];
   private String r_gno[];
   private String kq_cardno;
   private String kq_gno;
   private ArrayList order_list=new ArrayList();
   private String order_status;
   private String order_flag;
   private String id_len;
   private String flag="";
   public String getId_len() {
	return id_len;
}
public void setId_len(String id_len) {
	this.id_len = id_len;
}
public String getOrder_flag() {
   return order_flag;
}
public void setOrder_flag(String order_flag) {
	this.order_flag = order_flag;
}
public ArrayList getOrder_list() {
	return order_list;
}
public void setOrder_list(ArrayList order_list) {
	this.order_list = order_list;
}
public String getOrder_status() {
	return order_status;
}
public void setOrder_status(String order_status) {
	this.order_status = order_status;
}
public String getKq_gno() {
	return kq_gno;
}
public void setKq_gno(String kq_gno) {
	this.kq_gno = kq_gno;
}
public String getKq_cardno() {
	return kq_cardno;
}
public void setKq_cardno(String kq_cardno) {
	this.kq_cardno = kq_cardno;
}
public String[] getR_card() {
	return r_card;
}
public void setR_card(String[] r_card) {
	this.r_card = r_card;
}
public String[] getR_code() {
	return r_code;
}
public void setR_code(String[] r_code) {
	this.r_code = r_code;
}
public String getCard_no() {
	return card_no;
}
public void setCard_no(String card_no) {
	this.card_no = card_no;
}
public String getA_code() {
	return a_code;
}
public void setA_code(String a_code) {
	this.a_code = a_code;
}
public ArrayList getCard_list() {
	return card_list;
}
public void setCard_list(ArrayList card_list) {
	this.card_list = card_list;
}
public String getNbase() {
	return nbase;
}
public void setNbase(String nbase) {
	this.nbase = nbase;
}
@Override
public void outPutFormHM()
  {      
	   
	   this.setTreeCode((String)this.getFormHM().get("treeCode"));	
	   this.setNbase((String)this.getFormHM().get("nbase"));
	   this.setA_code((String)this.getFormHM().get("a_code"));
	   this.setCard_list((ArrayList)this.getFormHM().get("card_list"));
	   this.setCard_no((String)this.getFormHM().get("card_no"));
	   this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));
	   this.setKq_gno((String)this.getFormHM().get("kq_gno"));
	   this.setOrder_list((ArrayList)this.getFormHM().get("order_list"));
	   this.setOrder_status((String)this.getFormHM().get("order_status"));
	   this.setOrder_flag((String)this.getFormHM().get("order_flag"));
	   this.setId_len((String)this.getFormHM().get("id_len"));
	   this.setFlag((String)this.getFormHM().get("flag"));
  }
  @Override
  public void inPutTransHM() {
	  this.getFormHM().put("nbase",this.getNbase());
	  this.getFormHM().put("a_code",this.getA_code());
	  this.getFormHM().put("r_card",this.getR_card());
	  this.getFormHM().put("r_code",this.getR_code());
	  this.getFormHM().put("kq_cardno",this.getKq_cardno());
	  this.getFormHM().put("kq_gno",this.getKq_gno());
	  this.getFormHM().put("order_status",this.getOrder_status());
	  this.getFormHM().put("order_flag",this.getOrder_flag());
	  this.getFormHM().put("r_gno",this.getR_gno());
	  this.getFormHM().put("id_len",this.getId_len());
  }
  public String getTreeCode() {
	return treeCode;
  }
  public void setTreeCode(String treeCode) {
	this.treeCode = treeCode;
  }
  @Override
  public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/kq/options/manager/sendcard".equals(arg0.getPath())&&arg1.getParameter("b_work")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().clear();
	    }
		if("/kq/options/manager/batchsendcard".equals(arg0.getPath())&&arg1.getParameter("b_send")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setOrder_flag("");
	        this.getFormHM().clear();
	    }
		arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		 return super.validate(arg0, arg1);
	}
public String[] getR_gno() {
	return r_gno;
}
public void setR_gno(String[] r_gno) {
	this.r_gno = r_gno;
}
public String getFlag() {
	return flag;
}
public void setFlag(String flag) {
	this.flag = flag;
}
}
