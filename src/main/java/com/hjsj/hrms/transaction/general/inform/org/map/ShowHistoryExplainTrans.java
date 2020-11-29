package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowHistoryExplainTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String catalog_id=(String)hm.get("catalog_id");
		StringBuffer sql=new StringBuffer();
		sql.append("select description,archive_date from  hr_org_catalog");
		sql.append(" where catalog_id='"+catalog_id+"'");
		String description="";
		String archive_date="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				description=this.frowset.getString("description");
				archive_date=String.valueOf(this.frowset.getDate("archive_date"));
			}
			description = description.replaceFirst("\r\n","<br>");
			description +="<br><p align='right'>"+archive_date+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("description",description);

	}

}

