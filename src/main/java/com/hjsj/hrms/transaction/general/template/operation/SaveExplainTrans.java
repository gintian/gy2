package com.hjsj.hrms.transaction.general.template.operation;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 保存业务说明
 * <p>Title:SaveOperationExplainTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 23, 2006 3:58:56 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveExplainTrans extends IBusiness {


	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String content=(String)this.getFormHM().get("content");
		/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
		content = PubFunc.stripScriptXss(content);
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
