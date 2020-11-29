package com.hjsj.hrms.utils.components.complexcondition.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectOrDelGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList ids=(ArrayList)this.getFormHM().get("ids");
			String flag = (String)this.getFormHM().get("flag");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("del".equals(flag)){
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<ids.size();i++)
				{
					whl.append(","+ids.get(i));
				}
				if(whl.length()>0)
				{
					dao.delete("delete from gwhere where id in ("+whl.substring(1)+")",new ArrayList());
				}
			}else{
				this.frowset=dao.search("select * from gwhere where id="+ids.get(0));
				if(this.frowset.next())
				{
					this.getFormHM().put("expr",Sql_switcher.readMemo(this.frowset,"lexpr"));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
