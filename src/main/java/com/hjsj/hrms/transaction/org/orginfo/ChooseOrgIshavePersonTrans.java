package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseOrgIshavePersonTrans  extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//ArrayList delorglist=(ArrayList)this.getFormHM().get("selectedlist");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String orgitem=(String)hm.get("orgitem");
		String orgitems[]=orgitem.split("`");
		//ArrayList delorglist=new ArrayList();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String code=(String)this.getFormHM().get("code");
	    StringBuffer column=new StringBuffer();
	    if(orgitems==null||orgitems.length==0){
	    		column.append("''");
	    		throw GeneralExceptionHandler.Handle(new GeneralException("","请选择机构！","",""));
	    }	    	
	    else
	    {
	    	for(int i=0;i<orgitems.length;i++)
	    	{
	    		column.append("'"+orgitems[i]+"',");
	    	}
	    	column.setLength(column.length()-1);
	    }
	    StringBuffer strsql=new StringBuffer();
	    if(code!=null && code.trim().length()>0){
			strsql.append("select * from organization where parentid='"+code+"'");
			if(column.length()>0)
			   strsql.append(" and codeitemid not in("+column.toString()+")");
			strsql.append(" and codeitemid<>parentid order by A0000");
		}
		else
		{
			strsql.append("select * from organization where codeitemid=parentid ");
			if(column.length()>0)
			   strsql.append(" and codeitemid not in("+column.toString()+")");
			strsql.append("order by A0000");
		}
	    ArrayList chooselist=new ArrayList();
	    try
	    {
	    	this.frowset=dao.search(strsql.toString());
	    	while(this.frowset.next())
	    	{
	    		CommonData data=new CommonData();
	    		data.setDataName(this.frowset.getString("codeitemdesc"));
	    		data.setDataValue(this.frowset.getString("codesetid")+"`"+this.frowset.getString("codeitemid"));
	    		chooselist.add(data);
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    this.getFormHM().put("chooselist",chooselist);
	}

}
