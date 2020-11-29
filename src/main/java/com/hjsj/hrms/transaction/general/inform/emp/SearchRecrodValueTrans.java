package com.hjsj.hrms.transaction.general.inform.emp;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * <p>
 * Title:SearchRecrodValueTrans.java
 * </p>
 * <p>
 * Description:保存主集后刷新列表页面，包括主集和子集
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-12-29 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchRecrodValueTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String fieldsetid = (String) this.getFormHM().get("fieldset");
	String dbname = (String) this.getFormHM().get("dbname");
	String a0100 = (String) this.getFormHM().get("a0100");
	this.getFormHM().put("setname", fieldsetid);
	this.getFormHM().put("dbname", dbname);
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	
	if (fieldsetid.indexOf("A01") == -1)// 子集
	{
		String i9999 = (String) this.getFormHM().get("I9999");
		try
		{		
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from ");
			strsql.append(fieldsetid + " where a0100='");
			strsql.append(a0100 + "' and i9999="+i9999);
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next())
			{  
				String item = "";
				ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid.substring(3), Constant.USED_FIELD_SET);
				for (int i = 0; i < fieldlist.size(); i++)
				{
					FieldItem fielditem = (FieldItem) fieldlist.get(i);
					String itemType =fielditem.getItemtype(); 
					String itemid = fielditem.getItemid();			
					String temp = this.frowset.getString(itemid);			
					if(temp!=null)
					{
						if("D".equalsIgnoreCase(itemType))
						{
							java.sql.Date date = this.frowset.getDate(itemid);
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
							temp = df.format(date);
						}
						item += itemid + ",";
						this.getFormHM().put(itemid, temp);
					}
				}
				this.getFormHM().put("fielditem", item);
			}
		} catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	} else
	{
		try
		{		
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from ");
			strsql.append(fieldsetid + " where a0100='");
			strsql.append(a0100 + "'");
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next())
			{
				String item = "";
				ArrayList fieldlist = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
				String b0110 = this.frowset.getString("b0110");
				String e0122 = this.frowset.getString("e0122");
				String e01a1 = this.frowset.getString("e01a1");
				
				if(b0110!=null)
				{
				    item += "b0110,";
				    this.getFormHM().put("b0110", b0110);
				}
				if(e0122!=null)
				{
				    item += "e0122,";
				    this.getFormHM().put("e0122", e0122);
				}
				if(e01a1!=null)
				{
				    item += "e01a1,";
				    this.getFormHM().put("e01a1", e01a1);
				}
				for (int i = 0; i < fieldlist.size(); i++)
				{
					FieldItem fielditem = (FieldItem) fieldlist.get(i);
					String itemid = fielditem.getItemid();					
					String itemType =fielditem.getItemtype(); 				
					if("b0110".equalsIgnoreCase(itemid) || "e0122".equalsIgnoreCase(itemid) || "e01a1".equalsIgnoreCase(itemid))
						continue;
					Object val = null;
					if("D".equalsIgnoreCase(itemType))
					    val = this.frowset.getDate(itemid);
					else
					    val = this.frowset.getString(itemid);
					if(val!=null)
					{	String temp="";
						if("D".equalsIgnoreCase(itemType))
						{
							java.sql.Date date = (java.sql.Date)val;
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
							temp = df.format(date);
						}
						else
						    temp=(String)val;
						item += itemid + ",";
						this.getFormHM().put(itemid, temp);
					}
				}
				this.getFormHM().put("fielditem", item);
			}
		} catch (Exception e)
		{
		e.printStackTrace();
	    }
	}
    }

}
