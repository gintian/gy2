package com.hjsj.hrms.transaction.general.template;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class GetMailTempletInfoTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			RowSet rowSet=dao.search("select * from email_name where id="+id);
			if(rowSet.next())
			{
				String subject=rowSet.getString("subject");
				String content=Sql_switcher.readMemo(rowSet,"content");
				this.getFormHM().put("subject", SafeCode.encode(subject));
				this.getFormHM().put("content", SafeCode.encode(content));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
