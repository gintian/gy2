/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:终止任务</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 3, 20069:06:50 AM
 * @author chenmengqing
 * @version 4.0
 */
public class KillTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList ins_list=(ArrayList)this.getFormHM().get("selectedlist");
		try
		{
			/**loop end.*/
			for(int i=0;i<ins_list.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)ins_list.get(i);
				String ins_id=(String)dynabean.get("ins_id");//流程实例
				String tabid=(String)dynabean.get("tabid");//表格号
				String taskid=(String)dynabean.get("task_id");
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
				RecordVo ins_vo=new RecordVo("t_wf_instance");
				ins_vo.setInt("ins_id",Integer.parseInt(ins_id));	
				
				WF_Actor wf_actor=new WF_Actor(this.userView.getUserName(),"1");
				wf_actor.setContent(ResourceFactory.getProperty("error.wf.killed")); //批复内容
				wf_actor.setEmergency("1"); //优先级
				wf_actor.setSp_yj("03");//审批意见
				wf_actor.setActorname(this.userView.getUserFullName());			
				/**=5为非正常结束任务*/
				if(ins.finishTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView,"5"))
				{
					
				}				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
