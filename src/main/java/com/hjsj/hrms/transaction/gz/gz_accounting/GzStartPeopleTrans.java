package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class GzStartPeopleTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operate=(String)hm.get("operate");
			String collect = (String) hm.get("collect");
			if("confirm".equalsIgnoreCase(operate)|| "confirmAll".equalsIgnoreCase(operate)|| "confirmGroup".equalsIgnoreCase(operate))
			{
				String  selectGzRecords=SafeCode.decode((String)hm.get("selectID"));
				selectGzRecords=PubFunc.keyWord_reback(selectGzRecords);
				if(selectGzRecords.length()>0&&!"confirmGroup".equalsIgnoreCase(operate))
					selectGzRecords=selectGzRecords.substring(1);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String salaryid=(String)this.getFormHM().get("salaryid");
				String bosdate=(String)this.getFormHM().get("bosdate");  //业务日期(发放日期)
				String count=(String)this.getFormHM().get("count");		 //发放次数
				if("confirmGroup".equalsIgnoreCase(operate)||"1".equals(collect))
				{
					salaryid=(String)hm.get("salaryid");
					bosdate=(String)hm.get("a00z2");
					count=(String)hm.get("a00z3");
				}
				String reportSql=(String)this.getFormHM().get("reportSql");
				if(reportSql==null||reportSql.trim().length()==0)
					reportSql="";
				reportSql = PubFunc.decrypt(reportSql);
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				
				LazyDynaBean dataBean = gzbo.getSalaryPayDate(bosdate, count);
				ArrayList primitiveDataTables= gzbo.getPrimitiveDataTable(dataBean, selectGzRecords,operate,reportSql);
				StringBuffer str=new StringBuffer("");
				for(int e=0;e<primitiveDataTables.size();e++)
				{
						String primitiveDataTable=(String)primitiveDataTables.get(e);
						String[] atemps=primitiveDataTable.split("_salary_");
						str.append(",'"+atemps[0].toLowerCase()+"'");
				}
				String user_="";
				String user_h="";
				if(str.length()>0)
				{
					this.frowset=dao.search("select username,fullName from operuser where lower(username) in ("+str.substring(1)+") ");
				    while(this.frowset.next())
				    {
				    	 String username=this.frowset.getString("username");
				    	 String fullName=this.frowset.getString("fullName");
				    	 if(fullName==null||fullName.trim().length()==0)
				    		 fullName=username;
				    	 user_+=","+fullName;
				    	 user_h+=","+username;
				    	
				    }
					if(user_.length()>0)
					{
						this.getFormHM().put("user_", user_.substring(1));
						this.getFormHM().put("user_h", user_h);
					}
					else
					{
						this.getFormHM().put("user_", "");
						this.getFormHM().put("user_h", "");
					}
					
					
				}
				else
				{
					this.getFormHM().put("user_", "");
					this.getFormHM().put("user_h", "");
				}
				
			}
			else
			{
				this.getFormHM().put("user_", "");
				this.getFormHM().put("user_h", "");
				
			}
			hm.remove("operate");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
