package com.hjsj.hrms.transaction.general.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:ShowOpinionTrans.java</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Dec 5, 2012 12:00:49 PM 
 * @author dengcan
 * @version 6.x
 */
public class ShowOpinionTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
		 
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
	/*		String model=(String)map.get("model");  //6:报备  7 加签
			if(model.equals("6"))
			{
				this.getFormHM().put("topic","");
			}
			else */
			{
				String topic="";
				String taskState="";  //任务状态
				String task_id=(String)this.getFormHM().get("taskid");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select content,task_State from t_wf_task where task_id="+task_id);
				if(this.frowset.next())
				{
					topic=Sql_switcher.readMemo(this.frowset,"content");
					taskState=this.frowset.getString("task_State");
				}
				this.getFormHM().put("topic", topic);
				this.getFormHM().put("taskState",taskState);
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
