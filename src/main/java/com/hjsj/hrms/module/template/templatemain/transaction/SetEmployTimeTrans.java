package com.hjsj.hrms.module.template.templatemain.transaction;

import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SetEmployTimeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		RowSet rowSet=null;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap formMap = this.getFormHM();
			TemplateFrontProperty frontProperty = new TemplateFrontProperty(
					formMap);
			String taskId = frontProperty.getTaskId();
			String[] lists=StringUtils.split(taskId,",");
			ArrayList updList=new ArrayList();
			String taskid = "";
			for(int i=0;i<lists.length;i++){
				if(StringUtils.isBlank(lists[i]))
					continue;
				if(i==0)
					taskid+=lists[i];
				else
					taskid+=","+lists[i];
			}
			if(StringUtils.isNotBlank(taskid)) {
				rowSet = dao.search("select DISTINCT t2.task_id from t_sys_role t4,t_wf_node t1,t_wf_task t3,t_wf_task_objlink t2 where "
						+ "t1.node_id=t2.node_id and t2.task_id=t3.task_id and t3.actorid=t4.role_id and "
						+ Sql_switcher.isnull("t2.state","0")+"=0 and t2.task_id in("+taskid+") and "
						+ "t1.nodetype=2 and t4.role_property not in (9,10,11,12,13,14)");
				while(rowSet.next()){
					String task_id = rowSet.getString("task_id");
					ArrayList tempList=new ArrayList();
					Timestamp dateTime = new Timestamp((new Date()).getTime());
					tempList.add(dateTime);
					tempList.add(task_id);
					updList.add(tempList);
				}
			}
			if(updList.size()>0)
			{ 
				dao.batchUpdate("update t_wf_task_objlink set locked_time=? where task_id=?",updList );
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
	}
}
