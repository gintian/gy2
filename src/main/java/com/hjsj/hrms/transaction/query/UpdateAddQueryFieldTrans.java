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
public class UpdateAddQueryFieldTrans extends IBusiness {

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

        for(int i=0;i<fields.length;i++)
        {
            String fieldname=fields[i];
            if(fieldname==null|| "".equals(fieldname))
                continue;
            cat.debug("field_name="+fieldname);
            item=DataDictionary.getFieldItem(fieldname.toUpperCase());
            cat.debug("item_desc="+item.toString());
            if(item!=null)
            {
                CommonData datavo=new CommonData(item.getItemid(),item.getItemdesc());
                fieldlist.add(datavo);
            }
        }
        this.getFormHM().put("fieldlist",fieldlist);
    }

}
