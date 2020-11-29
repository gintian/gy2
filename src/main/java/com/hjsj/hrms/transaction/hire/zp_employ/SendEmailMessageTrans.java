/*
 * Created on 2005-9-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_employ;

import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SendEmailMessageTrans</p>
 * <p>Description:发送邮件通知</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SendEmailMessageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String count=(String)this.getFormHM().get("count");
			ArrayList toAddr=(ArrayList)this.getFormHM().get("toAddrlist");
			String topic = ResourceFactory.getProperty("label.hireemploye.sendemailtopic");
			String content = ResourceFactory.getProperty("label.hireemploye.sendemailconent");
			EMailBo eMailBo=new EMailBo(this.getFrameconn(),true,"dbname");
			ArrayList bodylist=new ArrayList();
			ArrayList filelist=new ArrayList();
			ArrayList cclist=new ArrayList();
			bodylist.add(content);	
			if(toAddr!=null && !toAddr.isEmpty())
			  eMailBo.sendEqualEmail(topic,bodylist,filelist,userView.getUserId(),toAddr,cclist);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
	
}
