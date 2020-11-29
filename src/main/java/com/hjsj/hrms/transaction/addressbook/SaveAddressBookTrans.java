/*
 * Created on 2005-6-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveAddressBookTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String A0100=userView.getUserId();
		A0100="00000001";	
		List fieldlist=(List)this.getFormHM().get("fielditemlist");	         //获得fieldList
		String userbase=userView.getDbname();
		userbase="Usr";
		String tablename=userbase + "A01";
		StringBuffer fields=new StringBuffer();
		StringBuffer fieldvalues=new StringBuffer();
		String[] fieldsname=new String[fieldlist.size()];
		String[] fieldcode=new String[fieldlist.size()];
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldlist.get(i);
			fields.append(fieldItem.getItemid());
			fieldsname[i]=fieldItem.getItemid();
			fieldcode[i]=fieldItem.getValue();
			if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
			    fieldvalues.append("null");
			else
				fieldvalues.append("'" + fieldItem.getValue() + "'");
			fields.append(",");
			fieldvalues.append(",");
		}
		boolean flag=false;
		flag=new StructureExecSqlString().InfoUpdate("1",tablename,fieldsname,fieldcode,A0100,"I9999",userView.getUserName(),this.getFrameconn());
		this.getFormHM().put("fielditemlist",fieldlist);
		}
}
