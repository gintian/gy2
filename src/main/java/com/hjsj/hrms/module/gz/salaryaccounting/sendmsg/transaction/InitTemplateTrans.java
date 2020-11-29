package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitTemplateTrans 
 * 类描述：薪资发放-选择模板
 * 创建人：sunming
 * 创建时间：2015-7-3
 * @version
 */
public class InitTemplateTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException
	{
		try
		{
			SendMsgBo bo = new SendMsgBo(this.getFrameconn(), userView);
			/**
			 取得邮件模板
			 */
			ArrayList templateList=bo.getEmailTemplateList(2);
			ArrayList<HashMap> list = new ArrayList<HashMap>();
			String tempId = "";//模板id
			String tempDesc = "";//模板name
			DynaBean dynabean = null;
			for(int i=0;i<templateList.size();i++){
			    dynabean=(DynaBean)templateList.get(i);
				tempId = dynabean.get("id").toString();
				tempDesc = dynabean.get("name").toString();
				HashMap tempMap = new  HashMap();
				tempMap.put("id", tempId);
				tempMap.put("name", tempDesc);
				list.add(tempMap);
			}
			this.getFormHM().put("data", list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
