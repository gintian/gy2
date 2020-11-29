package com.hjsj.hrms.businessobject.performance.options;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title:PerRelationBo.java</p>
 * <p>Description:考核关系</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-15 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PerRelationBo
{
    Connection conn = null;

    private UserView userview=null;
    
    public PerRelationBo(Connection cn)
    {

    	this.conn = cn;
    }

    public PerRelationBo(Connection cn,UserView _userview)
    {

		this.conn = cn;
		this.userview=_userview;
    }
    
    
    /** 获得考核对象数据 */
    public ArrayList getPerObjectDataList(String a_code,String querySql) throws GeneralException
    {
    	querySql = PubFunc.keyWord_reback(querySql);
		ArrayList list = new ArrayList();
		String code = "";
		String codeset = "";
		if (a_code != null && a_code.length() > 1)
		{
		    code = a_code.substring(2, a_code.length());
		    codeset = a_code.substring(0, 2);
		}
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_object_std where 1=1 ");
		    if (code != null && !"".equals(code))
		    {
				if ("UN".equalsIgnoreCase(codeset)) {
                    sql.append(" and b0110 like '" + code + "%' ");
                } else if ("UM".equalsIgnoreCase(codeset)) {
                    sql.append(" and e0122 like '" + code + "%' ");
                } else if ("@k".equalsIgnoreCase(codeset)) {
                    sql.append(" and e01a1 like '" + code + "%' ");
                }
		    }
		    
			PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
			String userPrivwhl = pb.getPrivWhere(this.userview);
			sql.append(userPrivwhl);    
		    
		    if(querySql.length()>0) {
                sql.append(" and object_id in ("+querySql+")");
            }
		    sql.append(" order by a0000");
	
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String obj_body_id = rowSet.getString("obj_body_id");
		
				abean = new LazyDynaBean();
				abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				abean.set("obj_body_id", obj_body_id);
				String a0101 = rowSet.getString("a0101");
				abean.set("a0101", a0101 == null ? "" : a0101);
				abean.set("object_id", rowSet.getString("object_id"));
				abean.set("postid", e01a1!=null?e01a1:"");
				list.add(abean);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 获得考核对象类别 */
    public ArrayList getObjTypes() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    //只是列出人员考核对象类别
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1 and status=1  and (object_type=2 or object_type is null) order by seq");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得所有考核对象类别 包括无效的*/
    public ArrayList getObjTypes2() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得对象对应的主体 */
    public ArrayList getMainBodys(String object_id) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
		    HashMap mainbodyTypes = this.getMainBodyTypes();
		    ContentDAO dao = new ContentDAO(this.conn);
		    
		    // per_mainbodyset.level(SQLServer)在Oracle中是level_o modify by 刘蒙
		    String level = "level";
		    if (Sql_switcher.searchDbServer() == 2) {
		    	level = "level_o";
		    }
		    
		    StringBuffer sql = new StringBuffer();
		    sql.append("select pstd.*,");
		    sql.append("pset.").append(level).append(" ").append(level);
		    sql.append(" from per_mainbody_std pstd,per_mainbodyset pset ");
		    sql.append("where pstd.object_id='");
		    sql.append(object_id);
		    sql.append("' and pstd.body_id=pset.body_id  order by ");
		    sql.append(" pset.").append(level).append(" desc,pstd.body_id,pstd.mainbody_id");
	
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String body_id = rowSet.getString("body_id");
		
				abean = new LazyDynaBean();
				abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				abean.set("body_id", body_id==null?"":body_id);
				String a0101 = rowSet.getString("a0101");
				abean.set("a0101", a0101 == null ? "" : a0101);
				abean.set("object_id", object_id);
				abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				list.add(abean);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 获得考核主体类别 */
    public HashMap getMainBodyTypes() throws GeneralException
    {

		HashMap map = new HashMap();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=0 or body_type is null");
		    rowSet = dao.search(sql.toString());
		    while (rowSet.next()) {
                map.put(rowSet.getString("body_id"), rowSet.getString("name"));
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return map;
    }
    
    /** 获得考核主体类别 */
    public ArrayList getMainBodyTypes2() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where (body_type=0 or body_type is null) and status=1 and body_id!=-1 order by seq");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得所有考核主体类别 */
    public ArrayList getMainBodyTypes3() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where (body_type=0 or body_type is null)");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 手工选择 条件选择 考核对象 */
    public void handInsertObjects(String a0100s) throws GeneralException
    {
    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
	
		    StringBuffer insertSql = new StringBuffer();
		    insertSql.append("insert into per_object_std(object_id,b0110,e0122,e01a1,a0101,a0000)");
		    insertSql.append("select a0100,b0110,e0122,e01a1,a0101,a0000 from usra01 where a0100 in (" + a0100s + ")");
		    insertSql.append(" and a0100 not in (select object_id from per_object_std)");
	
		    dao.insert(insertSql.toString(), new ArrayList());
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    }

    /** 取出选中的考核对象 */
    public ArrayList getKhObjectsList(String[] array) throws GeneralException
    {
		ArrayList list = new ArrayList();
		if (array == null) {
            return list;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sqlStr = new StringBuffer("select * from per_object_std");
		sqlStr.append(" where object_id in (");
		for (int i = 0; i < array.length; i++)
		{
		    if ("".equals(array[i])) {
                continue;
            }
		    sqlStr.append("'" + array[i] + "',");
		}
		sqlStr = new StringBuffer(sqlStr.substring(0, sqlStr.length() - 1) + ")");
	
		RowSet rowSet = null;
		try
		{
			rowSet = dao.search(sqlStr.toString());
		    while (rowSet.next())
		    {
				CommonData temp = new CommonData(rowSet.getString("object_id"), rowSet.getString("a0101"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;

    }

    /** 根据考核对象,考核主体类别查询考核主体 */
    public ArrayList getMainBodyList(HashMap objs, String body_id) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer();
		    sql.append("select * from per_mainbody_std where 1=1 ");
	
		    StringBuffer buf = new StringBuffer();
		    Set objSet = objs.keySet();
		    Iterator it = objSet.iterator();
		    while (it.hasNext())
		    {
				String objId = (String) it.next();
				buf.append(",'" + objId + "'");
		    }
	
		    if (buf.length() > 0) {
                sql.append(" and object_id in(" + buf.substring(1) + ")");
            }
	
		    if (!"all".equals(body_id)) {
                sql.append(" and body_id='" + body_id + "'");
            }
	
		    sql.append(" order by mainbody_id");
	
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    HashMap bodyMap = this.getMainBodyTypes();
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String a0101 = rowSet.getString("a0101");
				String bodyId = rowSet.getString("body_id");
				abean = new LazyDynaBean();
				abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				String objectID = rowSet.getString("object_id");
				String objectName = (String) objs.get(objectID);
				abean.set("objectID", objectID);
				abean.set("objectName", objectName == null ? "" : objectName);
				abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				abean.set("a0101", a0101 == null ? "" : a0101);
				abean.set("bodyTypeName", bodyMap.get(bodyId)==null?"":(String) bodyMap.get(bodyId));
				abean.set("bodyid", bodyId);
				list.add(abean);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 指定考核主体/条件（手工）选人 
     * 原则 考核关系里  考核对象不能把自己选为自己的考核主体
     * */
    public void selMainBody(String a0100s,String mainBodyType, String object) throws GeneralException
    {	
    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    // 先删除该对象已存在的重复主体
		    String delStr = "delete from per_mainbody_std where object_id = '" + object + "' and mainbody_id in (" + a0100s + ")";
		    dao.delete(delStr, new ArrayList());
	
		    StringBuffer insertSql = new StringBuffer();
		    insertSql.append("insert into per_mainbody_std (mainbody_id,object_id,b0110,e0122,e01a1,a0101,body_id)");
			insertSql.append("select a0100,'" + object + "',b0110,e0122,e01a1,a0101,'" + mainBodyType + "' from usra01 where a0100 in (" + a0100s + ") ");
			insertSql.append(" and a0100<>'"+ object + "'");
			
		    dao.insert(insertSql.toString(), new ArrayList());
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
    }
    
   /**删除考核主体*/
    public void delKhMainBody(String[] ids)  throws GeneralException
    {

		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		// 删除考核主体表
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from per_mainbody_std where mainbody_id=? and object_id=? and body_id=?");
	
		for (int i = 0; i < ids.length; i++)
		{	
		    String id = ids[i];// mainbody_id:objectID:body_id
		    String[] temp = id.split(":");
		    String mainBody = temp[0];
		    String object_id = temp[1];
		    String body_id = temp[2];
		    
		    ArrayList list1 = new ArrayList();
		    list1.add(mainBody);
		    list1.add(object_id);
		    list1.add(body_id);
		    list.add(list1);
		}
	
		try
		{
			
		    dao.batchUpdate(strSql.toString(), list);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
    }
    
    /**粘贴考核主体
     * 原则不可以把自己做为自己的考核主体*/
    public void pasteKhMainBody(String objectPasted,String objectCopyed)  throws GeneralException
    {
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("insert into per_mainbody_std(mainbody_id,object_id,b0110,e0122,e01a1,a0101,body_id)");
			buf.append("select mainbody_id,'");
			buf.append(objectPasted);
			buf.append("',b0110,e0122,e01a1,a0101,body_id from per_mainbody_std where object_id='");
			buf.append(objectCopyed);
			buf.append("' and mainbody_id not in (select mainbody_id from per_mainbody_std where object_id='");
			buf.append(objectPasted);
			buf.append("') and mainbody_id<>'"+objectPasted+"' ");//不让把自己 作为自己的考核主体 
		
			dao.insert(buf.toString(), new ArrayList());
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
    }
    
    /**取出某个表中的字段进行单表查询*/
    public ArrayList getFieldlist(String tablename)
    {
		ArrayList list = new ArrayList();	
		ArrayList fieldList = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++)
		{
		    FieldItem fi = (FieldItem) fieldList.get(i);
		    String itemid = fi.getItemid();
		    if("b0110,e0122,e01a1,a0101".indexOf(itemid)!=-1)
		    {
				CommonData temp = new CommonData(fi.getItemid() + "<@>" + fi.getItemdesc() + "<@>" + fi.getItemtype() + "<@>" + fi.getCodesetid() + "<@>" + tablename, fi.getItemdesc());
				list.add(temp);
		    }	  
		}
		CommonData temp = new CommonData( "obj_body_id<@>考核对象类别<@>N<@>0<@>" + tablename, "考核对象类别");
		list.add(temp);
		return list;
    }
    
    /**获得 汇报关系中 直接上级指标*/	
    public String getPS_SUPERIOR_value()
    {
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
		if(vo==null) {
            return fieldItem;
        }
		String param=vo.getString("str_value");
		if(param==null|| "".equals(param)|| "#".equals(param)) {
            return fieldItem;
        }
		fieldItem=param;
		return fieldItem;
    }
    
    /**获得本人考核主体类别的ID
     * @throws GeneralException */
    public String getSelfBodyID() throws GeneralException
    {
		String selbodyid = "";
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    String level = "level";
		    if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
                level = "level_o";
            }
		    String strSql="select body_id from per_mainbodyset where "+level+"=5";
		    rowSet = dao.search(strSql);
	
		    if (rowSet.next()) {
                selbodyid = rowSet.getString(1)==null?"":rowSet.getString(1);
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return selbodyid;
    }
    
    /**判断是否可以进行考核关系的调整
     * @throws GeneralException */
    public boolean isEnableFlag() throws GeneralException
    {
		boolean flag = false;
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    String strSql="select * from per_plan where method=2 and status in (4,8,6)";
		    rowSet = dao.search(strSql);
	
		    if (rowSet.next()) {
                flag=true;
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
    }
    
    /**选出参与了目标计划的考核对象，且这些计划处于分发 评估 和启动状态*/
    public HashMap getJoinedObjs()  throws GeneralException
    {
    	HashMap map = new HashMap();
    	RowSet rowSet = null;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    String strSql="select distinct object_id,a0101 from per_object where plan_id in (select plan_id  from per_plan where method=2 and status in (4,8,6))";
    	    rowSet = dao.search(strSql);
    	    while (rowSet.next())
    	    {
    	    	map.put(rowSet.getString(1), rowSet.getString(2));
    	    }
    	    //团队负责人的考核关系也不允许变动
    	    strSql="select distinct mainbody_id,a0101 from per_mainbody where body_id=-1 and plan_id in (select plan_id  from per_plan where method=2 and status in (4,8,6))";
    	    rowSet = dao.search(strSql);
    	    while (rowSet.next())
    	    {
    	    	map.put(rowSet.getString(1),rowSet.getString(2));
    	    }
    	    
    	    if(rowSet!=null) {
                rowSet.close();
            }
    	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}
    	return map;
    }
}
