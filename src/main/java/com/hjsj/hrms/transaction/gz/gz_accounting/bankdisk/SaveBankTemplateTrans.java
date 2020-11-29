package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveBankTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			ArrayList list =(ArrayList)this.getFormHM().get("selectedFieldList");
			HashMap hashmap =(HashMap)this.getFormHM().get("requestPamaHM");
			String bank_id=(String)this.getFormHM().get("bank_id");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String a_code = (String)this.getFormHM().get("code");
			String bankCheck=(String)this.getFormHM().get("bankCheck");
			String bankFormat = (String)this.getFormHM().get("bankFormatValue");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			//bo.deleteBankInfo("gz_bank_item",bank_id);
			bo.deleteItem(list, bank_id);
			bo.saveTemplateItem(list,bank_id);	
			bo.updateBankTemplate(bankCheck,bankFormat+"`",bank_id);
			//dml 2011年8月29日10:05:36 start
			String scope=(String)hashmap.get("priv");    		
     		String bankname=(String)hashmap.get("bankname");
     		bankname=SafeCode.decode(bankname);
			bo.updateBankNameandPriv(bankname, scope, bank_id);
			//end
	          this.getFormHM().put("bank_id",bank_id);
			  this.getFormHM().put("salaryid",salaryid);
			  this.getFormHM().put("code",a_code);
			  this.getFormHM().put("tableName",tableName);
			  this.getFormHM().put("isclose","1");
			  this.getFormHM().put("username", this.userView.getUserName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
