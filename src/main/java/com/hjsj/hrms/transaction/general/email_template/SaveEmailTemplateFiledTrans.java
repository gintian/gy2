package com.hjsj.hrms.transaction.general.email_template;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:SaveEmailTemplateFiledTrans.java</p>
 * <p>Description:保存选择的邮件模板项目</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-21 14:44:44</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class SaveEmailTemplateFiledTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");//项目类型
			String nflag = (String)this.getFormHM().get("flag");//0:指标,1:公式
			String templateId=(String)this.getFormHM().get("id");//模板id
			StringBuffer sql = new StringBuffer();
			if("A".equalsIgnoreCase(type))
			{
				
			}else if("D".equalsIgnoreCase(type))
			{
				
			}else if("N".equalsIgnoreCase(type))
			{
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
