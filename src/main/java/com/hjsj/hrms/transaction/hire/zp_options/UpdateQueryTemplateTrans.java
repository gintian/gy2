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
 * <p>Title:UpdateQueryTemplateTrans</p>
 * <p>Description:增加查询指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class UpdateQueryTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String[] fields=(String[])this.getFormHM().get("right_fields");
        
        ArrayList fieldlist=new ArrayList();        
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
            item=DataDictionary.getFieldItem(fieldname.toUpperCase());
            if(item!=null)
            {
                CommonData datavo=new CommonData(item.getItemid(),item.getItemdesc());
                fieldlist.add(datavo);
            }
        }
        this.getFormHM().put("fieldlist",fieldlist);

	}

}
