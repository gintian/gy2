package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SaveDegreeHighSetTrans.java</p>
 * <p>Description:参数设置/等级分类/高级设置/保存</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveDegreeHighSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String degreeID=(String)hm.get("degreeID");
		String used=(String)hm.get("used");
		String toRoundOff = (String) this.getFormHM().get("toRoundOff");
		String plan_id = (String) this.getFormHM().get("plan_id");
		
		PerDegreeBo bo = new PerDegreeBo(this.frameconn,degreeID,plan_id);
	
		bo.saveHighSet(used,toRoundOff);
	
    }

}
