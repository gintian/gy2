package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 得到构建指标
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 8, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class BusiMakeFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String tabname=(String)this.getFormHM().get("tabname");
		ArrayList contractedFieldList=new ArrayList();
		ArrayList uncontractedFiledList=new ArrayList();
		if(tabname==null)
			throw GeneralExceptionHandler.Handle(new GeneralException("","得到子集时出错!","",""));
		String tabs[]=tabname.split("/");
		String sysid=tabs[0];
		String setid=tabs[1];
		String sql="select itemid,itemdesc,useflag from t_hr_busifield where fieldsetid='"+sysid+"' order by displayid";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			CommonData da=null;
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				da=new CommonData();
				da.setDataName(this.frowset.getString("itemdesc"));
				da.setDataValue(this.frowset.getString("itemid"));
				if(this.frowset.getString("useflag")!=null&& "1".equals(this.frowset.getString("useflag")))
					contractedFieldList.add(da);
				else
					uncontractedFiledList.add(da);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("contractedFieldList", contractedFieldList);
		this.getFormHM().put("uncontractedFiledList", uncontractedFiledList);
	}

}
