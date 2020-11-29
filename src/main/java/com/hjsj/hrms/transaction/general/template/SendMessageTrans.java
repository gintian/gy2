package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashSet;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:发送消息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 1, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SendMessageTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		try
		{
			String isSendMessage=(String)this.getFormHM().get("isSendMessage");   //1:email  2:sms  3:email and sms
			String context=SafeCode.decode((String)this.getFormHM().get("context"));
			String user_h=SafeCode.decode((String)this.getFormHM().get("user_h"));
			String title=SafeCode.decode((String)this.getFormHM().get("title"));
			String pt_type=(String)this.getFormHM().get("pt_type");   //0:自助  1：业务
			if(pt_type==null)
				pt_type="0";
			String tabid=(String)this.getFormHM().get("tabid");
			String email_staff_value=this.getFormHM().get("email_staff_value")!=null?(String)this.getFormHM().get("email_staff_value"):"0";
			String[] users=user_h.split(",");
			HashSet set=new HashSet();
			for(int i=0;i<users.length;i++)
			{
				set.add(users[i]);
			}
			SendMessageBo bo=new SendMessageBo(this.getFrameconn(),this.userView);
			StringBuffer buf=new StringBuffer("select * from ");
			
			if("0".equals(pt_type))//员工通过自助平台发动申请
			{
				buf.append("g_templet_"+tabid);
				buf.append(" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			}
			else
			{
				buf.append(this.userView.getUserName()+"templet_"+tabid);
				buf.append(" where submitflag=1 ");
			}
			bo.sendMessage(set,isSendMessage,context,title,tabid,buf.toString(),false,email_staff_value,null);
		}
		catch(Exception e)
		{
			e.fillInStackTrace();
		}

	}

}
