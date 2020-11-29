package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 初始化 批量发送邮件
 * @author dengcan
 * @serialData  2007/06/06
 *
 */
public class InitBatchSendMailTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b_init=(String)hm.get("b_init");
			String type=(String)hm.get("type");			// 0：发送邮件  1：群发邮件
			String status=(String)hm.get("status");
			if("init".equals(b_init)&& "0".equals(type))
			{
				String a0100s=((String)hm.get("a0100s")).substring(9);
				this.getFormHM().put("a0100s",a0100s);
			}
			
			
			String mailTempID=(String)this.getFormHM().get("mailTempID");
			String title="";
			String content="";
			ArrayList zbj_list=new ArrayList();
			ArrayList zb_list=new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			EmployResumeBo bo=new EmployResumeBo(this.getFrameconn());
			if(mailTempID!=null&&mailTempID.trim().length()>0)
			{
				this.frowset=dao.search("select title,content from t_sys_msgtemplate where template_id="+mailTempID);
				if(this.frowset.next())
				{
					title=this.frowset.getString("title");
					content=Sql_switcher.readMemo(this.frowset,"content");
				}
				zbj_list=bo.getZbjList();
				zb_list=bo.getZbList("Z05");
			}
			else
			{
				mailTempID="";
			
			}
			
			this.getFormHM().put("type",type);
			this.getFormHM().put("status",status);
			this.getFormHM().put("zbj_list",zbj_list);
			this.getFormHM().put("zb_list",zb_list);
			this.getFormHM().put("mailTempID",mailTempID);
			this.getFormHM().put("title",title);
			this.getFormHM().put("content",content);
			this.getFormHM().put("mailTempList",bo.getMailTempList(this.getUserView()));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	


}
