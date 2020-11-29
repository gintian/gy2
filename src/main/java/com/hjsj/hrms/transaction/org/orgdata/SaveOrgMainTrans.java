package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SaveOrgMainTrans.java
 * </p>
 * <p>
 * Description:保存单位主集
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-02-19 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveOrgMainTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String primaryValue = (String) this.getFormHM().get("itemVal");	
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

	ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldslist");

	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String newVal = "";
	boolean flag=false;
	try
	{
	    RecordVo vo = new RecordVo(tableName);
	    vo.setString(priFld, primaryValue);
	    vo = dao.findByPrimaryKey(vo);

	    for (int i = 0; i < fieldlist.size(); i++)
	    {
		FieldItem fieldItem = (FieldItem) fieldlist.get(i);

		String itemid = fieldItem.getItemid();
		String value = fieldItem.getValue();
//
//		if (value.equals(""))
//		    continue;

		if (itemid.equals(priFld))
		{
		    if(!primaryValue.equalsIgnoreCase(value))
			flag=true;
		    newVal=value;
		    continue;
		}
		  
		if ("D".equals(fieldItem.getItemtype()))
		{
		    vo.setDate(itemid, value);
		} else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
		{
		    value = PubFunc.round(value, fieldItem.getDecimalwidth());
		    vo.setString(itemid, value);
		} else
		    vo.setString(itemid, value);
	    }

	    dao.updateValueObject(vo);
	    
	    if(flag){
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(tableName);
		buf.append(" set ");
		buf.append(priFld);
		buf.append("='");
		buf.append(newVal);
		buf.append("' where ");
		buf.append(priFld);
		buf.append("='");
		buf.append(primaryValue);
		buf.append("'");
		

	    dao.update(buf.toString());}
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

}
