package com.hjsj.hrms.transaction.query;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:UpdateQueryFieldTrans</p>
 * <p>Description:高级查询字段的选择的增减处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 20, 2005:2:06:41 PM</p>
 * @author chenmengqing
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
        String[] fields=(String[])this.getFormHM().get("right_fields");
        HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
        String queryFields = (String) map.get("queryFields");
        map.remove("queryFields");
        
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
            
            if(queryFields != null) {
                if(queryFields.indexOf(fieldname) < 0)
                    continue;
                
                queryFields = queryFields.replaceFirst(fieldname, "");
            }
            
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
