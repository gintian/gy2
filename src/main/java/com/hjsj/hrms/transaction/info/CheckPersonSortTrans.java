package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckPersonSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
        String fieldsort=new SortFilter().getSortPersonField(this.getFrameconn());
        ArrayList personsortlist=new ArrayList();
        String personsort="";
		if(fieldsort!=null)
		{
	        FieldItem fielditem=DataDictionary.getFieldItem(fieldsort);
	        StringBuffer sql=new StringBuffer();
	        sql.append("select * from codeitem where codesetid='");
	        if(fielditem!=null){
	        	sql.append(fielditem.getCodesetid());
	        }
	        sql.append("'");
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	       
	        try{
	        	int i=0;
	           this.frowset=dao.search(sql.toString());
	           while(this.frowset.next())
	           {
	        	   CodeItem codeitem=new CodeItem();
	        	   if(i==0)
	        		   personsort=this.getFrowset().getString("codeitemid");  
	        	   i++;
	               codeitem.setCcodeitem(this.getFrowset().getString("childid"));
	               codeitem.setCodeid(this.getFrowset().getString("codesetid"));
	               codeitem.setCodeitem(this.getFrowset().getString("codeitemid"));
	               codeitem.setCodename(this.getFrowset().getString("codeitemdesc"));
	               codeitem.setPcodeitem(this.getFrowset().getString("parentid"));
	               personsortlist.add(codeitem);
	           }
	         
	        }catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
        }
	    this.getFormHM().put("personsortlist", personsortlist);
//	    System.out.println(this.getFormHM().get("personsort"));
	    if(this.getFormHM().get("personsort")==null || "All".equalsIgnoreCase((String)this.getFormHM().get("personsort"))|| "".equals(personsort))
	    	this.getFormHM().put("personsort", "All");
	    else
	    	this.getFormHM().put("personsort", personsort);
	}

}
