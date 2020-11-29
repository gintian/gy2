package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectBankItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
    		String salaryid=(String)this.getFormHM().get("salaryid");
    		HashMap hashmap =(HashMap)this.getFormHM().get("requestPamaHM");
     		//String bank_id = (String)this.getFormHM().get("bank_id");
    		String bank_id = (String)hashmap.get("bank_id");
     		String a_code =(String)this.getFormHM().get("code");
     		String tableName=this.userView.getUserName()+"_salary_"+salaryid;
     		String scope=(String)hashmap.get("priv");    		
     		String bankname=(String)hashmap.get("bankname");
     		bankname=SafeCode.decode(bankname);
    		BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
    		bo.updateBankNameandPriv(bankname, scope, bank_id);
    		bo.setUserview(this.userView);//dml 2011年7月22日11:37:00
    		//ArrayList selectedList=bo.getSelectedItemList(bank_id);
    		ArrayList allList=bo.getAllBankItem(salaryid,bank_id);
    		//this.getFormHM().put("selectedFieldList",selectedList);
    		this.getFormHM().put("allList",allList);
    		this.getFormHM().put("salaryid",salaryid);
    		this.getFormHM().put("code",a_code);
    		this.getFormHM().put("tableName",tableName);
    		this.getFormHM().put("bank_id", bank_id);
    		this.getFormHM().put("scope", scope);
    		this.getFormHM().put("bank_name", bankname);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
