package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:结束审批任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 13, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class FinishedTaskTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=this.getFormHM();
			String taskid=(String)(String)hm.get("taskid");
			String ins_id=(String)hm.get("ins_id");	
			String tabid=(String)hm.get("tabid");
			
			LazyDynaBean actor=(LazyDynaBean)hm.get("actor");
			String actorid="";
			String actor_type="";
			if(this.userView.getStatus()==0)
			{
				actorid=this.userView.getUserName();
				actor_type="4";
			}
			else
			{
				actorid=this.userView.getDbname()+this.userView.getA0100();
				actor_type="1";
			}
			
			WF_Actor wf_actor=new WF_Actor(actorid,actor_type);
			wf_actor.setContent(SafeCode.decode((String)actor.get("content")).replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
			wf_actor.setEmergency((String)actor.get("pri")); //优先级
			wf_actor.setSp_yj((String)actor.get("sp_yj"));//审批意见   01:同意
			wf_actor.setActorname(this.userView.getUserFullName());
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
			ins.finishedTask(Integer.parseInt(taskid),Integer.parseInt(ins_id),wf_actor,this.userView);
			RecordVo ins_vo=new RecordVo("t_wf_instance");
			ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
			String	approveopinion = tablebo.getApproveOpinion(ins_vo, taskid,wf_actor,"");
			String opinionstr = " select * from templet_"+tabid+" where ins_id="+ins_id+" and submitflag=1";
			String opinion_field = tablebo.getOpinion_field();
			ArrayList fieldlist=tablebo.getAllFieldItem();
			for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(j);
				String field_name=fielditem.getItemid();
				if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
				{
				 
					tablebo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
					break;
				}
					
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
