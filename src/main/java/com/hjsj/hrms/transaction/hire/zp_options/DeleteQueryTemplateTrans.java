/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:DeleteQueryTemplateTrans</p>
 * <p>Description:删除查询模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class DeleteQueryTemplateTrans extends IBusiness {

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
        FieldItem item=null;
        ArrayList fieldlisttemp=new ArrayList();
        for(int i=0;i<fieldlist.size();i++)
        { 
        	 
        	boolean isadd=true;
        	CommonData tempdata=(CommonData)fieldlist.get(i);
        	for(int j=0;j<fields.length;j++)
        	{
        		 String fieldname=fields[j];
        		 if(fieldname==null|| "".equals(fieldname))
                    continue;
        		 item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                 if(tempdata.getDataValue().equalsIgnoreCase(item.getItemid()))
                 {
                   isadd=false;
                   break;
                 }
        	}
            if(isadd)
            	fieldlisttemp.add(tempdata);           
        }
        this.getFormHM().put("fieldlist",fieldlisttemp);
        this.getFormHM().put("left_fields",null);

	}

}
