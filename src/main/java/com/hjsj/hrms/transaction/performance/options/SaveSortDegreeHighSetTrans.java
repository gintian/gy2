package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveSortDegreeHighSetTrans.java</p>
 * <p>Description:参数设置/等级分类/高级设置/排序/保存</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-16 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveSortDegreeHighSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String sort = (String) hm.get("sortStr");
		String degreeID=(String)hm.get("degreeID");
		PerDegreeBo bo = new PerDegreeBo(this.frameconn,degreeID,"");
		sort = sort != null && sort.trim().length() > 0 ? sort : "";
	
		bo.saveSort(sort);
		
		//得到变动的等级项目
		ArrayList list = bo.getDegrees();
		this.getFormHM().put("itemCount", new Integer(list.size()));
		this.getFormHM().put("degrees",list);

        list = bo.getData();
        this.getFormHM().put("extpro",list);
      
        bo.setUsed();
        this.getFormHM().put("qy",bo.getUsed());

    }

}
