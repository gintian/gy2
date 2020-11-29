package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
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
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AddEmpMainTrans.java
 * </p>
 * <p>
 * Description:新增/编辑人员主集
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-12-24 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddEmpMainTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String dbname = (String) hm.get("dbname");
	String fieldsetid = dbname + "A01";
	this.getFormHM().put("dbname", dbname);
	String a0100 = (String) hm.get("a0100");
	this.getFormHM().put("a0100", a0100);
	ArrayList fieldList = this.userView.getPrivFieldList("A01", Constant.USED_FIELD_SET);
	HashMap map = new HashMap();
	ArrayList readOnlyFlds = new ArrayList();

	Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
	String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name"); // 身份证指标
	chk = chk != null ? chk : "";
	String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name"); // 验证唯一性指标
	onlyname = onlyname != null ? onlyname : "";
	String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "valid");// 身份证验证是否启用
	chkvalid = chkvalid != null ? chkvalid : "";
	String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");// 唯一性验证是否启用
	uniquenessvalid = uniquenessvalid != null ? uniquenessvalid : "";
	String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "db");// 验证身份证适用的人员库
	dbchk = dbchk != null ? dbchk : "";
	String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "db");// 验证唯一性适用的人员库
	dbonly = dbonly != null ? dbonly : "";
	String isTestBirthday = "0";
	if ("1".equals(chkvalid))
	    if (dbchk.trim().length() > 2 && dbchk.toUpperCase().indexOf(dbname.toUpperCase()) != -1)
		isTestBirthday = "1";
	this.getFormHM().put("isTestBirthday", isTestBirthday);//保存时候进行出生日期是否和身份证一致的验证

	for (int i = 0; i < fieldList.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldList.get(i);
	    String pri = this.userView.analyseFieldPriv(fieldItem.getItemid());
	    if ("1".equals(pri))// 只读
	    {
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("itemid", fieldItem.getItemid());
		abean.set("codesetid", fieldItem.getCodesetid());
		readOnlyFlds.add(abean);
	    }
	    if ("0".equals(pri))// 没有权限
		continue;
	    String itemid = fieldItem.getItemid();
	    map.put(itemid, fieldItem);
	}
	this.getFormHM().put("readOnlyFlds", readOnlyFlds);
	HashMap hiddenFields = this.getHideFields();

	FieldSet fieldset = DataDictionary.getFieldSetVo("A01");
	GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
	ArrayList list = gzbo.itemList1(fieldset);

	ArrayList fieldInfoList = new ArrayList();
	String b0110 = "";
	String e0122 = "";
	try
	{
	    // this.getFormHM().put("pcode", a0100);

	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    StringBuffer strsql = new StringBuffer();
	    strsql.append("select * from ");
	    strsql.append(fieldsetid + " where a0100='");
	    strsql.append(a0100 + "'");
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

		    if (hiddenFields.get(itemid1.toLowerCase()) != null)
			continue;

		    String itemid = fieldItem.getItemid();
		    String itemName = fieldItem.getItemdesc();
		    String itemType = fieldItem.getItemtype();
		    String codesetId = fieldItem.getCodesetid();

		    // System.out.println(itemid+"--"+itemName+"--"+itemType+"--"+codesetId);

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
				fieldItemView.setValue(value != null ? value : "");

				if ("b0110".equalsIgnoreCase(itemid))
				{
				    b0110 = value;
				    String temp="";//liweichao
					if(!userView.isSuper_admin()){
						if(userView.getStatus()==4)
							temp=this.getUserView().getManagePrivCodeValue();
						else{
							String codeall = userView.getUnit_id();
							if(codeall!=null&&codeall.length()>2)
								temp=codeall.split("`")[0].substring(2);
							else if("".equals(temp))
								temp=this.getUserView().getManagePrivCodeValue();
						}
					}else
						temp=this.getUserView().getManagePrivCodeValue();
					this.getFormHM().put("orgparentcode",temp);
//				    this.getFormHM().put("orgparentcode", userView.getManagePrivCodeValue());
				}
				if ("e0122".equalsIgnoreCase(itemid))
				{
				    e0122 = value;
				    if (b0110.length() > 0)
					this.getFormHM().put("deptparentcode", b0110);
				    else
					this.getFormHM().put("deptparentcode", userView.getManagePrivCodeValue());
				}
				if ("e01a1".equalsIgnoreCase(itemid))
				{
				    if (e0122.length() > 0)
					this.getFormHM().put("posparentcode", e0122);
				    else
					this.getFormHM().put("posparentcode", userView.getManagePrivCodeValue());
				}
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

    /** 提取被隐藏字段 */
    public HashMap getHideFields() throws GeneralException
    {

	HashMap map = new HashMap();
	String strSql = "select itemid from fielditem where fieldsetid='A01' and displaywidth=0";
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
