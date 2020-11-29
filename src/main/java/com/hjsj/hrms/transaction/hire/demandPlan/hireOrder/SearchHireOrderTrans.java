package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchHireOrderTrans.java
 * </p>
 * <p>
 * Description:招聘订单
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchHireOrderTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String a_code = (String) hm.get("a_code");
	hm.remove("a_code");
	String b_query = (String) hm.get("b_query");
	hm.remove("b_query");

	String queryItem = (String) this.getFormHM().get("queryItem");
	if (b_query != null && "1".equals(b_query))
	{
	    a_code = "";
	    queryItem = "";
	}
	if (queryItem == null)
	    queryItem = "";
	this.getFormHM().put("queryItem", queryItem);

	ArrayList list = DataDictionary.getFieldList("Z04", Constant.USED_FIELD_SET);
	ArrayList fieldlist = new ArrayList();
	ArrayList queryItemList = new ArrayList();
	CommonData temp = new CommonData("", "");
	queryItemList.add(temp);

	ArrayList queryValueList = new ArrayList();

	FieldItem item = new FieldItem();
	item.setFieldsetid("Z04");
	item.setItemid("oper");
	item.setItemdesc(ResourceFactory.getProperty("column.operation"));
	item.setItemtype("A");
	item.setCodesetid("0");
	item.setAlign("center");
	item.setReadonly(true);
	fieldlist.add(item.cloneField());

	StringBuffer sql = new StringBuffer();
	sql.append("select '' as oper,");
	for (int i = 0; i < list.size(); i++)
	{
	    item = (FieldItem) list.get(i);
	    Field field = (Field) item.cloneField();
	    String itemid = item.getItemid();
	    String itemtype = item.getItemtype();
	    String itemdesc = item.getItemdesc();
	    String codesetid = item.getCodesetid();

	    // 据业务字典显示/隐藏列表字段
	    field.setVisible(item.isVisible());

	    // 订单编号 招聘需求号 需求部门 需求单位 职位 除外， 以及备注类型 除外
	    if (!"Z0400".equalsIgnoreCase(itemid) && !"Z0407".equalsIgnoreCase(itemid) && !"Z0405".equalsIgnoreCase(itemid) && !"Z0404".equalsIgnoreCase(itemid) && !"Z0403".equalsIgnoreCase(itemid)
		    && !"M".equalsIgnoreCase(itemtype))
	    {
		if (item.isVisible())
		{
		    temp = new CommonData(itemid + ":" + codesetid + ":" + itemtype, itemdesc);
		    queryItemList.add(temp);
		}
	    }// || itemid.equalsIgnoreCase("Z0410")
	    if ("Z0400".equalsIgnoreCase(itemid) || "Z0405".equalsIgnoreCase(itemid) || "Z0404".equalsIgnoreCase(itemid) || "Z0403".equalsIgnoreCase(itemid) || "Z0402".equalsIgnoreCase(itemid)
		    || "Z0409".equalsIgnoreCase(itemid) || "Z0412".equalsIgnoreCase(itemid) || "Z0414".equalsIgnoreCase(itemid) || "Z0416".equalsIgnoreCase(itemid))
		field.setReadonly(true);
	    if ("Z0406".equalsIgnoreCase(itemid))// 在结束之前只有完成日期可以改
		field.setReadonly(false);
	    // 招聘需求号 订单编号 不显示
	    if ("Z0400".equalsIgnoreCase(itemid) || "Z0407".equalsIgnoreCase(itemid))
		field.setVisible(false);

	    fieldlist.add(field);
	    sql.append(itemid + ",");
	}
	sql.setLength(sql.length() - 1);
	sql.append(" from Z04 where 1=1 and Z0407 in (select z0301 from z03 where z0319!='09') ");
	// 查询
	if (a_code != null && a_code.trim().length() > 1)
	{
	    String codesetid = a_code.substring(0, 2);
	    String value = a_code.substring(2);

	    if (value != null && value.trim().length() > 0)
	    {
		if ("UN".equalsIgnoreCase(codesetid))
		    sql.append(" and Z0404 like '" + value + "%'");
		else if ("UM".equalsIgnoreCase(codesetid))
		    sql.append(" and Z0405 like '" + value + "%'");
		else if ("@K".equalsIgnoreCase(codesetid))
		    sql.append(" and Z0403 like '" + value + "%'");
	    }
	}
	if (queryItem != null && queryItem.trim().length() > 1)
	{

	    String[] theItem = queryItem.split(":");
	    if ("A".equalsIgnoreCase(theItem[2]))
	    {
		if ("0".equalsIgnoreCase(theItem[1]) || theItem[1].trim().length() == 0)// 非代码类型支持模糊查询
		{
		    String queryValue = (String) this.getFormHM().get("queryValue");
		    if(queryValue.trim().length()>0)
			sql.append(" and " + theItem[0] + " like '%" + queryValue + "%'");
		} else if (!"0".equalsIgnoreCase(theItem[1]))
		{
		    String codeValue = (String) this.getFormHM().get("codeValue");
		    if (!"all".equalsIgnoreCase(codeValue))
			sql.append(" and " + theItem[0] + "='" + codeValue + "'");
		    this.getFormHM().put("codeValue2", codeValue);
		}
	    } else if ("D".equalsIgnoreCase(theItem[2]))
	    {
		String startDate = (String) this.getFormHM().get("startDate");
		String endDate = (String) this.getFormHM().get("endDate");

		if (startDate.length() > 0)
		{
		    StringBuffer buf = new StringBuffer();
		    buf.append(Sql_switcher.year(theItem[0]) + ">" + getDatePart(startDate, "y") + " or ");
		    buf.append("(" + Sql_switcher.year(theItem[0]) + "=" + getDatePart(startDate, "y") + " and ");
		    buf.append(Sql_switcher.month(theItem[0]) + ">" + getDatePart(startDate, "m") + ") or ");
		    buf.append("(" + Sql_switcher.year(theItem[0]) + "=" + getDatePart(startDate, "y") + " and ");
		    buf.append(Sql_switcher.month(theItem[0]) + "=" + getDatePart(startDate, "m") + " and ");
		    buf.append(Sql_switcher.day(theItem[0]) + ">=" + getDatePart(startDate, "d") + ")");
		    sql.append(" and (" + buf.toString() + ")");
		}
		if (endDate.length() > 0)
		{
		    StringBuffer buf = new StringBuffer();
		    buf.append(Sql_switcher.year(theItem[0]) + "<" + getDatePart(endDate, "y") + " or ");
		    buf.append("(" + Sql_switcher.year(theItem[0]) + "=" + getDatePart(endDate, "y") + " and ");
		    buf.append(Sql_switcher.month(theItem[0]) + "<" + getDatePart(endDate, "m") + ") or ");
		    buf.append("(" + Sql_switcher.year(theItem[0]) + "=" + getDatePart(endDate, "y") + " and ");
		    buf.append(Sql_switcher.month(theItem[0]) + "=" + getDatePart(endDate, "m") + " and ");
		    buf.append(Sql_switcher.day(theItem[0]) + "<=" + getDatePart(endDate, "d") + ")");
		    sql.append(" and (" + buf.toString() + ")");
		}
	    } else if ("N".equalsIgnoreCase(theItem[2]))
	    {
		String startNum = (String) this.getFormHM().get("startNum");
		String endNum = (String) this.getFormHM().get("endNum");
		if (startNum.length() > 0)
		    sql.append(" and " + theItem[0] + ">=" + startNum);
		if (endNum.length() > 0)
		    sql.append(" and " + theItem[0] + "<=" + endNum);
	    }
	}
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String privCode = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
	if (!this.userView.isSuper_admin())
	{
	    if (this.userView.haveTheRoleProperty("8"))// 招聘主管
	    {
		if (privCode != null && privCode.trim().length() > 0)
		{
		    String codesetid = privCode.substring(0, 2);
		    String value = privCode.substring(2);
		    if (value.length() > 0)
		    {
			if ("UN".equalsIgnoreCase(codesetid))
			{
			    sql.append(" and z0404 like '");
			    sql.append(value);
			    sql.append("%' ");
			} else if ("UM".equalsIgnoreCase(codesetid))
			{
			    sql.append(" and z0405 like '");
			    sql.append(value);
			    sql.append("%' ");
			} else if ("@K".equalsIgnoreCase(codesetid))
			{
			    sql.append(" and z0403 like '");
			    sql.append(value);
			    sql.append("%' ");
			}
		    }
		}
	    } else
	    // 用负责人字段和登录用户比
	    {
		// 判断登录用户为哪种类型的用户：用户管理的还是帐号分配里的
		int status = this.userView.getStatus();	
		String zh = "";// 负责人帐号的值
		try
		{
		 
		    if (status == 4)// 帐号分配里面的用户需要由【库前缀+a0100】和【帐号指标】取到对应的帐号
		    {
			// 先取到帐号对应的指标
			String zpFld = "";
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
			if (login_vo != null)
			{
			    String login_name = login_vo.getString("str_value");
			    int idx = login_name.indexOf(",");
			    if (idx != -1)
				zpFld = login_name.substring(0, idx);
			}
			if ("".equals(zpFld) || "#".equals(zpFld))
			    zpFld = "username";

			String sqlStr = "select " + zpFld + " from " + this.getUserView().getDbname() + "A01 where a0100='" + this.getUserView().getA0100() + "'";
			RowSet rs = dao.search(sqlStr);
			if (rs.next())
			    zh = rs.getString(1);
			
		    } else if (status == 0)// 用户管理里面的用户
			zh = this.getUserView().getUserName();
		    
		    
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		sql.append(" and (z0409='" + zh + "' or z0414='"+ zh + "')");

		// if(this.userView.getStatus()==0)//用户管理里面的用户
		// sql.append(" and
		// z0409='"+this.getUserView().getUserId()+"'");
		// else if(this.userView.getStatus()==4)//帐号分配里的用户
		// sql.append(" and
		// z0409='"+this.getUserView().getUserName()+"'");
	    }
	}
	//招聘主管和超级用户可以删除
	String delFlag="0";
	if(this.userView.isSuper_admin() || this.userView.haveTheRoleProperty("8"))
	    delFlag="1";
	this.getFormHM().put("delFlag", delFlag);
	this.getFormHM().put("sql", sql.toString());
	this.getFormHM().put("fieldlist", fieldlist);
	this.getFormHM().put("queryItemList", queryItemList);

    }

    public String getDatePart(String mydate, String datepart)
    {

	String str = "";
	if ("y".equalsIgnoreCase(datepart))
	    str = mydate.substring(0, 4);
	else if ("m".equalsIgnoreCase(datepart))
	{
	    if ("0".equals(mydate.substring(5, 6)))
		str = mydate.substring(6, 7);
	    else
		str = mydate.substring(5, 7);
	} else if ("d".equalsIgnoreCase(datepart))
	{
	    if ("0".equals(mydate.substring(8, 9)))
		str = mydate.substring(9, 10);
	    else
		str = mydate.substring(8, 10);
	}
	return str;
    }

}
