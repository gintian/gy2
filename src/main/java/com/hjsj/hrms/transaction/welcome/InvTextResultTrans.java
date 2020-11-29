/*
 * 创建日期 2005-8-10
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @author luangaojiong 对热点调查中的问答的结果进行查阅
 */
public class InvTextResultTrans extends IBusiness {

	/*
	 * （非 Javadoc）
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		/**
		 * 得到项目id
		 */
		String itemid = "0";
		if (this.getFormHM().get("hotitemid") != null) {
			itemid = this.getFormHM().get("hotitemid").toString();
		} else {
			return;
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/**
		 * 取得项目名称
		 */
		try {

			this.frowset = dao.search("select name from investigate_item where itemid='"
							+ itemid + "'");
			if (this.frowset.next()) {
				this.getFormHM().put("item", this.frowset.getString("name"));

			} else {
				this.getFormHM().put("item", "");
			}
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
		/**
		 * 取得输出列表
		 */
		ArrayList list=new ArrayList();
		try
		{
			this.frowset=dao.search("select * from investigate_content where state=2 and itemid='"+itemid+"'");
			while(this.frowset.next())
			{
				WelcomeForm wf=new WelcomeForm();
				wf.setUserName(PubFunc.nullToStr(this.frowset.getString("staff_id")));
				wf.setContext(PubFunc.nullToStr(this.frowset.getString("context")));
				list.add(wf);
			}
			
			this.getFormHM().put("itemwhilelst",list);
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}