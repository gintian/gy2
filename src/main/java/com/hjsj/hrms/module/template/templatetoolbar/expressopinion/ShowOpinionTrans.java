package com.hjsj.hrms.module.template.templatetoolbar.expressopinion;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:ShowOpinionTrans.java</p>
 * <p>Description:显示发表意见</p> 
 * <p>Company:hjsj</p> 
 * create time at:Dec 5, 2016 08:00:49 PM 
 * @author lis
 * @version 7.x
 */
public class ShowOpinionTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		try
		{
			String topic="";//发表意见
			String task_id=(String)this.getFormHM().get("task_id");
			String task_ids = "";
			if(task_id.contains(",")){
				String [] taskarr = task_id.split(",");
				for(int i=0;i<taskarr.length;i++){
					String taskid = PubFunc.decrypt(taskarr[i]);
					task_ids+="'"+taskid+"',";
				}
				task_ids = task_ids.substring(0, task_ids.length()-1);
			}else
				task_ids = "'"+PubFunc.decrypt(SafeCode.decode(task_id))+"'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select content,task_State from t_wf_task where task_id in("+task_ids+")");
			while(this.frowset.next())
			{
				topic=Sql_switcher.readMemo(this.frowset,"content");
				if(topic!=null&&!"".equals(topic))
					break;
			}
			this.getFormHM().put("topic", topic);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
