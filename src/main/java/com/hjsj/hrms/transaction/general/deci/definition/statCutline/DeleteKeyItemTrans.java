package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Iterator;

public class DeleteKeyItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList selectlist=(ArrayList)this.getFormHM().get("selectedList");		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
	    {
				StringBuffer typeids=new StringBuffer(",''");
	        	for(Iterator t=selectlist.iterator();t.hasNext();)
	        	{
	        		RecordVo a=(RecordVo)t.next();
	        		typeids.append(",'"+a.getString("itemid")+"'");
	        	}
	        	String temp=typeids.substring(1);	        	
	        	dao.delete("delete from ds_key_item where itemid in ("+temp+")",new ArrayList());
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
