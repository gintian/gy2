package com.hjsj.hrms.transaction.org.orgdata;

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
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AddOrgSubSet.java
 * </p>
 * <p>
 * Description:机构相关子集的新增/编辑操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddOrgSubSet extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String priFldVal = (String) hm.get("itemid");
	priFldVal = priFldVal != null ? priFldVal : "";
	hm.remove("itemid");
	this.getFormHM().put("itemid", priFldVal);

//	curri9999 用于插入方式新增记录 标志在curri9999标志的记录前面插入记录
	String curri9999 = (String) hm.get("curri9999");	
	hm.remove("curri9999");
	this.getFormHM().put("curri9999",curri9999);
	
	String i9999 = (String) hm.get("i9999");
	i9999 = i9999 != null ? i9999 : "";
	hm.remove("i9999");
	this.getFormHM().put("i9999", i9999);

	String subset = (String) hm.get("subset");
	this.getFormHM().put("subset", subset != null ? subset : "");
	hm.remove("subset");
	
	String infor = (String) hm.get("infor");
	this.getFormHM().put("infor", infor != null ? infor : "");
	hm.remove("infor");
	String mainitem="";
	if("2".equals(infor)){
		mainitem = "B0110";
	}else if("3".equals(infor)){
		mainitem = "E01A1";

	}else{
		mainitem = "B0110";
	}
	
	ArrayList fieldList = DataDictionary.getFieldList(subset, Constant.USED_FIELD_SET);
	ArrayList fieldInfoList = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());

//	if(curri9999!=null)//插入方式新增记录
//	{
//	    GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
//	    i9999 = gzbo.insertSubSet(subset,mainitem,priFldVal,curri9999);
//	}
	ArrayList readOnlyFlds = new ArrayList();
	
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

		if("0".equals(this.userView.analyseFieldPriv(itemid)))
		    continue;
		
		if(fieldItem.getDisplaywidth()==0)
		    continue;
		
		String pri = this.userView.analyseFieldPriv(fieldItem.getItemid());
		if ("1".equals(pri))// 只读
		{
		    LazyDynaBean abean = new LazyDynaBean();
		    abean.set("itemid", fieldItem.getItemid());
		    abean.set("codesetid", fieldItem.getCodesetid());
		    readOnlyFlds.add(abean);
		}
		    
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
		fieldItemView.setFillable(fieldItem.isFillable());
		if ("0".equals(i9999))// 新建
		{
		    fieldItemView.setViewvalue("");
		    fieldItemView.setValue("");
		} else
		// 修改
		{
		    StringBuffer strsql = new StringBuffer();
		    strsql.append("select " + itemid + " from ");
		    strsql.append(subset + " where ");
		    strsql.append(mainitem+"='");
		    strsql.append(priFldVal + "' and i9999=");
		    strsql.append(i9999);

		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
		    {
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
	    this.getFormHM().put("i9999", i9999);
	    this.getFormHM().put("readOnlyFlds2",readOnlyFlds);
	}

    }

}
