package com.hjsj.hrms.transaction.performance.scoreAjust;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchAjustScoreTrans.java</p>
 * <p>Description:评分调整</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-15 14:23:45</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchAjustScoreTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String ajustOper = (String) hm.get("b_ajust");
		String plan_id = PubFunc.decrypt(SafeCode.decode((String) hm.get("plan_id")));
		String object_id = (String) hm.get("object_id");		
        object_id = PubFunc.decrypt(SafeCode.decode(object_id));
		hm.remove("b_ajust");
		hm.remove("planid");
		hm.remove("object_id");
		try
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(), object_id, plan_id,this.userView);
			ArrayList mainbodyList = bo.getMainBodyList(plan_id, object_id);			
			this.getFormHM().put("mainBodyList", mainbodyList);			
			String html="";
			String adjustEvalRange = (String)bo.getPlanParameter().get("AdjustEvalRange"); // 调整范围：0=指标，1=总分.默认为0
			
			RecordVo vo=new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo); 
			if("1".equals(adjustEvalRange)&&vo.getInt("method")==2)
			{
				ObjectCardBo _bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),"8","-2","0");
				String sub_html = _bo.getObjectCardHtml();
				html=bo.getHtml(sub_html,ajustOper,_bo.getObjTableColumnNum());
			}
			else
				html = bo.getScoreAjustHtml(ajustOper);
			
			this.getFormHM().put("scoreAjustHtml", html);
			this.getFormHM().put("adjustEvalRange",adjustEvalRange);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("plan_id", SafeCode.encode(PubFunc.encrypt(plan_id)));
		this.getFormHM().put("object_id", SafeCode.encode(PubFunc.encrypt(object_id)));
		this.getFormHM().put("ajustOper", ajustOper);

	}
}
