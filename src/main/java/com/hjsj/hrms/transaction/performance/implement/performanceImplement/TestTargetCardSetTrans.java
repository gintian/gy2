package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchTargetCardSetTrans.java</p>
 * <p>Description:考核实施/目标卡制定 检验</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class TestTargetCardSetTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		
		String planid = (String) this.getFormHM().get("planid");	
		String objCode = (String) this.getFormHM().get("objCode");	
		
		try 
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid,"targetCard",this.getUserView());
			String targetCardTestStr = bo.testObjTargetCard();
			this.getFormHM().put("targetCardTestStr", targetCardTestStr);
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}			
		
	}
	
}
