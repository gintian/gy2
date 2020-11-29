package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SortDegreeHighSetTrans.java</p>
 * <p>Description:参数设置/等级分类/高级设置/排序</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SortDegreeHighSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
	{
    	
	//	PerDegreeBo bo = new PerDegreeBo(this.frameconn);
	//	ArrayList list = bo.sortList();
	//	this.getFormHM().put("sortlist",list);
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
    	String degreeID=(String)hm.get("degreeID");
    	PerDegreeBo bo = new PerDegreeBo(this.frameconn,degreeID,"");
    
        ArrayList list = bo.getData();
        this.getFormHM().put("extpro",list);
            //启用高级设置
    	this.getFormHM().put("qy",bo.getUsed());
    	
    }
    
}
