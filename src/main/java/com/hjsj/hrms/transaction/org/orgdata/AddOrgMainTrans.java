package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:AddOrgMainTrans.java</p>
 * <p>Description:编辑单位主集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009.02.19</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddOrgMainTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String itemVal = (String) hm.get("itemVal");
	String infor = (String) hm.get("infor");
	
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
	
	this.getFormHM().put("itemVal", itemVal);
	this.getFormHM().put("infor", infor);
	ArrayList readOnlyFlds = new ArrayList();
	ArrayList fieldList = DataDictionary.getFieldList(tableName, Constant.USED_FIELD_SET);
	HashMap map = new HashMap();
	for (int i = 0; i < fieldList.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldList.get(i);
	    String itemid = fieldItem.getItemid();
	    
	    String pri = this.userView.analyseFieldPriv(fieldItem.getItemid());
	    if("1".equals(pri))//只读
	    {
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("itemid", fieldItem.getItemid());
		abean.set("codesetid", fieldItem.getCodesetid());
		readOnlyFlds.add(abean);
	    }
	    if("0".equals(pri))//没有权限
		continue;
	    map.put(itemid, fieldItem);	    
	}
	this.getFormHM().put("readOnlyFlds", readOnlyFlds);
	
	OrgDataBo orgbo = new OrgDataBo(this.frameconn,this.userView);
	FieldSet fieldset=DataDictionary.getFieldSetVo(tableName);
	ArrayList list = orgbo.itemList(fieldset,infor);
	
	HashMap hiddenFields = this.getHideFields(tableName);
	ArrayList fieldInfoList = new ArrayList();
	try
	{
	    // this.getFormHM().put("pcode", a0100);

	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    StringBuffer strsql = new StringBuffer();
	    strsql.append("select * from  ");
	    strsql.append(tableName);
	    strsql.append(" where ");
	    strsql.append(priFld+"='");
	    strsql.append(itemVal + "'");
	    this.frowset = dao.search(strsql.toString());
	    if (this.frowset.next())
	    {
		for (int i = 0; i < list.size(); i++)// 循环字段
		{
		    Field field = (Field) list.get(i);
		    String itemid1 = field.getName();
		    FieldItem fieldItem = (FieldItem) map.get(itemid1);
		    if (fieldItem == null)
			continue;

		    if(hiddenFields.get(itemid1.toLowerCase())!=null)
			continue;
		    
		    String itemid = fieldItem.getItemid();
		    String itemName = fieldItem.getItemdesc();
		    String itemType = fieldItem.getItemtype();
		    String codesetId = fieldItem.getCodesetid();		    

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
		    if ("1".equals(this.userView.analyseFieldPriv(itemid)))
			fieldItemView.setReadonly(true);
		    if(itemid.equalsIgnoreCase(priFld))
			fieldItemView.setReadonly(true);
		    
		    String value = this.frowset.getString(itemid);
		    if (value == null)
		    {
			fieldItemView.setViewvalue("");
			fieldItemView.setValue("");
		    } else
		    {
			if ("A".equals(itemType) || "M".equals(itemType))
			{
			    if (!"0".equals(codesetId))
			    {
				if ("b0110".equalsIgnoreCase(itemid))
				{
				    if(AdminCode.getCode("UN", value) != null)
					codesetId="UN";
				    else if(AdminCode.getCode("UM", value) != null)
					codesetId="UM";
				this.getFormHM().put("orgType", codesetId);
				}				
				
				String codevalue = value;
				if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
				    fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
				else
				    fieldItemView.setViewvalue("");
				fieldItemView.setValue(value != null ? value : "");
				
			    } else
			    {
				fieldItemView.setViewvalue(value);
				fieldItemView.setValue(value);
			    }
			} else if ("D".equals(itemType)) // 日期型有待格式化处理
			{
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
			} else
			    // 数值类型的有待格式化处理
			    fieldItemView.setValue(PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth()));
		    }
		    fieldInfoList.add(fieldItemView);
		}
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally
	{
	    this.getFormHM().put("fieldslist", fieldInfoList);
	}
    }

    /**
         * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位 节点。
         * 
         * @param codevalue
         * @return
         */
    private String getParentCodeValue(String codevalue) throws GeneralException
    {

	String value = "";
	StringBuffer buf = new StringBuffer();
	buf.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
	ArrayList paralist = new ArrayList();
	paralist.add(codevalue);
	try
	{
	    ContentDAO dao = new ContentDAO(this.getFrameconn());

	    RowSet rset = dao.search(buf.toString(), paralist);
	    if (rset.next())
	    {
		String codeid = rset.getString("codesetid");
		String parentid = rset.getString("parentid");
		if (!"UN".equalsIgnoreCase(codeid))
		    value = getParentCodeValue(parentid);
		else
		    value = rset.getString("codeitemid");
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
	return value;
    }
	
    /**提取被隐藏字段*/
    public HashMap getHideFields(String tableName) throws GeneralException
    {
	HashMap map = new HashMap();
	String strSql="select itemid from fielditem where fieldsetid='"+tableName+"' and displaywidth=0";
	try
	{
	    ContentDAO dao = new ContentDAO(this.getFrameconn());

	    RowSet rset = dao.search(strSql);
	    while (rset.next())
	    {
		String itemid = rset.getString("itemid");
		map.put(itemid.toLowerCase(), itemid);
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
	return map;
    }	
    
}
