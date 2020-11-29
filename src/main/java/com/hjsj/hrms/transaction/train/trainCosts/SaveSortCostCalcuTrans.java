package com.hjsj.hrms.transaction.train.trainCosts;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:QueryCostCalcuTrans.java
 * </p>
 * <p>
 * Description:费用计算
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-09-01 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveSortCostCalcuTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String sortStr = (String) hm.get("sortStr");

	String[] items = sortStr.split("@");

	String strSql = "Update HrpFormula set DB_TYPE=? where Unit_type='5' and itemid=?";

	ArrayList list2 = new ArrayList();
	for (int i = 0; i < items.length; i++)
	{

	    ArrayList list = new ArrayList();
	    String temp = items[i];
	    if ("".equals(temp))
		continue;

	    String num = new Integer(i + 1).toString();
	    if (num.length() == 1)
		num = "0000" + num;
	    else if (num.length() == 2)
		num = "000" + num;
	    else if (num.length() == 3)
		num = "00" + num;
	    else if (num.length() == 4)
		num = "0" + num;
	    list.add(num);
	    list.add(temp);
	    list2.add(list);
	}
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	try
	{
	    dao.batchUpdate(strSql, list2);
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }

}
