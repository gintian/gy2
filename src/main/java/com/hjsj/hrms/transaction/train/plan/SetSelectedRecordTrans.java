package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetSelectedRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String selectID=(String)hm.get("selectID");
		selectID =PubFunc.keyWord_reback(selectID);
		StringBuffer r3101=new StringBuffer("");
		String[] ss=selectID.split("\\*");
		ArrayList selectedList=new ArrayList();
		for(int i=0;i<ss.length;i++)
		{
			r3101.append(",'"+ss[i]+"'");
			
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select r31.r3130,r25.r2502 from r31 left join r25 on r31.r3125=r25.r2501  where r3101 in ("+r3101.substring(1)+")");
			CommonData data=null;
			while(this.frowset.next())
			{
				String name=frowset.getString("r3130");
				name=name.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("&lt;","<").replaceAll("&gt;",">");
				if(this.frowset.getString("r2502")!=null)
					name+="(计划: "+this.frowset.getString("r2502")+")";
				data=new CommonData("",name);
				selectedList.add(data);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("selectedList",selectedList);
	}

}
