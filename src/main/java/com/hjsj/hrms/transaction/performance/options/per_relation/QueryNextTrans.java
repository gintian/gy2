package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:CopyMainBodyTrans.java</p>
 * <p>Description:考核关系/查询/下一步</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-20 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class QueryNextTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
        String[] selectedField=(String[])this.getFormHM().get("right_fields");        
		ArrayList selectedFieldList=new ArrayList();
		for(int i=0;i<selectedField.length;i++)
		{
			String temp=selectedField[i];
			String[] array=temp.split("<@>");
			DynaBean bean = new LazyDynaBean();
			bean.set("itemid",array[0]);
			bean.set("itemdesc",array[1]);
			bean.set("itemtype",array[2]);
			bean.set("table_name", array[4]);
			if(array.length>3)
				bean.set("itemsetid",array[3]);
			else
				bean.set("itemsetid","0");
			
			selectedFieldList.add(bean);			
		}
		this.getFormHM().put("selectedFieldList",selectedFieldList);
		
		PerRelationBo bo = new PerRelationBo(this.getFrameconn());
		ArrayList objectTypes = bo.getObjTypes();
		this.getFormHM().put("objectTypes", objectTypes);
    }

}
