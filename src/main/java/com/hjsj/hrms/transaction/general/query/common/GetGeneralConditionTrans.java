package com.hjsj.hrms.transaction.general.query.common;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String id=(String)this.getFormHM().get("id");
			this.frowset=dao.search("select * from gwhere where id="+id);
			if(this.frowset.next())
			{
			//	System.out.println(Sql_switcher.readMemo(this.frowset,"lexpr"));
				this.getFormHM().put("expr",SafeCode.encode(Sql_switcher.readMemo(this.frowset,"lexpr")));
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
