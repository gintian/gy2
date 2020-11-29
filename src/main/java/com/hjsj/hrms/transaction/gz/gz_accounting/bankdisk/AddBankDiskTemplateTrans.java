package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class AddBankDiskTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryid= (String)this.getFormHM().get("salaryid");
			String bankName=(String)this.getFormHM().get("bank_name");
			bankName=SafeCode.decode(bankName);
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String code =(String)this.getFormHM().get("code");
			String priv=(String)this.getFormHM().get("priv");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String userName=this.userView.getUserName();
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			//IDGenerator ID = new IDGenerator(2,this.getFrameconn());
			if(Sql_switcher.searchDbServer() == Constant.MSSQL)
			{
    			String sql = "insert into gz_bank (bank_name,scope,username) values('"+bankName+"',"+priv+",'"+userName+"')";
     			dao.insert(sql,new ArrayList());
			}
			else
			{
				String id = bo.getMaxBank_id();
				if(id==null|| "".equals(id))
					id="1";
				String sql = "insert into gz_bank (bank_name,bank_id,scope,username) values('"+bankName+"',"+(Integer.parseInt(id)+1)+","+priv+",'"+userName+"')";
     			dao.insert(sql,new ArrayList());
			}
			String bank_id=bo.getMaxBank_id();
			/**gz_bank_item已选的项目*/
			this.getFormHM().put("bank_id",bank_id);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("code",code);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
