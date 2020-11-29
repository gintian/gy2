package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBankItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
	    	String rightField=(String)this.getFormHM().get("rightFields");
	    	ArrayList alist =(ArrayList)this.getFormHM().get("selectedFieldList");
	    	HashMap hashmap =(HashMap)this.getFormHM().get("requestPamaHM");
	    	String salaryid=(String)this.getFormHM().get("salaryid");
	    	String code =(String)this.getFormHM().get("code");
	    	String tableName=this.userView.getUserName()+"_salary_"+salaryid;
	    	BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
	    	String bank_id="";
	    	bank_id=(String)this.getFormHM().get("bank_id");
	    	HashMap map=bo.getCheckAndFormat(bank_id);
	        ArrayList itemlist= bo.firstSaveItem(rightField, salaryid,alist);
	        //bo.deleteBankInfo("gz_bank_item",bank_id);
	        bo.deleteItem(itemlist, bank_id);
	     	bo.saveTemplateItem(itemlist,bank_id);
	     	String scope=(String)hashmap.get("priv");    		
     		String bankname=(String)hashmap.get("bankname");
     		bankname=SafeCode.decode(bankname);
	    	//ArrayList list=bo.getNewSelectFieldList(rightField,salaryid,bank_id);
	    	this.getFormHM().put("selectedFieldList",itemlist);
	    	this.getFormHM().put("code",code);
	    	this.getFormHM().put("tableName",tableName);
	    	this.getFormHM().put("bank_id",bank_id);
	    	this.getFormHM().put("salaryid",salaryid);
	    	this.getFormHM().put("bankFormat",map.get("bankformat")==null?"":(String)map.get("bankformat"));
	    	this.getFormHM().put("bankCheck",map.get("bankcheck")==null?"0":(String)map.get("bankcheck"));
	    	this.getFormHM().put("scope", scope);
    		this.getFormHM().put("bank_name", bankname);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
