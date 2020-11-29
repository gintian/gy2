package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchBonusTrans.java
 * </p>
 * <p>
 * Description:查询奖金数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-06 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchBonusTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String a_code = (String) hm.get("a_code");
	a_code = a_code==null?"":a_code;
	hm.remove("a_code");

	String b_query = (String) hm.get("b_query");
	hm.remove("b_query");
	
//	查询条件
	String expr = (String) this.getFormHM().get("expr");
	String factor = (String) this.getFormHM().get("factor");
	String businessDate = (String) this.getFormHM().get("businessDate");
	businessDate = businessDate == null ? "" : businessDate;
	
	// b_query:=0 是首次进入奖金管理模块 =1 点击组织结构的根节点 =add 保存新增返回列表页面
	//查询条件会一直保存在session里面	 
	if (b_query != null && "add".equals(b_query))//保存新增返回的记录只是和选中的组织结构树有关和查询结果无关
	{
	    a_code = (String) this.getFormHM().get("a_code");
	    expr = "";//新增就把查询条件去掉
	    factor="";
	    this.getFormHM().put("expr", expr);
	    this.getFormHM().put("factor", factor);
	}else if (b_query != null && "0".equals(b_query))// 首次进入界面
	{
	    a_code = "";
	    //日期设为所有
	    businessDate = "all";
	    //查询条件设为空
	    expr = "" ;
	    factor = "" ;
	    this.getFormHM().put("expr", expr);
	    this.getFormHM().put("factor", factor);
	}
	    

	this.getFormHM().put("a_code",a_code);


	
	ContractBo bo = new ContractBo(this.frameconn, this.userView);
	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_PARAM", "Params");
	// 人员库
	String nbaseStr = xml.getTextValue("/Params/Bonus/base");
	String[] nbaseArray = nbaseStr.split(",");

	// 工号
	String jobnum = xml.getTextValue("/Params/Bonus/num");
	this.getFormHM().put("jobnumFld", jobnum);
	// 奖金子集
	String bonusSet = xml.getTextValue("/Params/Bonus/setid");
	if (bonusSet == null || (bonusSet != null && bonusSet.trim().length() == 0))
	    throw new GeneralException("请先在参数设置中设定奖金子集！");

	this.getFormHM().put("bonusSet", bonusSet);
	ArrayList fieldlist = this.getFieldList(bonusSet, jobnum);
	StringBuffer buf = new StringBuffer("select * from (");
	HashMap dbmap = bo.searchNbase();
	String dateFld = "";
	String doStatusFld = "";
	for (int i = 0; i < nbaseArray.length; i++)
	{
	    String dbpri = nbaseArray[i].trim();
	    if (dbpri.length() > 0)
	    {
		String dbname = (String) dbmap.get(dbpri);
		String maintable = dbpri + "A01";

		buf.append("select '"+dbname+"' dbname,'" + dbpri + "' dbase," + maintable + ".b0110," + maintable + ".e0122," + maintable + ".a0101," + maintable + ".a0000,");
		if (jobnum != null && jobnum.length() > 0)
		    buf.append(maintable + "." + jobnum + ",");

		String subset = dbpri + bonusSet;
		ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
		for (int j = 0; j < list.size(); j++)
		{
		    FieldItem fielditem = (FieldItem) list.get(j);
		    buf.append(subset + "." + fielditem.getItemid() + ",");
		    if (dateFld.length() == 0 && "业务日期".equals(fielditem.getItemdesc()))
			dateFld = fielditem.getItemid();
		    if (doStatusFld.length() == 0 && "处理状态".equals(fielditem.getItemdesc()))
			doStatusFld = fielditem.getItemid();

		}

		buf.append(subset + ".CreateUserName,");
		buf.append(subset + ".a0100,");
		buf.append(subset + ".i9999");
		buf.append(" from " + maintable + "  join " + subset + " on " + maintable + ".a0100=" + subset + ".a0100 ");
		buf.append("union all ");
	    }
	}
	buf.setLength(buf.length() - "union all ".length());
	buf.append(") a where 1=1 ");
	if (a_code != null && a_code.trim().length() > 1)
	{
	    String codesetid = a_code.substring(0, 2);
	    String value = a_code.substring(2);

	    if (value != null && value.trim().length() > 0)
	    {
		if ("UN".equalsIgnoreCase(codesetid))
		    buf.append(" and a.b0110 like '" + value + "%'");
		else if ("UM".equalsIgnoreCase(codesetid))
		    buf.append(" and a.e0122 like '" + value + "%'");
	    }
	}

	if (!this.userView.isSuper_admin() && !"1".equals(this.userView.getGroupId()))
	{
	    String code = this.userView.getManagePrivCode();
	    String value = this.userView.getManagePrivCodeValue();
	    if (code == null)
	    {
		buf.append(" and 1=2 ");
	    } else if ("UN".equalsIgnoreCase(code))
	    {
		buf.append(" and (a.b0110 like '");
		buf.append((value == null ? "" : value) + "%'");
		if (value == null)
		{
		    buf.append(" or a.b0110 is null ");
		}
		buf.append(")");
	    } else if ("UM".equalsIgnoreCase(code))
	    {
		buf.append(" and (a.e0122 like '");
		buf.append((value == null ? "" : value) + "%'");
		if (value == null)
		{
		    buf.append(" or a.e0122 is null ");
		}
		buf.append(")");
	    }
	}

	ContentDAO dao = new ContentDAO(this.getFrameconn());
	   
	// 取得业务日期的列表
	ArrayList busiDateList = new ArrayList();
	String sqlStr = "select distinct b." + dateFld + " from (" + buf.toString() + ") b order by b." + dateFld;
	try
	{
	    RowSet rs = dao.search(sqlStr);
	    CommonData temp = new CommonData("all", ResourceFactory.getProperty("label.all"));
	    busiDateList.add(temp);
	    HashMap map = new HashMap();
	    while (rs.next())
	    {
		if (rs.getDate(1) == null)
		    continue;
		String busiDate = PubFunc.FormatDate(rs.getDate(1), "yyyy.MM");

		if (!map.containsKey(busiDate))
		{
		    CommonData data = new CommonData(busiDate, busiDate);
		    busiDateList.add(data);
		    map.put(busiDate, busiDate);
		}
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}

	if (businessDate.length() == 0 && busiDateList.size() > 0)
	{
	    CommonData temp = (CommonData) busiDateList.get(0);
	    businessDate = temp.getDataValue();
	}
	//业务日期作为条件
	String dateConSql = this.getDateCond(businessDate, dateFld);
	if (dateConSql.length() > 0)
	{
	    buf.append(" and (" + dateConSql + ")");
	}
	
	
	expr = expr == null ? "" : SafeCode.decode(expr);
	factor = factor == null ? "" : SafeCode.decode(factor);
	if (expr.length() > 0 && factor.length() > 0)
	{
	    String condSql = "";
	    HashMap fieldItemMap = new HashMap();
	    FieldItem item = new FieldItem();
	    item.setCodesetid("@@");
	    item.setUseflag("1");
	    item.setItemtype("A");
	    item.setItemid("dbase");
	    item.setAlign("left");
	    item.setItemdesc("人员库");
	    fieldItemMap.put("dbase", item);

	    
	    item = new FieldItem();
	    item.setCodesetid("0");
	    item.setUseflag("1");
	    item.setItemtype("A");
	    item.setItemid("CreateUserName");
	    item.setAlign("left");
	    item.setItemdesc("录入员");
	    fieldItemMap.put("CreateUserName", item);
	    
	    fieldItemMap.put("b0110", DataDictionary.getFieldItem("b0110"));
	    fieldItemMap.put("e0122", DataDictionary.getFieldItem("e0122"));
	    fieldItemMap.put("a0101", DataDictionary.getFieldItem("a0101"));

	    ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
	    for (int i = 0; i < list.size(); i++)
	    {
		FieldItem fielditem = (FieldItem) list.get(i);
		fielditem.setUseflag("1");
		Field field = fielditem.cloneField();
		String itemid = field.getName();
		if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
		    continue;

		fieldItemMap.put(fielditem.getItemid(), fielditem);
	    }

	    FactorList factor_bo = new FactorList(expr, factor, this.userView.getUserId(), fieldItemMap);
	    condSql = factor_bo.getSingleTableSqlExpression("a");

	    if (condSql.length() > 0)
	    {
		buf.append(" and (" + condSql + ")");
	    }
//	    this.getFormHM().put("expr", "");
//	    this.getFormHM().put("factor", "");
	}
	
	buf.append(" order by dbase,a0000");
	this.getFormHM().put("businessDate", businessDate);
	this.getFormHM().put("sql", buf.toString());
	this.getFormHM().put("fieldlist", fieldlist);
	this.getFormHM().put("doStatusFld", doStatusFld);
	this.getFormHM().put("dateList", busiDateList);
    }

    /**
         * 求当前数据集的指标列表
         * 
         * @param setname
         * @return
         */
    private ArrayList getFieldList(String setname, String jobnum)
    {

	ArrayList fieldlist = new ArrayList();

	Field tempfield = new Field("dbname", "人员库");
	tempfield.setReadonly(true);
	tempfield.setCodesetid("0");
	tempfield.setDatatype(DataType.STRING);
	tempfield.setLength(10);
	fieldlist.add(tempfield);
	
	tempfield = new Field("dbase", "人员库前缀");
	tempfield.setVisible(false);
	tempfield.setCodesetid("0");
	tempfield.setDatatype(DataType.STRING);
	tempfield.setLength(10);
	fieldlist.add(tempfield);

	tempfield = new Field("A0100", "A0100");
	tempfield.setDatatype(DataType.STRING);
	tempfield.setLength(8);
	tempfield.setReadonly(true);
	tempfield.setVisible(false);
	tempfield.setCodesetid("0");
	fieldlist.add(tempfield);

	tempfield = new Field("I9999", "序号");
	tempfield.setDatatype(DataType.INT);
	tempfield.setReadonly(true);
	tempfield.setVisible(false);
	tempfield.setCodesetid("0");
	fieldlist.add(tempfield);

	FieldItem fielditem = DataDictionary.getFieldItem("b0110");
	tempfield = fielditem.cloneField();
	tempfield.setReadonly(true);
	tempfield.setVisible(true);
	fieldlist.add(tempfield);

	fielditem = DataDictionary.getFieldItem("e0122");
	tempfield = fielditem.cloneField();
	tempfield.setVisible(true);
	tempfield.setReadonly(true);
	fieldlist.add(tempfield);

	fielditem = DataDictionary.getFieldItem("a0101");
	tempfield = fielditem.cloneField();
	tempfield.setReadonly(true);
	tempfield.setVisible(true);
	fieldlist.add(tempfield);

	if (jobnum != null && jobnum.length() > 0)
	{
	    fielditem = DataDictionary.getFieldItem(jobnum);
	    tempfield = fielditem.cloneField();
	    tempfield.setLabel("工号");
	    tempfield.setReadonly(true);
	    fieldlist.add(tempfield);
	}

	ArrayList list = DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
	for (int i = 0; i < list.size(); i++)
	{
	    fielditem = (FieldItem) list.get(i);
	    Field field = fielditem.cloneField();
	    String itemid = field.getName();
	    if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
		field.setVisible(false);
	    if ("1".equals(this.userView.analyseFieldPriv(itemid, 0)) && "1".equals(this.userView.analyseFieldPriv(itemid, 1)))
		field.setReadonly(true);
	    if (!"2".equals(this.userView.analyseTablePriv(setname)))
		field.setReadonly(true);
	    field.setSortable(true);

	    if ("处理状态".equals(fielditem.getItemdesc()) || "业务日期".equals(fielditem.getItemdesc()))
		field.setReadonly(true);
	    fieldlist.add(field);
	}

	tempfield = new Field("CreateUserName", "录入员");
	tempfield.setDatatype(DataType.STRING);
	tempfield.setReadonly(true);
	tempfield.setVisible(true);
	fieldlist.add(tempfield);

	return fieldlist;
    }

    private String getDateCond(String busidate, String busiDateFld)
    {

	StringBuffer buf = new StringBuffer();
	if (busidate == null || "".equalsIgnoreCase(busidate) || "all".equalsIgnoreCase(busidate))
	    return "";
	String[] datearr = StringUtils.split(busidate, ".");
	String theyear = datearr[0];
	String themonth = datearr[1];
	buf.append(Sql_switcher.year("a." + busiDateFld));
	buf.append("=");
	buf.append(theyear);
	buf.append(" and ");
	buf.append(Sql_switcher.month("a." + busiDateFld));
	buf.append("=");
	buf.append(themonth);
	return buf.toString();
    }

}
