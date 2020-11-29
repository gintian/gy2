package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EditBankTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hashmap =(HashMap)this.getFormHM().get("requestPamaHM");
			String bank_id = (String)hashmap.get("bank_id");
			String salaryid=(String)hashmap.get("salaryid");
			String code = (String)hashmap.get("code");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String opt=(String)hashmap.get("opt");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			HashMap salarySetMap=bo.getSalarySetFields(salaryid);
			ArrayList selectedFieldList = new ArrayList();
			if(opt!=null&& "add".equalsIgnoreCase(opt)){
				selectedFieldList = bo.getBankItemInfo(bank_id,salaryid,3,salarySetMap);
			}else{
				selectedFieldList = bo.getBankItemInfo(bank_id,salaryid,1,salarySetMap);
			}
			
			HashMap map = bo.getCheckAndFormat(bank_id);
			this.getFormHM().put("code",code);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("isclose","2");
			this.getFormHM().put("bank_id",bank_id);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("selectedFieldList",selectedFieldList);
			this.getFormHM().put("bankCheck",map.get("bankcheck")==null?"0":(String)map.get("bankcheck"));
			this.getFormHM().put("bankFormat",map.get("bankformat")==null?"":(String)map.get("bankformat"));
			this.getFormHM().put("bank_name",map.get("bank_name")==null?"":(String)map.get("bank_name"));
			this.getFormHM().put("scope",map.get("scope")==null?"0":(String)map.get("scope"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
