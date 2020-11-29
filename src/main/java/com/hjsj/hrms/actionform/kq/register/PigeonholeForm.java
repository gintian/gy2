package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;

public class PigeonholeForm extends FrameForm 
{
	   private String infor_Flag="1";
	   private String po_sqlstr;
	   private String po_wherestr;
	   private String po_column;
	   private String destfld;
	   private String temp_table;
	   private String bytesid;
	   private String setlist;
	   private String pigeonhole_flag;
	   @Override
       public void outPutFormHM()
	   {
		   this.setPo_sqlstr((String)this.getFormHM().get("po_sqlstr"));
		   this.setPo_wherestr((String)this.getFormHM().get("po_wherestr"));
		   this.setPo_column((String)this.getFormHM().get("po_column"));
		   this.setDestfld((String)this.getFormHM().get("destfld"));
		   this.setTemp_table((String)this.getFormHM().get("temp_table"));
		   this.setBytesid((String)this.getFormHM().get("bytesid"));
	       this.setPigeonhole_flag((String)this.getFormHM().get("pigeonhole_flag")); 
	   }
	   @Override
       public void inPutTransHM()
	   {
		   this.getFormHM().put("destfld",this.getDestfld());
		   this.getFormHM().put("temp_table",this.getTemp_table());
		   this.getFormHM().put("bytesid",this.getBytesid());
		   if(this.getPagination()!=null)        
		          this.getFormHM().put("list",this.getPagination().getAllList());
		   this.getFormHM().put("setlist",this.getSetlist());
	   }
	
	public String getInfor_Flag() {
		return infor_Flag;
	}
	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}
	
	
	public String getDestfld() {
		return destfld;
	}
	public void setDestfld(String destfld) {
		this.destfld = destfld;
	}
	public String getTemp_table() {
		return temp_table;
	}
	public void setTemp_table(String temp_table) {
		this.temp_table = temp_table;
	}
	public String getBytesid() {
		return bytesid;
	}
	public void setBytesid(String bytesid) {
		this.bytesid = bytesid;
	}
	public String getSetlist() {
		return setlist;
	}
	public void setSetlist(String setlist) {
		this.setlist = setlist;
	}
	public String getPigeonhole_flag() {
		return pigeonhole_flag;
	}
	public void setPigeonhole_flag(String pigeonhole_flag) {
		this.pigeonhole_flag = pigeonhole_flag;
	}
	public String getPo_column() {
		return po_column;
	}
	public void setPo_column(String po_column) {
		this.po_column = po_column;
	}
	public String getPo_sqlstr() {
		return po_sqlstr;
	}
	public void setPo_sqlstr(String po_sqlstr) {
		this.po_sqlstr = po_sqlstr;
	}
	public String getPo_wherestr() {
		return po_wherestr;
	}
	public void setPo_wherestr(String po_wherestr) {
		this.po_wherestr = po_wherestr;
	}
}
