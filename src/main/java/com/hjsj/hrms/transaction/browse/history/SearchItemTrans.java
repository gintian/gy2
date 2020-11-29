package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 * <p>Title:SearchItemTrans.java</p>
 * <p>Description>:SearchItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 25, 2010 5:30:08 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SearchItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String setname=(String)this.getFormHM().get("tablename");
		ArrayList fielditemlist = new ArrayList();
		String sql="select * from "+setname+" where 1=2";
		try {
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			StringBuffer columnstr = new StringBuffer();
			for (int i = 1; i <= size; i++) {
				String field = rsmd.getColumnName(i);
				if("id".equalsIgnoreCase(field))
					continue;
				if("Nbase".equalsIgnoreCase(field))
					continue;
				if("A0000".equalsIgnoreCase(field))
					continue;
				if("A0100".equalsIgnoreCase(field))
					continue;
				if(field!=null&&field.length()>0){
					FieldItem fi=DataDictionary.getFieldItem(field);
					if(fi==null)
						continue;
					String itemid=fi.getItemid()+":"+fi.getItemtype()+":"+fi.getCodesetid()+":"+fi.getFieldsetid();
					CommonData obj=new CommonData(itemid,fi.getItemdesc());
					fielditemlist.add(obj);
				}
			}
			this.getFormHM().put("fieldlist",fielditemlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}

}
