package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * <p>Title:TestCodeAccordTrans.java</p>
 * <p>Description:是否需要代码对应</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-03 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TestCodeAccordTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String destFildSet = (String)this.getFormHM().get("destFildSet");
	String fieldName = (String)this.getFormHM().get("fieldName");
	String imgcount = (String)this.getFormHM().get("imgcount");
	String strSql="select codesetid from fielditem where fieldsetid='"+destFildSet+"' and itemtype='A' and codesetid!='0' and itemid='"+fieldName+"'";
	try
	{

	    ContentDAO dao = new ContentDAO(this.frameconn);
	    RowSet rs = dao.search(strSql);
	    if (rs.next())
	    {
		this.getFormHM().put("disp", "block");
		String codesetid = rs.getString("codesetid");
		this.getFormHM().put("codesetid", codesetid);
	    }
	    else
	    {
		this.getFormHM().put("disp", "none");
		this.getFormHM().put("codesetid", "0");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	this.getFormHM().put("destFildid", fieldName);
	this.getFormHM().put("imgcount", imgcount);
    }
}
