/*
 * Created on 2005-9-6
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
 * <p>Title:UpdateQueryFieldTrans</p>
 * <p>Description:增加查询指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class UpdateQueryFieldTrans extends IBusiness {

	 /**
     * 
     */
    public UpdateQueryFieldTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	ArrayList fieldlist=new ArrayList();    
        String[] fields=(String[])this.getFormHM().get("right_fields"); 
        if(fields==null||fields.length==0)
        {   
        	cat.debug("fieldlist->");
            this.getFormHM().put("fieldlist",fieldlist);           
            return;
        }       
        FieldItem item=null;

        for(int i=0;i<fields.length;i++)
        {
            String fieldname=fields[i];
            if(fieldname==null|| "".equals(fieldname))
                continue;
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
