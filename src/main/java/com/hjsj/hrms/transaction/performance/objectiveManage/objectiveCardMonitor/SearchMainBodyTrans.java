package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SearchMainBodyTrans.java</p>
 * <p>Description>:SearchMainBodyTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 25, 2009 3:34:19 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchMainBodyTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("object_id");
			String plan_id=(String)hm.get("plan_id");
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo = dao.findByPrimaryKey(vo);
			String object_type=vo.getString("object_type");
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn());
			boolean isORG=false;
			HashMap leaderMap = null;
			if(!"2".equals(object_type))
			{
				isORG=true;
				leaderMap=bo.getOrgLeader(plan_id,null);
			}
		   HashMap map =null;
		   if(isORG)
		   {
			   LazyDynaBean bean = (LazyDynaBean)leaderMap.get(object_id+plan_id);
			   String a0100=(String)bean.get("a0100");
			   map=bo.getAllMainBodyList(plan_id, a0100,isORG,object_id);
		   }
		   else
		   {
			   map=bo.getAllMainBodyList(plan_id, object_id,isORG,object_id);
		   }
		   ArrayList list = (ArrayList)map.get("list");
		   String orgInfo=(String)map.get("orgInfo");
		   String targetAppMode=(String)map.get("targetAppMode");
		   String targetMakeSeries=(String)map.get("targetMakeSeries");
		   String market=(String)map.get("market");
		   this.getFormHM().put("object_id",object_id);
		   this.getFormHM().put("plan_id",plan_id );
		   this.getFormHM().put("khType", targetAppMode);
		   this.getFormHM().put("level", targetMakeSeries);
		   this.getFormHM().put("mainbodyList", list);
		   this.getFormHM().put("market",market);
		   this.getFormHM().put("a0101",(String)map.get("orgInfo"));
		   this.getFormHM().put("maxLevel", (String)map.get("maxLevel"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
