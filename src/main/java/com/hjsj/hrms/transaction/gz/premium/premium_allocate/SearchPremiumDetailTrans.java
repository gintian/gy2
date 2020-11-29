package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 * <p>
 * Title:AddMonthPremiumTrans.java
 * </p>
 * <p>
 * Description:部门月奖金明细
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
public class SearchPremiumDetailTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String currentOrg = (String) this.getFormHM().get("operOrg");
	String theyear = (String) this.getFormHM().get("year");
	String themonth = (String) this.getFormHM().get("month");
	
	String str = "";
	String plan_id = "";
	try
	{
	    ContentDAO dao = new ContentDAO(this.frameconn);
	    // 年度
	    String sql = "select distinct per_plan.plan_id from per_plan,per_object where per_plan.plan_id=per_object.plan_id and method=2 and ( sp_flag='03' or sp_flag='06' )   and object_id='"
		    + currentOrg + "' ";
	    sql += " and  theyear='" + theyear + "' ";
	    RowSet rowSet = dao.search(sql);
	    while (rowSet.next())
		plan_id += "," + rowSet.getString("plan_id");

	    // 半年
	    String thequarter = "1";
	    if ("'01','02','03','04','05','06'".indexOf(themonth) != -1)
		thequarter = "1";
	    else if ("'07','08','09','10','11','12'".indexOf(themonth) != -1)
		thequarter = "2";

	    sql = "select distinct per_plan.plan_id from per_plan,per_object where per_plan.plan_id=per_object.plan_id and method=2 and ( sp_flag='03' or sp_flag='06' )  and object_id='"
		    + currentOrg + "' ";
	    sql += " and  theyear='" + theyear + "' ";
	    sql += " and ( ( cycle=1 and Thequarter='" + thequarter + "' ) ";
	    if ("01".equals(thequarter) || "1".equals(thequarter))
	    {
		sql += " or (cycle=2 and ( Thequarter='01' or Thequarter='02') ) ";
		sql += " or (cycle=3 and Themonth in ('01','02','03','04','05','06') ) ";
	    } else
	    {
		sql += " or (cycle=2 and ( Thequarter='03' or Thequarter='04') ) ";
		sql += " or (cycle=3 and Themonth in ('07','08','09','10','11','12') ) ";
	    }
	    sql += " )";
	    rowSet = dao.search(sql);
	    while (rowSet.next())
		plan_id += "," + rowSet.getString("plan_id");

	    // 季度
	    if ("'01','02','03',".indexOf(themonth) != -1)
		thequarter = "01";
	    else if ("'04','05','06'".indexOf(themonth) != -1)
		thequarter = "02";
	    else if ("'07','08','09'".indexOf(themonth) != -1)
		thequarter = "03";
	    else if ("'10','11','12'".indexOf(themonth) != -1)
		thequarter = "04";

	    sql = "select per_plan.plan_id from per_plan,per_object where per_plan.plan_id=per_object.plan_id and method=2 and ( sp_flag='03' or sp_flag='06' )  and object_id='" + currentOrg + "' ";
	    sql += " and  theyear='" + theyear + "' ";
	    sql += " and ( ( cycle=2 and Thequarter='" + thequarter + "' ) ";
	    if ("01".equals(thequarter) || "1".equals(thequarter))
	    {
		sql += " or (cycle=3 and Themonth in ('01','02','03') ) ";
	    } else if ("02".equals(thequarter) || "2".equals(thequarter))
	    {
		sql += " or (cycle=3 and Themonth in ('04','05','06') ) ";
	    } else if ("03".equals(thequarter) || "3".equals(thequarter))
	    {
		sql += " or (cycle=3 and Themonth in ('07','08','09') ) ";
	    } else if ("04".equals(thequarter) || "4".equals(thequarter))
	    {
		sql += " or (cycle=3 and Themonth in ('10','11','12') ) ";
	    }
	    sql += " )";
	    rowSet = dao.search(sql);
	    while (rowSet.next())
		plan_id += "," + rowSet.getString("plan_id");

	    // 月
	    sql = "select per_plan.plan_id from per_plan,per_object where per_plan.plan_id=per_object.plan_id and method=2 and ( sp_flag='03' or sp_flag='06' )  and object_id='" + currentOrg + "' ";
	    sql += " and  theyear='" + theyear + "' ";
	    sql += " and  cycle=3 and Themonth='" + themonth + "' ";
	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
		plan_id += "," + rowSet.getString("plan_id");
	    }

	    if (plan_id.length() > 0)
		str = "/performance/objectiveManage/objectiveCard.do?b_query2=query&operator=1&from=jj&planids=" + plan_id + "&object_id=" + currentOrg;
	    else
		str="/gz/premium/premium_allocate/monthPremiumList.do?br_khtable=link";

	    if(rowSet!=null)
		rowSet.close();
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	this.getFormHM().put("paramStr", str);
    }

}
