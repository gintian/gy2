package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ValidateOrgTrans extends IBusiness {

	public void execute() throws GeneralException {
		String codeValues=(String)this.getFormHM().get("codeItemValue");
		String codeSetID=(String)this.getFormHM().get("codeSetID");
		String codeValue=(String)this.getFormHM().get("codeValue");
		
		String isTrue="1";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql=new StringBuffer("select codesetid from organization where codeitemid in ( ");
			StringBuffer whe=new StringBuffer("");
			if(codeValues.indexOf(",")==-1)
			{
				whe.append("'"+codeValues+"'");
				sql.append(whe.toString()+" )");
			}	
			else
			{
				String[] temp=codeValues.split(",");
				for(int i=0;i<temp.length;i++)
				{
					whe.append(",'"+temp[i]+"'");
				}
				sql.append(whe.substring(1)+" )");
			}
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String a_temp=this.frowset.getString("codesetid");
				if(!a_temp.equalsIgnoreCase(codeSetID))
					isTrue="0";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("isTrue",isTrue);
		this.getFormHM().put("codeValue",codeValue);
		this.getFormHM().put("codeItemValue",codeValues);
		this.getFormHM().put("codeSetID",codeSetID);
		
		
	}

}
