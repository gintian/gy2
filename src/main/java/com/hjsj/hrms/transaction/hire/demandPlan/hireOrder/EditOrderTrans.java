package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SaveOrderTrans.java</p>
 * <p>Description:查询订单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-13 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class EditOrderTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String orderid = (String) hm.get("orderid");
	hm.remove("orderid");
	this.getFormHM().put("orderid", orderid);
	
	ArrayList list = DataDictionary.getFieldList("Z04", Constant.USED_FIELD_SET);
	ArrayList fieldInfoList = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String sql = "select * from z04 where Z0400='"+orderid+"'";
	try
	{
	    this.frowset = dao.search(sql);
	    if (this.frowset.next())
	    {
		for (int i = 0; i < list.size(); i++)
		{
		    FieldItem fieldItem = (FieldItem) list.get(i);
		    String itemid = fieldItem.getItemid();
		    String itemName = fieldItem.getItemdesc();
		    String itemType = fieldItem.getItemtype();
		    String codesetId = fieldItem.getCodesetid();

		    //招聘需求号 订单编号 不显示
		    if("Z0400".equalsIgnoreCase(itemid) || "Z0407".equalsIgnoreCase(itemid))
			continue;
		    
		    if(!fieldItem.isVisible())
			continue;
		    
		    if("Z0412".equalsIgnoreCase(itemid) || "Z0409".equalsIgnoreCase(itemid) || "Z0416".equalsIgnoreCase(itemid) || "Z0414".equalsIgnoreCase(itemid) || "Z0400".equalsIgnoreCase(itemid))
			fieldItem.setReadonly(true);
		    
		    
		    FieldItemView fieldItemView = new FieldItemView();
		    fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
		    fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
		    fieldItemView.setCodesetid(codesetId);
		    fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
		    fieldItemView.setDisplayid(fieldItem.getDisplayid());
		    fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
		    fieldItemView.setExplain(fieldItem.getExplain());
		    fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
		    fieldItemView.setItemdesc(itemName);
		    fieldItemView.setItemid(itemid);
		    fieldItemView.setItemlength(fieldItem.getItemlength());
		    fieldItemView.setItemtype(itemType);
		    fieldItemView.setModuleflag(fieldItem.getModuleflag());
		    fieldItemView.setState(fieldItem.getState());
		    fieldItemView.setUseflag(fieldItem.getUseflag());
		    fieldItemView.setPriv_status(fieldItem.getPriv_status());
		    fieldItemView.setRowflag(String.valueOf(list.size() - 1)); // 在struts用来表示换行的变量
		    fieldItemView.setReadonly(fieldItem.isReadonly());

		    Object val = null;
		    if ("D".equalsIgnoreCase(itemType) && Sql_switcher.searchDbServer() == Constant.ORACEL)// 日期型
			// oracle数据库必须这样取数据
			val = this.getFrowset().getDate(itemid);
		    else
			val = this.frowset.getString(itemid);

		    if (val == null)
		    {
			fieldItemView.setViewvalue("");
			fieldItemView.setValue("");
		    } else
		    {
			if ("A".equals(itemType) || "M".equals(itemType))
			{
			    String value = (String) val;
			    if (!"0".equals(codesetId) && codesetId.length()>0)
			    {
				String codevalue = value;
				if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
				    fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
				else
				    fieldItemView.setViewvalue("");
				fieldItemView.setValue(value != null ? value.toString() : "");
				 if("z0410".equalsIgnoreCase(itemid) && "1".equals(codevalue))
				     this.getFormHM().put("endFlag", "1");
				 else if("z0410".equalsIgnoreCase(itemid) && !"1".equals(codevalue))
				     this.getFormHM().put("endFlag", "0");
			    } else
			    {
				fieldItemView.setViewvalue(value);
				fieldItemView.setValue(value);
			    }
			} else if ("D".equals(itemType)) // 日期型有待格式化处理
			{
			    if (Sql_switcher.searchDbServer() == Constant.MSSQL)
			    {
				String value = (String) val;

				if (value != null && value.length() >= 10 && fieldItem.getItemlength() == 10)
				{
				    value = new FormatValue().format(fieldItem, value.substring(0, 10));
				    value = PubFunc.replace(value, ".", "-");
				    fieldItemView.setViewvalue(value);
				    fieldItemView.setValue(value);
				} else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 4)
				{
				    value = new FormatValue().format(fieldItem, value.substring(0, 4));
				    value = PubFunc.replace(value, ".", "-");
				    fieldItemView.setViewvalue(value);
				    fieldItemView.setValue(value);
				} else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 7)
				{
				    value = new FormatValue().format(fieldItem, value.substring(0, 7));
				    value = PubFunc.replace(value, ".", "-");
				    fieldItemView.setViewvalue(value);
				    fieldItemView.setValue(value);
				} else
				{
				    fieldItemView.setViewvalue("");
				    fieldItemView.setValue("");
				}
			    } else if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			    {
				Date dateVal = (Date) val;
				fieldItemView.setViewvalue(dateVal.toString());
				fieldItemView.setValue(dateVal.toString());
			    }

			} else
			// 数值类型的有待格式化处理
			{
			    String value = (String) val;
			    fieldItemView.setValue(PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth()));
			}
		    }			
		    fieldInfoList.add(fieldItemView);
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally
	{
	    this.getFormHM().put("fieldslist", fieldInfoList);
	}
	
    }
}
