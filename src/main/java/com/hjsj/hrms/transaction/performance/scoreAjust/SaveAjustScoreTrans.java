package com.hjsj.hrms.transaction.performance.scoreAjust;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchAjustScoreTrans.java</p>
 * <p>Description:保存评分调整</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-15 09:13:31</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveAjustScoreTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String plan_id = (String) this.getFormHM().get("plan_id");
		plan_id = PubFunc.decrypt(SafeCode.decode(plan_id));
		String object_id = (String) this.getFormHM().get("object_id");
		object_id = PubFunc.decrypt(SafeCode.decode(object_id));
		String oper = (String) this.getFormHM().get("oper");
		ArrayList pointScoreList = (ArrayList) this.getFormHM().get("pointScore");	
		pointScoreList=pointScoreList==null?new ArrayList():pointScoreList;//没有可调整的指标就直接点保存的情况
		
		try
		{	
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(), object_id, plan_id,this.userView);
			if("1".equals(oper) || "2".equals(oper))
			{
				HashMap newResultMap = bo.SaveScoreAjust(pointScoreList,this.userView,oper); 			
				this.getFormHM().put("totalScore", newResultMap.get("totalScore")); 
				this.getFormHM().put("resultdesc", newResultMap.get("resultdesc")); 
				this.getFormHM().put("ordering", newResultMap.get("ordering")); 
				this.getFormHM().put("oper", oper);
				
			}else if("3".equals(oper))//调整标度 改变分值的显示
			{
				String point_id = (String) this.getFormHM().get("point_id");
				this.getFormHM().put("point_id", point_id);
				String adjustScore=bo.getAdjustScoreByGrade(pointScoreList);
				this.getFormHM().put("adjustScore", adjustScore);					
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
