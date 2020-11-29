package com.hjsj.hrms.transaction.performance.scoreAjust;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SearchMainBodyTrans.java</p>
 * <p>Description:评分调整退回考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-02-12 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 * 
 */

public class SearchMainBodyTrans extends IBusiness {

	public void execute() throws GeneralException {
		String plan_id=PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("plan_id")));
		String object_id=PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("object_id")));
		try
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(), object_id, plan_id,this.userView);
			ArrayList mainbodyList = bo.getMainBodyList(plan_id, object_id);			
			this.getFormHM().put("mainBodyList", mainbodyList);			
	
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	}

}
