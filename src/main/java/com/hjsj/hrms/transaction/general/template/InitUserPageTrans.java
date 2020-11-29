package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class InitUserPageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList specialRoleList=new ArrayList();
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String specialRoleNodeId=(String)hm.get("specialRoleNodeId");
			String sp_mode=(String)hm.get("sp_mode");
			String selfapply=(String)hm.get("selfapply");
			String tabid=(String)this.getFormHM().get("tabid");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String taskid=(String)this.getFormHM().get("taskid");
			String batch_task=(String)hm.get("batch_task");//bug33055 单子中人有多个直接领导，7x包60锁查询的不是勾选人，而是列表中最后一个人的。
			if(batch_task==null)
				batch_task = "";
			if(tabid==null)
				tabid=(String)hm.get("tabid");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			WorkflowBo wfBo=new  WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView); 
			if("0".equals(sp_mode))      //自动流转
			{
					String[] temps=batch_task.split(",");
					for(int i=0;i<temps.length;i++)
					{
						//bug33055 单子中人有多个直接领导，7x包60锁查询的不是勾选人，而是列表中最后一个人的。
						if(temps[i] != null && temps[i].trim().length() > 0){
							ArrayList valueList=new ArrayList();
							valueList.add(new Integer(temps[i]));
							valueList.add(new Integer(tabid));
							RowSet rowset=dao.search("select ins_id from t_wf_task_objlink where task_id=? and submitflag=1 and tab_id=? and state<>3 ",valueList); ////20160630 dengcan
							if(rowset.next())
							{
								taskid=temps[i];
								ins_id=rowset.getString("ins_id");
								break;
							}
							PubFunc.closeDbObj(rowset);
						}
					}
				this.getFormHM().put("specialRoleMap",wfBo.getSpecialRoleMap(specialRoleNodeId,Integer.parseInt(ins_id),Integer.parseInt(taskid),selfapply));
			}
			else if("1".equals(sp_mode)) //手工流转
			{
				String roleid=(String)hm.get("roleid");
				String role_property=(String)hm.get("role_property");
				this.getFormHM().put("specialRoleMap",wfBo.getSpecialRoleMap(roleid,role_property));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
