package com.hjsj.hrms.transaction.general.template.operation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 业务说明
 * <p>Title:OperationExplainTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 23, 2006 3:58:36 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ShowExplainTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String tabid=(String)hm.get("tabid");
		if(tabid==null||tabid.length()<=0)
			return;
		String sql="select content from template_table where tabid='"+tabid+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String content="";
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				content=Sql_switcher.readMemo(this.frowset,"content");
			}
		}catch(Exception e)
		{
			 e.printStackTrace();
		}
		this.getFormHM().put("content",content);
		this.getFormHM().put("tabid",tabid);
	}

}
