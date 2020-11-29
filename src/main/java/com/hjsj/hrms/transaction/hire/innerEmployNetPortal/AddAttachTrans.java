package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddAttachTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String i9999 = (String)map.get("i9999");
			String type = (String)map.get("type");//=1:edit,=2:new;
			String dbname = (String)map.get("dbname");
			String a0100= (String)map.get("a0100");
			String fileName="";
			if("1".equals(type))
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search("select title from "+PubFunc.decrypt(dbname)+"a00 where a0100='"+PubFunc.decrypt(a0100)+"' and i9999="+PubFunc.decrypt(i9999));
				while(this.frowset.next())
				{
					fileName = this.frowset.getString(1);
				}
			}
			this.getFormHM().put("fileName",fileName);
			this.getFormHM().put("zpkA0100", a0100);
    		this.getFormHM().put("dbname", dbname);
    		this.getFormHM().put("i9999", i9999);
    		this.getFormHM().put("type", type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
