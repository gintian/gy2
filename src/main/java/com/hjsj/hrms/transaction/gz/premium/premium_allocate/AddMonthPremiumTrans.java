package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.gz.premium.PremiumBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>
 * Title:AddMonthPremiumTrans.java
 * </p>
 * <p>
 * Description:新建部门月奖金
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-11-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddMonthPremiumTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String setid = (String) this.getFormHM().get("orgsubset");
	String currentOrg = (String) this.getFormHM().get("operOrg");

	PremiumBo bo = new PremiumBo(this.frameconn, this.userView);
	String date = bo.getMaxYearMonth(setid, currentOrg);

	/** yyyy-MM-dd */
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(DateUtils.getDate(date, "yyyy-MM-dd"));

	String sql = (String) this.getFormHM().get("sql");
	ContentDAO dao = new ContentDAO(this.frameconn);

	try
	{
	    this.frowset = dao.search(sql);
	    if (this.frowset.next())
	    {
	        gc.add(Calendar.MONTH, 1);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
	/** 发放次数 */
	String[] ym = StringUtils.split(date, "-");

	this.getFormHM().put("year", ym[0]);
	this.getFormHM().put("month", ym[1]);

    }

}
