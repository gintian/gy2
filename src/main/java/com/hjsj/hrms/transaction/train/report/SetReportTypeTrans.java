package com.hjsj.hrms.transaction.train.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SetReportTypeTrans.java</p>
 * <p>Description:培训报表编号设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-08-12 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SetReportTypeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String type = (String) hm.get("type");
	this.getFormHM().put("reportId", type);	
	
	String nowYear= PubFunc.getStringDate("yyyy");
	int n = Integer.parseInt(nowYear);
	
	ArrayList list = new ArrayList();
	for(int i=0;i<=10;i++)
	{
	    String temp = new Integer(n-i).toString();
	    CommonData data=new CommonData(temp,temp);	   
	    list.add(data);
	}
    //    CommonData data=new CommonData(nowYear,nowYear);	   
	//list.add(data);
	this.getFormHM().put("yearList", list);	
	   	
    }
}
