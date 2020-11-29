package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class NewHelpPepoleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView);
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt = "init";
			if(map.get("opt")!=null)
				opt=(String)map.get("opt");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String objectid=(String)this.getFormHM().get("objectid");
			String p0400="";
			ArrayList planList = bo.getPlanList(plan_id);
			String to_plan_id="";
			String to_itemid="";
			String qzfp="";
			String fromflag="";
			String p0401="";
			String p0407="";
			String type="";
			String group_id="";
			String taskid="";
			String task_type="";
			if("init".equals(opt))
			{
				if(planList.size()>0)
					to_plan_id=((CommonData)planList.get(0)).getDataValue();
				qzfp=(String)map.get("qz");
				fromflag=(String)map.get("fromflag");
				p0401=(String)map.get("p0401");
				p0407=SafeCode.decode((String)map.get("p0407"));
				type=(String)map.get("type");
				group_id=(String)map.get("group_id");
				taskid=(String)map.get("task_id");
				task_type=(String)map.get("task_type");
				p0400=(String)map.get("p0400");
			}
			else if("init2".equals(opt)){
				to_plan_id=(String)this.getFormHM().get("to_plan_id");
				qzfp=(String)this.getFormHM().get("qzfp");
				fromflag=(String)this.getFormHM().get("fromflag");
				p0401=(String)this.getFormHM().get("p0401");
				p0407=(String)this.getFormHM().get("p0407");
				type=(String)map.get("type");
				group_id=(String)this.getFormHM().get("group_id");
				taskid=(String)this.getFormHM().get("taskid");
				task_type=(String)this.getFormHM().get("task_type");
				p0400=(String)this.getFormHM().get("p0400");
			}
			ArrayList itemList  = bo.getPlanItem(to_plan_id);
			if("init".equals(opt))
			{
				if(itemList.size()>0)
					to_itemid=((CommonData)itemList.get(0)).getDataValue();
			}else{
				to_itemid=(String)this.getFormHM().get("to_itemid");
			}
			ArrayList objectList = bo.getObjectList(to_plan_id, p0400,group_id,task_type,to_itemid);
			this.getFormHM().put("to_plan_id", to_plan_id);
			this.getFormHM().put("p0400", p0400);
			this.getFormHM().put("objectid",objectid);
			this.getFormHM().put("to_itemid",to_itemid);
			this.getFormHM().put("planList", planList);
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("objectList", objectList);
			this.getFormHM().put("qzfp", qzfp);
			this.getFormHM().put("fromflag", fromflag);
			this.getFormHM().put("p0401", p0401);
			this.getFormHM().put("p0407", p0407);
			this.getFormHM().put("type", type);
			this.getFormHM().put("taskid",taskid);
			this.getFormHM().put("group_id", group_id);
			this.getFormHM().put("task_type", task_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
