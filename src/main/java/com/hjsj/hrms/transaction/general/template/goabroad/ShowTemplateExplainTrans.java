/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.goabroad;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ShowTemplateExplainTrans</p>
 * <p>Description:查询业务模板说明内容</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 24, 20069:57:33 AM
 * @author chenmengqing
 * @version 4.0
 */
public class ShowTemplateExplainTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String tabid=(String)hm.get("tabid");
		if(tabid==null||tabid.length()<=0)
			return;
		StringBuffer buf=new StringBuffer();
		buf.append("select content from template_table where tabid='");
		buf.append(tabid);
		buf.append("'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String content="";
		try
		{
			this.frowset=dao.search(buf.toString());
			if(this.frowset.next())
				content=Sql_switcher.readMemo(this.frowset,"content");
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		this.getFormHM().put("content",content);
	}

}
