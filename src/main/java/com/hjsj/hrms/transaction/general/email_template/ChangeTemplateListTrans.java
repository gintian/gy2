package com.hjsj.hrms.transaction.general.email_template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class ChangeTemplateListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String nmodule=(String)this.getFormHM().get("nmodule");
		    ArrayList list = this.getEmailTemplateListByNmodule(nmodule, 1);
		    this.getFormHM().put("list",list);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public ArrayList getEmailTemplateListByNmodule(String nmodule,int type)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule="+nmodule+" order by id";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql);
			list.add(new CommonData(" ",ResourceFactory.getProperty("label.select.dot")));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
			if(type==1)
			{
				list.add(new CommonData("createnew",ResourceFactory.getProperty("label.gz.new")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	

}
