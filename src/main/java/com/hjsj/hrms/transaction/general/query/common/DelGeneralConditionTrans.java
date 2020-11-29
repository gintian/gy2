package com.hjsj.hrms.transaction.general.query.common;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] right_fields=(String[])this.getFormHM().get("right_fields");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<right_fields.length;i++)
			{
				whl.append(","+right_fields[i]);
			}
			if(whl.length()>0)
			{
				HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
				String opt=(String)hm.get("b_delCondition");
				if("del".equals(opt))
					dao.delete("delete from gwhere where id in ("+whl.substring(1)+")",new ArrayList());
				else
				{
					this.frowset=dao.search("select * from gwhere where id="+right_fields[0]);
					if(this.frowset.next())
					{
					//	System.out.println(Sql_switcher.readMemo(this.frowset,"lexpr"));
						this.getFormHM().put("expr",Sql_switcher.readMemo(this.frowset,"lexpr"));
						
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
