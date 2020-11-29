package com.hjsj.hrms.transaction.org.orgdata;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * create time:2009-02-20 13:00:00
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
	
	String itemVal = (String) this.getFormHM().get("itemVal");
	String infor = (String) this.getFormHM().get("infor");
	
	String tableName = "B01";
	String priFld = "b0110";
	if("2".equals(infor))
	{
	    tableName="B01";
	    priFld = "b0110";
	}	   
	else if("3".equals(infor))
	{
	    tableName="K01";
	    priFld = "e01a1";
	}
	
	this.getFormHM().put("setname", fieldsetid);
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	
	if (fieldsetid.indexOf(tableName) == -1)// 子集
	{
	    String i9999 = (String) this.getFormHM().get("I9999");
	    try
	    {		
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from ");
		strsql.append(fieldsetid + " where "+priFld+"='");
		strsql.append(itemVal + "' and i9999="+i9999);
		this.frowset = dao.search(strsql.toString());
		if (this.frowset.next())
		{  
		    String item = "";
		    ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
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
				Date date = this.frowset.getDate(itemid);
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
		strsql.append(fieldsetid + " where "+priFld+"='");
		strsql.append(itemVal + "'");
		this.frowset = dao.search(strsql.toString());
		if (this.frowset.next())
		{
		    String item = "";
		    ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
		    for (int i = 0; i < fieldlist.size(); i++)
		    {
			FieldItem fielditem = (FieldItem) fieldlist.get(i);
			String itemid = fielditem.getItemid();			
			String temp = this.frowset.getString(itemid);
			String itemType =fielditem.getItemtype(); 
			
			if(temp!=null)
			{
			    if("D".equalsIgnoreCase(itemType))
			    {
				Date date = this.frowset.getDate(itemid);				
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				temp = df.format(date);
			    }
			    item += itemid + ",";
			    this.getFormHM().put(itemid, SafeCode.encode(temp));
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
