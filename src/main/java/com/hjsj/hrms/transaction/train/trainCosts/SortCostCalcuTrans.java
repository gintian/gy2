package com.hjsj.hrms.transaction.train.trainCosts;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>Title:SortCostCalcuTrans.java</p>
 * <p>Description:费用计算排序</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-01 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SortCostCalcuTrans extends IBusiness
{
    public void execute() throws GeneralException
    {       
	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    String sqlStr = "select itemid,forname from HrpFormula where Unit_type='5' and SetId='R45' order by DB_TYPE";
	    this.frowset = dao.search(sqlStr);
	    while (this.frowset.next())
	    {
		String itemid = this.frowset.getString("itemid");
		String forname = this.frowset.getString("forname");
		CommonData temp = new CommonData(itemid, forname);
		list.add(temp);
		  
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}finally
	{
	    this.getFormHM().put("sortlist", list);
	}
    }

}
