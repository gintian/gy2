package com.hjsj.hrms.businessobject.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainResourceBo.java
 * </p>
 * <p>
 * Description:培训体系
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainResourceBo
{
    private Connection cn;

    private String recTable;

    private String primaryField;

    private String recName;

    private String nameFld;// 记录名称字段
    
    private String classid;

    /* 资源类别[1-培训机构][2-培训教师][3-培训场所][4-培训设施][5-培训资料] */
    public TrainResourceBo(Connection conn, String type)
    {
    this.classid="";
	this.cn = conn;
	this.setRecTable(type);
    }
    
   
    public TrainResourceBo(Connection conn, String type, String classid)
    {

	this.cn = conn;
	this.classid = classid;
	this.setRecTable(type);
    }

    /**
         * 取得当前资源表的所有字段属性
         */
    public ArrayList getItems() throws GeneralException
    {

	DbWizard dbWizard = new DbWizard(this.cn);
	if (!dbWizard.isExistTable(this.recTable, false)) {
        throw new GeneralException("数据表[" + this.recTable + "]不存在！");
    }

	ArrayList list = new ArrayList();
	ArrayList fieldList = DataDictionary.getFieldList(this.recTable, Constant.USED_FIELD_SET);
	if (fieldList == null) {
        throw new GeneralException("数据表中可用字段不存在！");
    }
	for (int i = 0; i < fieldList.size(); i++)
	{
	    FieldItem item = (FieldItem) fieldList.get(i);

	    String itemid = item.getItemid();
	    String itemName = item.getItemdesc();
	    String itemType = item.getItemtype();
	    String codesetId = item.getCodesetid();
	    // System.out.println(itemid + "--" + itemName + "--" + itemType
	    // + "--" + codesetId);
	    LazyDynaBean abean = new LazyDynaBean();
	    abean.set("itemid", itemid);
	    abean.set("itemName", itemName);
	    abean.set("itemType", itemType);
	    abean.set("codesetId", codesetId);
	    list.add(abean);
	}
	return list;
    }

    public ArrayList getItems2() throws GeneralException
    {

	ArrayList list = new ArrayList();
	ArrayList fieldList = DataDictionary.getFieldList(this.recTable, Constant.USED_FIELD_SET);
	if (fieldList == null) {
        throw new GeneralException("数据表中可用字段不存在！");
    }
	for (int i = 0; i < fieldList.size(); i++)
	{
	    FieldItem item = (FieldItem) fieldList.get(i);

	    if (!item.isVisible()) {
            continue;
        }

	    String itemid = item.getItemid();
	    String itemName = item.getItemdesc();
	    String itemType = item.getItemtype();
	    String codesetId = item.getCodesetid();

	    LazyDynaBean abean = new LazyDynaBean();
	    abean.set("itemid", itemid);
	    abean.set("itemName", itemName);
	    abean.set("itemType", itemType);
	    abean.set("codesetId", codesetId);
	    list.add(abean);
	}
	return list;
    }

    public String getRecTable()
    {

	return recTable;
    }

    public void setRecTable(String type)
    {

	if ("1".equals(type))
	{
	    this.recTable = "r01";
	    this.primaryField = "r0101";
	    this.recName = "培训机构";
	    this.nameFld = "r0102";
	} else if ("2".equals(type))
	{
	    this.recTable = "r04";
	    this.primaryField = "r0401";
	    this.recName = "培训教师";
	    this.nameFld = "r0402";
	} else if ("3".equals(type))
	{
	    this.recTable = "r10";
	    this.primaryField = "r1001";
	    this.recName = "培训场所";
	    this.nameFld = "r1011";
	} else if ("4".equals(type))
	{
	    this.recTable = "r11";
	    this.primaryField = "r1101";
	    this.recName = "培训设施";
	    this.nameFld = "r1102";
	} else if ("5".equals(type))
	{
	    this.recTable = "r07";
	    this.primaryField = "r0701";
	    this.recName = "培训资料";
	    this.nameFld = "r0702";
	} else if ("6".equals(type))// 培训项目（类别）
	{
	    this.recTable = "r13";
	    this.primaryField = "r1301";
	} else if ("7".equals(type))// 培训课程
	{
	    this.recTable = "r50";
	    this.primaryField = "r5000";
	} else if ("8".equals(type))// 资源评估
	{
	    this.recTable = "r37";
	    this.recName = "培训资源评估";
	    this.primaryField = "r3701";
	} else if ("9".equals(type))// 学员评估
	{
	    this.recTable = "r40";
	    this.recName = "学员评估结果";
	    this.primaryField = "r4001";
	}
    }

    public String getPrimaryField()
    {

	return primaryField;
    }

    public String getMemoFld1(String priFld, String memoFldName,String dbname)
    {

	String memoFld = "";
	ContentDAO dao = new ContentDAO(this.cn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("select " + memoFldName);
	strSql.append(" from " + this.recTable);
	strSql.append(" where " + this.primaryField);
	strSql.append("='" + priFld + "'");
	strSql.append(" and nbase = '"+dbname+"'");
	
	String itemdesc = DataDictionary.getFieldItem(memoFldName, this.recTable).getItemdesc();
	
	if(this.classid!=null&&this.classid.length()>0){
		strSql.append(" and r4005='"+this.classid+"'");
	}
	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next())
	    {
		memoFld = (rs.getString(memoFldName) == null ? "" : rs.getString(memoFldName));
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return memoFld + "@@" + itemdesc;
    }
    
    public String getMemoFld(String priFld, String memoFldName)
    {

	String memoFld = "";
	String itemdesc = DataDictionary.getFieldItem(memoFldName, this.recTable).getItemdesc();
	
	ContentDAO dao = new ContentDAO(this.cn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("select " + memoFldName);
	strSql.append(" from " + this.recTable);
	strSql.append(" where " + this.primaryField);
	strSql.append("='" + priFld + "'");
	if(this.classid!=null&&this.classid.length()>0){
		strSql.append(" and r4005='"+this.classid+"'");
	}
	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next())
	    {
		memoFld = (rs.getString(memoFldName) == null ? "" : rs.getString(memoFldName));
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return memoFld + "@@" + itemdesc;
    }

    public void updateMemoFld(String priFld, String memoFldName, String value , String dbName)
    {
	ContentDAO dao = new ContentDAO(this.cn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("update " + this.recTable);
	strSql.append(" set " + memoFldName + "='" + value + "' ");
	strSql.append("where " + this.primaryField);
	strSql.append("='" + priFld + "'");
	if(!"".equalsIgnoreCase(dbName)){		
		strSql.append(" and nbase = '"+dbName+"'");
	}
	if(this.classid!=null&&this.classid.length()>0) {
        strSql.append(" and r4005='" + this.classid + "'");
    }
	try
	{
	    dao.update(strSql.toString());
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }

    public void delete(String[] ids)
    {

	if (ids == null || ids.length == 0) {
        return;
    }
	ContentDAO dao = new ContentDAO(this.cn);

	StringBuffer idstr = new StringBuffer();
	for (int i = 0; i < ids.length; i++)
	{
	    if("true".equalsIgnoreCase(ids[i]) || ids[i] == null || ids[i].length() < 1) {
            continue;
        }
	    
	    idstr.append("'");
	    idstr.append(PubFunc.decrypt(SafeCode.decode(ids[i])));
	    idstr.append("',");
	}
	idstr.setLength(idstr.length() - 1);
	try
	{
	    if ("r13".equals(this.recTable))
	    {
		StringBuffer strSql1 = new StringBuffer();
		strSql1.append("select * from r13 where r1301 in (");
		strSql1.append(idstr.toString());
		strSql1.append(") or r1308 in (");
		strSql1.append(idstr.toString() + ")");
		RowSet rs = dao.search(strSql1.toString());
		while (rs.next())
		{
		    String codeitem = rs.getString("r1301");
		    String pcodeitem = rs.getString("r1308");
		    String codename = rs.getString("r1302");
		    CodeItem code = new CodeItem();
		    if (pcodeitem != null && !"".equals(pcodeitem))
		    {
			code.setCcodeitem(pcodeitem);
			code.setPcodeitem(pcodeitem);
		    }
		    code.setCodeid("1_06");
		    code.setCodeitem(codeitem);
		    code.setCodename(codename);
		    AdminCode.removeCodeItem(code);// 把手动添加到代码类中的值也删掉
		}
	    }

	    StringBuffer strSql = new StringBuffer();
	    strSql.append("delete from ");
	    strSql.append(this.recTable);
	    strSql.append(" where " + this.primaryField + " in (");
	    strSql.append(idstr.toString());
	    strSql.append(")");

	    if ("r13".equals(this.recTable)) {
            strSql.append(" or r1308 in (" + idstr.toString() + ")");
        }

	    dao.delete(strSql.toString(), new ArrayList());

	    strSql.delete(0, strSql.length());
	    if("r01".equalsIgnoreCase(this.recTable)){
	    	strSql.append("update r31 set r3128='' where r3128 in (" + idstr.toString() + ")");
	    }else if("r04".equalsIgnoreCase(this.recTable)){
	    	strSql.append("update r41 set r4106='' where r4106 in (" + idstr.toString() + ")");
	    }else if("r07".equalsIgnoreCase(this.recTable)){
	    	strSql.append("update r41 set r4114='' where r4114 in (" + idstr.toString() + ")");
	    }else if("r10".equalsIgnoreCase(this.recTable)){
	    	strSql.append("update r31 set r3126='' where r3126 in (" + idstr.toString() + ")");
	    }else if("r13".equalsIgnoreCase(this.recTable)){
	    	strSql.append("update r41 set r4105='' where r4105 in (" + idstr.toString() + ")");
	    }
		if (!"r11".equalsIgnoreCase(this.recTable)) {
            dao.update(strSql.toString());
        }
	    
	    if ("r04".equalsIgnoreCase(this.recTable) || "r07".equalsIgnoreCase(this.recTable)) {
			strSql.delete(0, strSql.length());
			strSql.append("delete from tr_res_file where r0701 in (");
			strSql.append(idstr.toString());
			strSql.append(") and (");
			if ("r04".equalsIgnoreCase(this.recTable)) {
                strSql.append("type='1'");
            }
			if ("r07".equalsIgnoreCase(this.recTable)) {
                strSql.append("type='0' or type='' or type is null");
            }
			strSql.append(")");
			dao.delete(strSql.toString(), new ArrayList());
		}
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }

    public String getRecName()
    {

	return recName;
    }

    /**
         * 是否新建
         */
    public boolean isNew(String priFldValue)
    {

	boolean flag = true;
	if (priFldValue == null) {
        return flag;
    }
	ContentDAO dao = new ContentDAO(this.cn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("select * from ");
	strSql.append(this.recTable);
	strSql.append(" where " + this.primaryField + " ='");
	strSql.append(priFldValue);
	strSql.append("'");

	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next()) {
            flag = false;
        }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return flag;
    }

    /**
         * 得到I9999
         */
    public String getI9999(String value)
    {

	String i9999 = "";
	ContentDAO dao = new ContentDAO(this.cn);
	StringBuffer strSql = new StringBuffer();
	
	strSql.append("select "+Sql_switcher.isnull("max(i9999)", "0")+" n  from ");
	strSql.append(this.recTable);
	strSql.append(" where b0110='");
	strSql.append(value);
	strSql.append("'");
	int count = 1;
	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next()) {
            count = rs.getInt(1) + 1;
        }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	i9999 = new Integer(count).toString();
	return i9999;
    }

    public String getNameFld()
    {

	return nameFld;
    }
    
    public String getWhereStr(String search) throws GeneralException{
    	StringBuffer wherestr = new StringBuffer();
		if (search != null && search.trim().length() > 0) {
			search = SafeCode.decode(search);
			search = PubFunc.keyWord_reback(search);
			search = PubFunc.reBackWord(search);
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				try{
					FactorList factorslist=new FactorList(searcharr[0], searcharr[1], "", false, "1".equals(searcharr[2]), false, 0, "su");
					wherestr.append(" and "+factorslist.getSingleTableSqlExpression(this.recTable));
		    	 }catch(Exception e){
		    		 throw GeneralExceptionHandler.Handle(e);
		    	 }
				
			}
		}
		//System.out.println(wherestr.toString());
		return wherestr.toString();
    }
    
    public static boolean hasTrainResourcePrivByType(String type, UserView userView)
    {
        boolean hasPriv = false;
        
        int tabType = Integer.valueOf(type).intValue();
        
        switch(tabType)
        {
        case 1: //培训机构
            hasPriv = userView.hasTheFunction("32301");
            break;
        case 2: //培训教师
            hasPriv = userView.hasTheFunction("32302");
            break;
        case 3: //培训场所
            hasPriv = userView.hasTheFunction("32303") || userView.hasTheFunction("0912");
            break;
        case 4: //培训设施
            hasPriv = userView.hasTheFunction("32304");
            break;
        case 5: //培训资料
            hasPriv = userView.hasTheFunction("32305");
            break;
        case 6: //培训类别
            hasPriv = userView.hasTheFunction("32300");
            break;
        case 7: //培训课程
            hasPriv = userView.hasTheFunction("090903")||userView.hasTheFunction("32306C");
            break;
        case 8: //培训班-培训评估
            hasPriv = userView.hasTheFunction("323309");
            break;
        case 9: //培训学员
            hasPriv = userView.hasTheFunction("323308");
            break;
        default:
            break;      
        }
        
        return hasPriv;
    }
    
    public static boolean hasTrainResourcePrivByTab(String tab, UserView userView)
    {
        boolean hasPriv = false;
        String type = "0";
        
        if ("r01".equalsIgnoreCase(tab))// 培训机构
        {
            type = "1";
        } else if ("r04".equalsIgnoreCase(tab))// 培训教师
        {
            type = "2";
        } else if ("r07".equalsIgnoreCase(tab))// 培训资料
        {
            type = "5";
        } else if ("r10".equalsIgnoreCase(tab))// 培训场所
        {
            type = "3";
        } else if ("r11".equalsIgnoreCase(tab))//培训设施
        {
            type = "4";
        } else if ("r13".equalsIgnoreCase(tab))// 培训项目
        {
            type = "6";
        }
        
        hasPriv = hasTrainResourcePrivByType(type, userView);
        
        return hasPriv;
    }
    
    public static String substr(String str) {

        if (str.endsWith("&...")) {
            str = str.substring(0, str.lastIndexOf("&...")) + "...";
        }

        if (str.endsWith("&n...")) {
            str = str.substring(0, str.lastIndexOf("&n...")) + "...";
        }

        if (str.endsWith("&nb...")) {
            str = str.substring(0, str.lastIndexOf("&nb...")) + "...";
        }

        if (str.endsWith("&nbs...")) {
            str = str.substring(0, str.lastIndexOf("&nbs...")) + "...";
        }

        if (str.endsWith("&nbsp...")) {
            str = str.substring(0, str.lastIndexOf("&nbsp...")) + "...";
        }

        if (str.endsWith("<...")) {
            str = str.substring(0, str.lastIndexOf("<...")) + "...";
        }

        if (str.endsWith("<b...")) {
            str = str.substring(0, str.lastIndexOf("<b...")) + "...";
        }

        if (str.endsWith("<br...")) {
            str = str.substring(0, str.lastIndexOf("<br...")) + "...";
        }

        return str;

    }
}
