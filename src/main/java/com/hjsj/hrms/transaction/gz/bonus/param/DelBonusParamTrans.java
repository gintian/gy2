package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchBonusParamTrans.java
 * </p>
 * <p>
 * Description:奖金参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-02 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelBonusParamTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String paramStr = (String) this.getFormHM().get("paramStr");
	String[] codeitemids = paramStr.split(",");
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String menuid = (String) hm.get("menuid");

	String sql = "delete from codeitem where codesetid='";
	if ("2".equals(menuid))
	    sql += "49'";
	else if ("3".equals(menuid))
	    sql += "50'";
	sql+=" and codeitemid in (";
	
	ArrayList list = new ArrayList();
	for(int i=0;i<codeitemids.length;i++)	
	{
	    String[] temp = codeitemids[i].split(":");
	    sql+="'"+temp[0]+"',";
	}
	sql = sql.substring(0, sql.length()-1);
	sql +=")";
	ContentDAO dao = new ContentDAO(this.frameconn);
	try
	{
	    dao.delete(sql, list);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}	
    }

}
