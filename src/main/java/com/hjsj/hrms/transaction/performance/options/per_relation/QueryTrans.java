package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:QueryTrans.java</p>
 * <p>Description:考核关系/查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-12 09:13:45</p>
 * @author JinChunhai
 * @version 1.0
 */

public class QueryTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tablename=(String)hm.get("tablename");
		hm.remove(tablename);
		if(tablename==null || tablename.trim().length()<=0)
			tablename="per_object_std";   
					
		PerRelationBo bo = new PerRelationBo(this.frameconn);
		ArrayList fieldlist=bo.getFieldlist(tablename);
		
		this.getFormHM().put("leftlist", fieldlist);
//		this.getFormHM().put("tablename", tablename);	
	
    }
}
