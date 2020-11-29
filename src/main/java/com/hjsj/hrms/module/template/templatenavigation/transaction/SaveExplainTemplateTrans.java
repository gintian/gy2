package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 保存业务说明
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 11, 2016</p> 
 *@author hej
 *@version 1.0
 */
public class SaveExplainTemplateTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String content=(String)this.getFormHM().get("content");
		content= PubFunc.keyWord_reback(content);
		if(tabid==null||tabid.length()<=0)
			return;
		String update="update template_table set content=? where tabid=?";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		list.add(content);
		list.add(tabid);
		try
		{
			dao.update(update,list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
