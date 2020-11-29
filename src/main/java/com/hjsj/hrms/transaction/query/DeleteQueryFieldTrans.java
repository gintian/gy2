/*
 * Created on 2005-8-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.query;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteQueryFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
        String[] fields=(String[])this.getFormHM().get("right_fields");
        
        ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");        
        if(fields==null||fields.length==0)
        {
            this.getFormHM().put("fieldlist",fieldlist);           
            return;
        }
        cat.debug("right_fields[]="+fields.toString());        
        FieldItem item=null;
        ArrayList fieldlisttemp=new ArrayList();
        System.out.println("fieldlist" + fieldlist.size());
        System.out.println("fields" + fields.length);
        for(int i=0;i<fieldlist.size();i++)
        { 
        	 
        	boolean isadd=true;
        	CommonData tempdata=(CommonData)fieldlist.get(i);
        	System.out.println("tempdata" + tempdata.getDataValue());
        	for(int j=0;j<fields.length;j++)
        	{
        		 String fieldname=fields[j];
        		 if(fieldname==null|| "".equals(fieldname))
                    continue;
        		 cat.debug("field_name="+fieldname);
        		 item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                 cat.debug("item_desc="+item.toString());
                 if(tempdata.getDataValue().equalsIgnoreCase(item.getItemid()))
                 {
                   isadd=false;
                   break;
                 }
        	}
        	System.out.println("isadd" + isadd);
            if(isadd)
            	fieldlisttemp.add(tempdata);           
        }
        this.getFormHM().put("fieldlist",fieldlisttemp);
        this.getFormHM().put("left_fields",null);
    }

}
