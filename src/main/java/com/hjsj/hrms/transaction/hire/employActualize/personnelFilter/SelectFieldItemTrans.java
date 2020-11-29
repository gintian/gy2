package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=new ArrayList();
		String setname=(String)this.getFormHM().get("tablename");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String fieldItemIDs="";
			this.frowset=dao.search("select * from constant where constant='ZP_FIELD_LIST'");
			if(this.frowset.next())
			{
				fieldItemIDs=Sql_switcher.readMemo(this.frowset,"str_value");
			}
			if(fieldItemIDs.trim().length()>0)
			{
			//	int index=fieldItemIDs.indexOf()
				String temp_str=fieldItemIDs.substring(fieldItemIDs.indexOf(setname.trim()));
				temp_str=temp_str.substring(temp_str.indexOf("{")+1);
				temp_str=temp_str.substring(0,temp_str.indexOf("}"));
				
				String[] fieldItems=temp_str.split(",");
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<fieldItems.length;i++)
				{
					whl.append(",'"+fieldItems[i]+"'");
				}
				this.frowset=dao.search("select itemid,itemtype,itemdesc,codesetid,fieldsetid from fielditem where useflag=1 and itemtype<>'M' and itemid in ("+whl.substring(1)+")");
				String value="";
				while(this.frowset.next())
				{	
					 value=this.frowset.getString("itemid")+"%%"+this.frowset.getString("itemdesc")+"%%"+this.frowset.getString("itemtype")+"%%"+this.frowset.getString("codesetid")+"%%"+this.frowset.getString("fieldsetid");
					 CommonData dataobj = new CommonData(value, this.frowset.getString("itemdesc"));
					 list.add(dataobj);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    this.getFormHM().put("fieldlist",list);
		
		
	}

}
