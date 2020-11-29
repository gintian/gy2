package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchUnderlingObjectiveViewTrans.java</p>
 * <p>Description>:目标执行情况</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 09, 2010 09:25:37 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchUnderlingObjectiveViewTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = "";
			String posid = "";
			String a0100="";
			String status="-2";
			String year="";
			String opt = (String)map.get("opt");
			String entranceType=(String)map.get("entranceType");
			String object_type = "";
			if("1".equals(opt))
			{
			    plan_id = (String)map.get("plan_id");
				posid =(String)map.get("posid");
				a0100=(String)map.get("a0100");
				object_type=map.get("object_type")==null?"":(String)map.get("object_type");
				if("".equals(object_type)&&plan_id.length()>0){
					RecordVo vo = new RecordVo("per_plan");
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					vo.setInt("plan_id", Integer.parseInt(plan_id));
					vo = dao.findByPrimaryKey(vo);
					object_type = ""+vo.getInt("object_type");
				}
				
				this.getFormHM().put("object_type", object_type);
				
			}
			else if("2".equals(opt))
			{
				plan_id=(String)this.getFormHM().get("plan_id");
				posid=(String)this.getFormHM().get("posid");
				a0100=(String)this.getFormHM().get("a0100");
				status = (String)this.getFormHM().get("status");
			}
			else if("3".equals(opt))
			{
				plan_id=(String)map.get("plan_id");
				posid=(String)map.get("posid");
				a0100=(String)map.get("a0100");
				status = (String)map.get("status");
			}else if("4".equals(opt))
			{
				plan_id="-1";
				posid=(String)map.get("posid");
				a0100=(String)map.get("a0100");
				year=(String)map.get("year");
			}
			if("-1".equals(posid))
				posid=this.userView.getUserPosId();
			if("-1".equals(a0100))
				a0100=this.userView.getA0100();
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn());
			ArrayList dbname = new ArrayList();
			dbname.add("USR");
			object_type = (String)this.getFormHM().get("object_type");
			ArrayList personList = bo.getInPlanObjectStaff( plan_id, dbname,status,object_type,this.getUserView());
			ArrayList statusList = new ArrayList();
			statusList.add(new CommonData("-2",ResourceFactory.getProperty("label.all")));
			statusList.add(new CommonData("01",MyObjectiveBo.getSpflagDesc("01")));
			statusList.add(new CommonData("02",MyObjectiveBo.getSpflagDesc("02")));
			statusList.add(new CommonData("03",MyObjectiveBo.getSpflagDesc("03")));
			statusList.add(new CommonData("06",MyObjectiveBo.getSpflagDesc("06")));
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("personList",personList);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("status",status);
			this.getFormHM().put("posid",posid);
			this.getFormHM().put("a0100",a0100);	
			this.getFormHM().put("entranceType", entranceType);
			this.getFormHM().put("objMainbodys", bo.getObjMainbodys());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
