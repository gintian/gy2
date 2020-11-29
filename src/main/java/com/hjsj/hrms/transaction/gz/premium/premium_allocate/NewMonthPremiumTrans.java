package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:NewMonthPremiumTrans.java
 * </p>
 * <p>
 * Description:新增当前机构的所有子机构
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-11-30 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class NewMonthPremiumTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String setid = (String) this.getFormHM().get("orgsubset");
	String b0110s = (String) this.getFormHM().get("b0110s");
	String theYear = (String) this.getFormHM().get("year");
	String theMonth = (String) this.getFormHM().get("month");
	String busiField = setid + "z0";// 业务日期字段

	String date = theYear + "-" + theMonth + "-1";
	Date src_d = DateUtils.getDate(date, "yyyy-MM-dd");
	java.sql.Date d = new java.sql.Date(src_d.getTime());

	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
	String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标
	String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
	String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "keep_save_field");// 封存字段
	String checkUn_field = xml.getNodeAttributeValue("/Params/BONUS_SET","checkUn_field");//奖金核算单位标识指标
	
	String[] b0110ss = b0110s.split(",");
	String busiDateSqlStr = Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;

	String sqlStr2 = "select b0110,max(i9999) i9999 from " + setid + " group by b0110";
	ContentDAO dao = new ContentDAO(this.frameconn);
	HashMap map = new HashMap();
	String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
	try
	{
	    this.frowset = dao.search(sqlStr2);
	    while (this.frowset.next())
	    {
		String b0110 = this.frowset.getString("b0110");
		Integer i9999 = new Integer(this.frowset.getInt("i9999"));
		map.put(b0110, i9999);
	    }
	    ArrayList list = new ArrayList();
	    for (int i = 0; i < b0110ss.length; i++)
	    {
		String b0110 = b0110ss[i];
		String childOrgSqlStr = "select codeitemid from organization where  codesetid in ('UM','UN')  and codeitemid like '" + b0110 + "%'";
		childOrgSqlStr+=" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ";
		String sqlStr1 = childOrgSqlStr;
		sqlStr1 += " and codeitemid not in (select b0110 from " + setid + " where b0110 in (" + childOrgSqlStr + ") and " + busiDateSqlStr + ")";

		
		this.frowset = dao.search(sqlStr1);
		while (this.frowset.next())
		{
		    b0110 = this.frowset.getString("codeitemid");
		    int i9999 = 1;
		    if (map.get(b0110) != null)
			i9999 += ((Integer) map.get(b0110)).intValue();
		    RecordVo vo = new RecordVo(setid);
		    vo.setString("b0110", b0110);
		    vo.setInt("i9999", i9999);
		    vo.setString("createusername", this.getUserView().getUserName());
		    vo.setString("modusername", this.getUserView().getUserName());
		    vo.setDate("createtime", new Date());
		    vo.setDate("modtime", new Date());
		    vo.setString(dist_field.toLowerCase(), "2");
		    vo.setString(rep_field.toString(), "2");
		    vo.setDate(busiField.toLowerCase(), d);
		    vo.setInt(setid.toLowerCase() + "z1", 1);
		    vo.setString(keep_save_field.toLowerCase(), "2");
			vo.setString(checkUn_field.toLowerCase(), "1");
		    list.add(vo);
		}
		
	    }
	    dao.addValueObject(list);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    this.getFormHM().put("ok","0");
	    throw GeneralExceptionHandler.Handle(e);
	}
	this.getFormHM().put("ok","1");
    }

}
