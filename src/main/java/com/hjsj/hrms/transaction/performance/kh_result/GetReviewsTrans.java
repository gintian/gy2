package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:GetReviewsTrans.java</p>
 * <p>Description>:查看考核评语交易</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-13 上午09:19:15</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetReviewsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String plan_id = PubFunc.decryption((String)this.getFormHM().get("planid"));
			String object_id = PubFunc.decryption((String)this.getFormHM().get("object_id"));
			String distinctionFlag = (String)this.getFormHM().get("distinctionFlag");
			ResultBo bo = new ResultBo(this.getFrameconn());
			String reviews = bo.getReviews(object_id, plan_id);
			int method=bo.getPlanMethod(Integer.parseInt(plan_id));
			ArrayList drawList = bo.getDrawList(distinctionFlag,method,plan_id,this.userView);
			this.getFormHM().put("drawList",drawList);
			this.getFormHM().put("drawId","2");
			this.getFormHM().put("reviews", reviews);
			this.getFormHM().put("planid",PubFunc.encrypt(plan_id));
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			this.getFormHM().put("distinctionFlag",distinctionFlag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
