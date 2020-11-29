package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:ChangeSalarySetTrans.java</p>
 * <p>Description>:ChangeSalarySetTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 7, 2009 5:26:48 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ChangeSalarySetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			ArrayList itemList = new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String itemsql = "select distinct itemid,itemdesc,sortid from salaryset where salaryid="+salaryid+" order by sortid";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
		
			this.frowset = dao.search(itemsql);
			while(this.frowset.next())
			{
					//String itemid = this.frowset.getString("itemid");
					String itemdesc = this.frowset.getString("itemdesc");
					CommonData dataobj = new CommonData();
					//dataobj = new CommonData(itemid,itemdesc);
					dataobj = new CommonData(itemdesc,itemdesc);
					itemList.add(dataobj);
			}
			this.getFormHM().put("itemList",itemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
