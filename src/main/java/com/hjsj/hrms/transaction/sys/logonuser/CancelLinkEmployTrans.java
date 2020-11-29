/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Arrays;

/**
 * <p>Title:取消关联</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Dec 28, 200610:56:06 AM
 * @author chenmengqing
 * @version 4.0
 */
public class CancelLinkEmployTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String username=(String)this.getFormHM().get("username");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo oper_vo=new RecordVo("operuser");
			oper_vo.setString("username",username);
			/*oper_vo=dao.findByPrimaryKey(oper_vo);
			if(oper_vo==null)
				return;*/
			oper_vo.setString("a0100","");
			oper_vo.setString("nbase","");
			oper_vo.setString("fullname", "");
			dao.updateValueObject(oper_vo);
			
			
			//同步更新流程定义t_wf_actor表里的actorname数据
			dao.update("update t_wf_actor set actorname=? where actor_type=4 and actorid=? ",Arrays.asList(username,username));
			
			this.getFormHM().clear();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
