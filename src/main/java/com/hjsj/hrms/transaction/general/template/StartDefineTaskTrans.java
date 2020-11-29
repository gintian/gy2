package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Define_Instance;
import com.hjsj.hrms.businessobject.kq.TemplateDefineBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class StartDefineTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		WF_Actor wf_actor=null;
		boolean isCorrect=false;
		try
		{
			String sp_mode=(String)hm.get("sp_mode");
			if(sp_mode==null|| "".equals(sp_mode))
				sp_mode="0";
			String define_tabid=(String)hm.get("define_tabid");
			ArrayList pk_list=(ArrayList)hm.get("params");
			if("1".equals(sp_mode))
			{
				LazyDynaBean actor=(LazyDynaBean)hm.get("actor");
				//String setname=(String)hm.get("setname");
				//String tabid=(String)actor.get("tabid");
				String actorid=(String)actor.get("name");
				String actorname=(String)actor.get("fullname");
				String actor_type=(String)actor.get("objecttype");
				/**审批对象*/
				wf_actor=new WF_Actor(actorid,actor_type);
				wf_actor.setContent((String)actor.get("content"));//当前提交人的审批意见
				wf_actor.setEmergency((String)actor.get("pri"));
				wf_actor.setSp_yj((String)actor.get("sp_yj")); 
				wf_actor.setActorname(actorname);
			}
			else
			{
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
				wf_actor=new WF_Actor(actorid,actor_type);
				wf_actor.setContent("");//当前提交人的审批意见
				wf_actor.setEmergency("");
				wf_actor.setSp_yj(""); 
				wf_actor.setActorname(this.userView.getUserFullName());	
				wf_actor.setBexchange(false);
			}			
			TemplateDefineBo tablebo=new TemplateDefineBo(this.getFrameconn(),define_tabid,this.userView);
			String url_addr=tablebo.getDefineUrl("appeal_form");
			for(int i=0;i<pk_list.size();i++)
			{
				String param=pk_list.get(i).toString();
				RecordVo ins_vo=new RecordVo("t_wf_instance");			
				WF_Define_Instance define_In=new WF_Define_Instance(this.getFrameconn(),this.userView,"",define_tabid);
				define_In.createTaskInstance(ins_vo, wf_actor,url_addr,param);	
			}
			isCorrect=true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(isCorrect)
			this.getFormHM().put("flag", "0");
		else
		    this.getFormHM().put("flag", "1");		
	}    
}
