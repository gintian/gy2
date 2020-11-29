package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * <p>Title:GetMailTempletInfoTrans.java</p>
 * <p>Description>:不走审批的提交数据查询信息</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-9-14 下午02:00:45</p>
 * <p>@author:hej</p>
 * <p>@version: 7.0</p>
 */
public class GetMailTempletInfoTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			RowSet rowSet=dao.search("select * from email_name where id="+id);
			if(rowSet.next())
			{
				String subject=rowSet.getString("subject");
				String content=Sql_switcher.readMemo(rowSet,"content");
				this.getFormHM().put("subject", SafeCode.encode(subject));
				this.getFormHM().put("content", SafeCode.encode(content));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
