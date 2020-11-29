package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:TestKhTempEditTrans.java</p>
 * <p>Description:判断考核计划的模板是否可改</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-25 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class TestKhTempEditTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String plan_id =(String)this.getFormHM().get("thePlan");
		String templId =(String)this.getFormHM().get("templId");
		String canedit = "1";	
		
		ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
		int status = 0;
		if(bo.getPlanVo()!=null)
			status =bo.getPlanVo().getInt("status");
		
		if(status==5)//暂停
		{
			canedit = "1";
		}else if(status==0)//起草 //如果是另存得来的考核计划，且另存时候选择了复制指标权限表，在起草状态也不能修改模板
		{
			String tableName = "PER_POINTPRIV_" + plan_id;
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			if (dbWizard.isExistTable(tableName, false))//判断是否是另存来的 如果是也不可编辑模板
			    canedit="0";
			else
				canedit = "1";
		}		
		else
			canedit="0";
	
		this.getFormHM().put("status", status+"");
		this.getFormHM().put("thePlan", plan_id);
		this.getFormHM().put("templId", templId);
		this.getFormHM().put("canedit", canedit);
		
    }
    
}
