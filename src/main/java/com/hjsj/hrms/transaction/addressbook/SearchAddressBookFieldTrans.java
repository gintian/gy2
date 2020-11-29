package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class SearchAddressBookFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
		strsql.append("select str_value from constant where constant='SS_ADDRESSBOOK'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		try
		{
			String str_value="";
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next())
				str_value=this.frowset.getString("str_value");
			else
				return;
			
			String str[]=str_value.split(",");
			for(int i=0;i<str.length;i++)
			{
				String st=str[i];
				FieldItem item= DataDictionary.getFieldItem(st);
				list.add(new LabelValueView(item.getItemid(),item.getItemdesc()));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		this.getFormHM().put("str_valuelist",list);
	}

}
