package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>
 * Title:ImportBonusDataTrans.java
 * </p>
 * <p>
 * Description:引入奖金数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-20 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ImportBonusDataTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String bonusItemFld = (String) this.getFormHM().get("isImportBonus");
	String salaryid = (String) this.getFormHM().get("salaryid");
	SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
	String tablename = gzbo.getGz_tablename();
	String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
	boolean isShare = true;
	boolean isGZmanager = false;
	if (manager == null || (manager != null && manager.length() == 0))// 不共享
	    isShare = false;
	if (this.userView.getUserName().equals(manager))
	    isGZmanager = true;

	StringBuffer sql = new StringBuffer();
	sql.append("select  A00Z0,nbase,a0100,min(A00Z1) cs from ");
	sql.append(tablename);
	sql.append(" where 1=1 ");

	if (isShare && !isGZmanager)// 共享的工资类别且非管理员有人员范围限制
	{
		
		String showUnitCodeTree=gzbo.getControlByUnitcode();
		if("1".equals(showUnitCodeTree)) ////是否按操作单位来控制
		{
			 
			String whl_str=gzbo.getWhlByUnits();
			if(whl_str.length()>0)
			{
				sql.append(whl_str); 
			}
		}
		else 
		{
		    String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
		    String codesetid = a_code.substring(0, 2);
		    String value = a_code.substring(2);
		    if ("UN".equalsIgnoreCase(codesetid))
		    {
			sql.append(" and (B0110 like '");
			sql.append(value);
			sql.append("%'");
			if ("".equalsIgnoreCase(value))
			    sql.append(" or B0110 is null");
			sql.append(")");
		    } else if ("UM".equalsIgnoreCase(codesetid))
		    {
			sql.append(" and E0122 like '");
			sql.append(value);
			sql.append("%'");
		    }
		}
	    
	    
	}
	sql.append(" group by A00Z0,nbase,a0100 ");
	ContentDAO dao = new ContentDAO(this.frameconn);

	try
	{
	    RowSet rs = dao.search(sql.toString());
	    ArrayList list = new ArrayList();
	    while (rs.next())
	    {
		java.sql.Date a00Z0 = rs.getDate("A00Z0");
		String nbase = rs.getString("nbase");
		String a0100 = rs.getString("a0100");
		String cs = rs.getString("cs");

		LazyDynaBean abean = new LazyDynaBean();
		abean.set("nbase", nbase);
		abean.set("a0100", a0100);
		abean.set("A00Z0", a00Z0);
		abean.set("A00Z1", cs);
		list.add(abean);
	    }
	    ConstantXml xml = new ConstantXml(this.frameconn, "GZ_PARAM", "Params");
	    String bonusSet = xml.getTextValue("/Params/Bonus/setid");
	    ArrayList fieldlist = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
	    String gzFlagFld = "";
	    String doStatusFld = "";
	    String jeFld = "";
	    String dateFld = "";
	    for (int j = 0; j < fieldlist.size(); j++)
	    {
		FieldItem fielditem = (FieldItem) fieldlist.get(j);
		if ("进工资标识".equals(fielditem.getItemdesc()))
		    gzFlagFld = fielditem.getItemid();
		if ("处理状态".equals(fielditem.getItemdesc()))
		    doStatusFld = fielditem.getItemid();
		if ("金额".equals(fielditem.getItemdesc()) || "奖金金额".equals(fielditem.getItemdesc()))
		    jeFld = fielditem.getItemid();
		if ("业务日期".equals(fielditem.getItemdesc()))
		    dateFld = fielditem.getItemid();

	    }

	    ArrayList updateList = new ArrayList();
	    String updateSql = "update " + tablename + " set " + bonusItemFld + "=? where a0100=? and A00Z0=? and nbase=? and A00Z1=? ";
	    for (int i = 0; i < list.size(); i++)
	    {
		LazyDynaBean abean = (LazyDynaBean) list.get(i);
		String nbase = (String) abean.get("nbase");
		String a0100 = (String) abean.get("a0100");
		java.sql.Date a00Z0 = (java.sql.Date) abean.get("A00Z0");
		String a00Z1 = (String) abean.get("A00Z1");
		double sumMoney = 0.0;
		String busiDate = DateStyle.dateformat(a00Z0, "yyyy-MM-dd");
		StringBuffer sqlStr = new StringBuffer();
		// 把对应人员本期奖金信息表中的“进工资标识”为是的合计导入奖金项目指标
		sqlStr.append("select sum(" + jeFld + ") from " + nbase + bonusSet);
		sqlStr.append(" where " + Sql_switcher.year(dateFld) + "=" + getDatePart(busiDate, "y"));
		sqlStr.append(" and " + Sql_switcher.month(dateFld) + "=" + getDatePart(busiDate, "m"));
		sqlStr.append(" and a0100='" + a0100 + "' and " + gzFlagFld + "=1");

		rs = dao.search(sqlStr.toString());
		if (rs.next())
		{
		    if (rs.getObject(1) != null)
		    {
			sumMoney = rs.getDouble(1);
			// 同时把本期奖金记录设置为已用
			sqlStr.setLength(0);
			sqlStr.append(" update " + nbase + bonusSet);
			sqlStr.append(" set " + doStatusFld + "=2");
			sqlStr.append(" where " + Sql_switcher.year(dateFld) + "=" + getDatePart(busiDate, "y"));
			sqlStr.append(" and " + Sql_switcher.month(dateFld) + "=" + getDatePart(busiDate, "m"));
			sqlStr.append(" and a0100='" + a0100 + "' and " + gzFlagFld + "=1");

			dao.update(sqlStr.toString());
		    }
		}

		ArrayList dataList = new ArrayList();
		dataList.add(new Double(sumMoney));
		dataList.add(a0100);
		dataList.add(a00Z0);
		dataList.add(nbase);
		dataList.add(a00Z1);
		updateList.add(dataList);
	    }

	    dao.batchUpdate(updateSql, updateList);

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
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
