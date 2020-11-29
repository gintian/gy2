/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import java.sql.Connection;

/**
 * <p>Title:WF_task</p>
 * <p>Description:任务列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 20065:35:47 PM
 * @author chenmengqing
 * @version 4.0
 */
public class WF_task {
	/**任务号*/
	private int task_id;
	/**数据库连接*/
	private Connection conn;
	
	public WF_task(int task_id,Connection conn) {
		this.task_id=task_id;
		this.conn=conn;
	}
	
	/**
	 * 重新分发任务，节点不发生转移,支持任意流转
	 * @param actorid 参与者编码
	 * @param actortype 参与者类型
	 * @return
	 * @throws GeneralException
	 */
	public boolean reAssignTask(String actorid,String actortype)throws GeneralException
	{
		boolean bflag=false;
		RecordVo task_vo=new RecordVo("t_wf_task");
		task_vo.setInt("task_id",this.task_id);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo!=null) //不增加记录，更改参与者
			{
				task_vo.setString("actorid",actorid);
				task_vo.setString("actor_type",actortype);
				dao.updateValueObject(task_vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 暂停任务，也可以说TASK_STOP暂停
	 * @return
	 */
	public boolean stopTask()
	{
		boolean bflag=false;
		return bflag;
	}
	
}
