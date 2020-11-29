package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 * Title:SearchChildOrgsTrans.java
 * </p>
 * <p>
 * Description:新建部门月奖金
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-12-16 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchChildOrgsTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String setid = (String) this.getFormHM().get("orgsubset");
	String currentOrg = (String) this.getFormHM().get("operOrg");
	String theYear = (String) this.getFormHM().get("year");
	String theMonth = (String) this.getFormHM().get("month");
	String busiField = setid + "z0";// 业务日期字段
	String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
	String busiDateSqlStr = Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;
	StringBuffer sqlStr = new StringBuffer("select * from organization where  codesetid in ('UM','UN') and parentid='" + currentOrg + "'");
	sqlStr.append(" and codeitemid!=parentid and codeitemid not in (");
	sqlStr.append(" select b0110 from " + setid + " where " + busiDateSqlStr + " )");
	sqlStr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
	
	ContentDAO dao = new ContentDAO(this.frameconn);
	ArrayList list = new ArrayList();
	try
	{
	    this.frowset = dao.search(sqlStr.toString());
	    while (this.frowset.next())
	    {
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("itemid", this.frowset.getString("codeitemid"));
		abean.set("itemdesc", this.frowset.getString("codeitemdesc"));
		list.add(abean);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	this.getFormHM().put("orgChilds", list);
    }
}
