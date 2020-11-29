package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 * Title:BatchUpdateTrans.java
 * </p>
 * <p>
 * Description:更新奖金数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-14 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class UpdateBonusDataTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String sql = (String) this.getFormHM().get("sql");
	sql = SafeCode.decode(sql);

	String doStatusFld = (String)this.getFormHM().get("doStatusFld");
	
	String updatevalue = (String) this.getFormHM().get("updatevalue");
	updatevalue = updatevalue != null && updatevalue.trim().length() > 0 ? updatevalue : "";

	String flagcheck = (String) this.getFormHM().get("flagcheck");
	flagcheck = flagcheck != null && flagcheck.trim().length() > 0 ? flagcheck : "1";

	String itemid = (String) this.getFormHM().get("itemid");
	itemid = itemid != null && itemid.trim().length() > 0 ? itemid : "";

	String bonuset = "";
	String[] arr = itemid.split(":");
	StringBuffer sqlstr = new StringBuffer();
	if (arr.length == 3)
	{
	    FieldItem field = DataDictionary.getFieldItem(arr[0]);
	    bonuset = field.getFieldsetid();
	  
	    sqlstr.append(arr[0].toUpperCase());
	    sqlstr.append("=");
	    if ("3".equals(flagcheck))// 参考项的情况
	    {
		sqlstr.append(updatevalue.toUpperCase());
	    } else if ("1".equals(flagcheck))// 数值类型增加值
	    {
		if ("N".equalsIgnoreCase(field.getItemtype()))
		{
		    if (field.getDecimalwidth() > 0)
		    {
			updatevalue = updatevalue != null && updatevalue.trim().length() > 0 ? updatevalue : "0";
		    } else
		    {
			updatevalue = updatevalue != null && updatevalue.trim().length() > 0 ? updatevalue : "0";
			if (updatevalue.trim().length() > 0 && updatevalue.indexOf(".") != -1)
			    updatevalue = updatevalue.substring(0, updatevalue.indexOf("."));
		    }
		    sqlstr.append(arr[0].toUpperCase() + "+" + updatevalue);
		}
	    } else if ("0".equals(flagcheck))// 数值类型增加值减少值
	    {
		if ("N".equalsIgnoreCase(field.getItemtype()))
		{
		    if (field.getDecimalwidth() > 0)
		    {
			updatevalue = updatevalue != null && updatevalue.trim().length() > 0 ? updatevalue : "0";
		    } else
		    {
			updatevalue = updatevalue != null && updatevalue.trim().length() > 0 ? updatevalue : "0";
			if (updatevalue.trim().length() > 0 && updatevalue.indexOf(".") != -1)
			    updatevalue = updatevalue.substring(0, updatevalue.indexOf("."));
		    }
		    sqlstr.append(arr[0].toUpperCase() + "-" + updatevalue);
		}
	    } else if ("2".equals(flagcheck))
	    {
		sqlstr.append("?");
	    }
	}
	sql = sql.substring(0, sql.indexOf("order"));
	try
	{
	    ArrayList dbList = new ArrayList();
	    String sql1 = "select distinct dbase from (" + sql + ") b";
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    RowSet rset = dao.search(sql1);
	    while (rset.next())
	    {
		String dbpri = rset.getString("dbase");
		dbList.add(dbpri);
	    }

	    for (int i = 0; i < dbList.size(); i++)
	    {
		String dbpri = (String) dbList.get(i);
		String sql2 = sql + " and a.dbase='" + dbpri + "'";
		rset = dao.search(sql2);
		ArrayList updateList = new ArrayList();
		while (rset.next())
		{
		    ArrayList list = new ArrayList();
		    if ("2".equals(flagcheck))
		    { 
			FieldItem field = DataDictionary.getFieldItem(arr[0]);
			if ("D".equalsIgnoreCase(field.getItemtype()))
			{
			    if (updatevalue != null && updatevalue.trim().length() > 0)
			    {
				Date date = DateUtils.getSqlDate(updatevalue, "yyyy-MM-dd");
				list.add(date);
			    } else
			    {
				list.add(null);
			    }
			} else
			{
			    list.add(updatevalue);
			}
		    }
		 
		    String a0100 = rset.getString("a0100");
		    String i9999 = rset.getString("i9999");
		    list.add(a0100);
		    list.add(new Integer(i9999));
		    updateList.add(list);
		}
		
		String updateSql = "update "+dbpri+bonuset+" set "+sqlstr.toString()+" where a0100=? and i9999=? and "+doStatusFld+"!='2'";
		dao.batchUpdate(updateSql, updateList);
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
	this.getFormHM().put("check", "ok");
	
    }

}
