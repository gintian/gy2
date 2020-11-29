/*
 * 创建日期 2005-8-12
 *
 */
package com.hjsj.hrms.transaction.welcome;

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
 *要点描述结果详细信息
 */
public class InvPointDescribeTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String itemid="0";
		String pointid="0";
		if(hm.get("pointid")!=null)
		{
			pointid=hm.get("pointid").toString();
		}
		
		
		if(hm.get("itemid")!=null)
		{
			itemid=hm.get("itemid").toString();
		}
		//System.out.println("InvPointDescribeTrans---->itemid-->"+itemid+"--->pointid-->"+pointid);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sb=new StringBuffer();
		sb.append("select staff_id,context from investigate_result where state=2 and pointid='");
		sb.append(pointid);
		sb.append("' and itemid='");
		sb.append(itemid);
		sb.append("'");
		ArrayList list=new ArrayList();
		String temp="";
		try
		{
			/**
			 * 得到题目名称
			 */
			this.frowset=dao.search("select name from investigate_item where itemid='"+itemid+"'");
			if(this.frowset.next())
			{
				this.getFormHM().put("item",this.frowset.getString("name"));
			}
			this.frowset=dao.search(sb.toString());
			while(this.frowset.next())
			{
				WelcomeForm wf=new WelcomeForm();
				wf.setUserName(PubFunc.nullToStr(this.frowset.getString("staff_id")));
				temp=PubFunc.nullToStr(this.frowset.getString("context"));
				if(!"".equals(temp.trim()))
				{
					//System.out.println("InvPointDescribeTrans--while-->"+temp);
					wf.setContext(temp);
					list.add(wf);
				}
			}
			
			this.getFormHM().put("pointList",list);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		
		
	}

}
