package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>Title:SearchCodeSetTrans.java</p>
 * <p>Description:取得代码列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchCodeItemsTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String codeset = (String)this.getFormHM().get("codeset");
//	AdminCode.getCodeItemList(codeset);
	ContentDAO dao=new ContentDAO(this.frameconn);
	ArrayList codeitemlist = new ArrayList();
	try
	{
	    RowSet rs = dao.search("select * from codeitem where codesetid = '"+codeset+"' order by codeitemid");	    
	    CommonData temp = new CommonData("all", "全部");
	    codeitemlist.add(temp);
	    while(rs.next())
	    {
	        temp = new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc"));
	        codeitemlist.add(temp);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	this.getFormHM().put("codeitems", codeitemlist);
    }

}
