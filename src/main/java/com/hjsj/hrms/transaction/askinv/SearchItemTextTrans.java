/*
 * 创建日期 2005-8-13
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 * 
 * 得到项目问答明细列表
 */
public class SearchItemTextTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String itemid = hm.get("itemid").toString();

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sbtp = new StringBuffer();
		sbtp.append("select investigate_item.name,investigate_content.staff_id,investigate_content.context from investigate_item ");
		sbtp.append(",investigate_content ");
		sbtp.append(" where investigate_content.state=2 and investigate_item.itemid=investigate_content.itemid and investigate_item.itemid='");
		sbtp.append(itemid);
		sbtp.append("'");
		ArrayList list = new ArrayList();
		try 
		{
			this.frowset = dao.search(sbtp.toString());
			while (this.frowset.next()) 
			{
				WelcomeForm wf = new WelcomeForm();
				String itemName=PubFunc.nullToStr(this.frowset.getString("name"));
				wf.setItemName(itemName);
 			    this.getFormHM().put("name",itemName);
				wf.setUserName(PubFunc.nullToStr(this.frowset.getString("staff_id")));
				wf.setContext(PubFunc.nullToStr(this.frowset.getString("context")));
				list.add(wf);
			}
			this.getFormHM().put("itemwhilelst", list);
		} 
		catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}