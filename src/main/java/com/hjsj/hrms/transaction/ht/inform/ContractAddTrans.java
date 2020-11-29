package com.hjsj.hrms.transaction.ht.inform;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ContractAddTrans.java
 * </p>
 * <p>
 * Description:合同相关子集的新增/编辑操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-16 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ContractAddTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	String a0100 = (String)hm.get("a0100");
	a0100=a0100!=null?a0100:"";
	hm.remove("a0100");
	
	String db = (String)hm.get("dbname");
	db=db!=null?db:"";
	hm.remove("dbname");
	
	String htFlag = (String)hm.get("htFlag");
	htFlag=htFlag!=null?htFlag:"";// 合同标识
	hm.remove("htFlag");
	
	String i9999 = (String)hm.get("i9999");
	this.getFormHM().put("i9999", i9999);
	hm.remove("i9999");
	
	this.getFormHM().put("a0100", a0100);
	this.getFormHM().put("dbname", db);

	String subSet = (String)hm.get("subset");
	this.getFormHM().put("itemtable", subSet!=null?subSet:"");	
	subSet=subSet!=null?subSet.substring(3):"";
	hm.remove("subset");
	
	
	ContractBo bo = new ContractBo(this.frameconn, this.userView);
	HashMap empinfo = bo.searchEmpInfo(a0100, db);  
	this.getFormHM().put("mainFlds", empinfo);

	ConstantXml xml = new ConstantXml(this.frameconn, "HT_PARAM", "Params");
	// 合同标识代码类
	String httype = xml.getTextValue("/Params/httype");
	httype = httype == null ? "" : httype;  
	String initFld = bo.getRelFld(subSet, httype);

//	ArrayList fieldList = DataDictionary.getFieldList(subSet, Constant.USED_FIELD_SET);
	ArrayList fieldList = this.userView.getPrivFieldList(subSet);
	ArrayList fieldInfoList = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	try
	{
	    for (int i = 0; i < fieldList.size(); i++)// 循环字段
	    {
		FieldItem fieldItem = (FieldItem) fieldList.get(i);
		String itemid = fieldItem.getItemid();
		String itemName = fieldItem.getItemdesc();
		String itemType = fieldItem.getItemtype();
		String codesetId = fieldItem.getCodesetid();
		if ("i9999".equalsIgnoreCase(itemid))
		    continue;

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
		fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量

		if ("0".equals(i9999))// 新建
		{
		    if (initFld.equalsIgnoreCase(itemid))// 初始化合同标识相关的字段
		    {
			fieldItemView.setViewvalue(AdminCode.getCode(codesetId, htFlag) != null ? AdminCode.getCode(codesetId, htFlag).getCodename() : "");
			fieldItemView.setValue(htFlag);
		    } else
		    {
			fieldItemView.setViewvalue("");
			fieldItemView.setValue("");
		    }
		} else
		// 修改
		{
		    StringBuffer strsql = new StringBuffer();
		    strsql.append("select " + itemid + " from ");
		    strsql.append(db + subSet + " where a0100='");
		    strsql.append(a0100 + "' and i9999=");
		    strsql.append(i9999);

		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
		    {
			  Object val = null;
			  if ("D".equalsIgnoreCase(itemType) && Sql_switcher.searchDbServer() == Constant.ORACEL)// 日期型 oracle数据库必须这样取数据
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
				if (!"0".equals(codesetId))
				{
				    String codevalue = value;
				    if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
					fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
				    else
					fieldItemView.setViewvalue("");
				    fieldItemView.setValue(value != null ? value.toString() : "");
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
		    }

		}
		fieldInfoList.add(fieldItemView);

	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally
	{
	    this.getFormHM().put("subFlds", fieldInfoList);
	}
    }
}
