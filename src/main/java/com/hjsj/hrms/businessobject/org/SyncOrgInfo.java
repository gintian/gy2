package com.hjsj.hrms.businessobject.org;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.tablemodel.ModelField;
import com.hrms.frame.dao.tablemodel.TableModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * @author guodd
 *  change organization information 
 */
@SuppressWarnings("all")
public class SyncOrgInfo {
     
	private Connection conn= null;
	private RowSet frowset = null;
	private ArrayList resultlist = new ArrayList();
	   /**
	    * addOrg
	    * @param type  "@K","UN","UM"
	    * @param orgName 
	    * @param corCode 
	    * @param parentID 父节点的corCode// not null // 如果顶级节点 请传“root”
	    * @param a0000 
	    * @return message, success:ok
	    */
	   public String insert(String type,String orgName,String corCode,String parentID,String unitID,String a0000)
	   {
		   String mess = "ok";
		   RowSet rs = null;
		   Connection sconn = null;
		   try
		   {	
			   String parent_id = parentID;
			   if(parentID==null || parentID.trim().length()<=0 || "0".equalsIgnoreCase(parentID)) {
                   parentID = unitID;
               }
	    	   
			   sconn = AdminDb.getConnection();
			   ContentDAO dao = new ContentDAO(sconn);
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			   if(parentID==null || parentID.trim().length()<=0 || "".equals(parentID)){
				   if(!"UN".equalsIgnoreCase(type)) {
                       return "根节点下不能建部门和岗位!";
                   }
			   }
			    
			   
			   String parentCodesetid = "UN";
			   String parentCodeitemid = "";
			   int  parentGrade = 0;
			   String maxChild = "";
			   String codeitemid = "";
			   Map parentInfo = new HashMap();
			   AddOrgInfo aoi= new AddOrgInfo();
			   
			   if(parentID==null || parentID.trim().length()<=0 || "".equals(parentID)){
				   parentCodesetid = "UN";
				   parentGrade = 0;
				   maxChild = getRootMaxChild(dao,rs);
				   codeitemid = aoi.GetNext(maxChild,"");
				   parentCodeitemid = codeitemid;
			   }else{
				   parentInfo = getOrgID(type,parent_id,unitID,dao,rs);
				   if(parentInfo==null || parentInfo.size()<=0) {
                       return "请先同步上一级！";
                   }
				   parentCodesetid = parentInfo.get("parentCodesetid").toString();
				   parentCodeitemid = parentInfo.get("parentCodeitemid").toString();
				   parentGrade = Integer.parseInt(parentInfo.get("parentGrade").toString());
				   maxChild = (String) parentInfo.get("maxChild");
				   codeitemid = parentCodeitemid+aoi.GetNext(maxChild, parentCodeitemid);
			   }
				   
			   
			   if("UN".equalsIgnoreCase(type) && "UM".equalsIgnoreCase(parentCodesetid)) {
                   return "部门下不能建单位!";
               }
			      
			   //新建机构
			   StringBuffer sqlstr = new StringBuffer();
			   sqlstr.append(" insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,grade,start_date,end_date,corCode,a0000) ");
			   sqlstr.append(" values(?,?,?,?,?,?,?,?,?,?)");
			   
			   RecordVo vo = new RecordVo("organization");
			   vo.setString("codesetid", type);
			   vo.setString("codeitemid", codeitemid);
			   vo.setString("codeitemdesc", orgName);
			   vo.setString("parentid", parentCodeitemid);
			   vo.setString("childid", codeitemid);
			   vo.setInt("grade", parentGrade+1);
			   vo.setDate("start_date", DateUtils.getDate(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
			   vo.setDate("end_date", DateUtils.getDate("9999-12-31", "yyyy-MM-dd"));
			   vo.setString("corcode", corCode);
			   vo.setInt("a0000", Integer.parseInt(a0000));
			   
			   
//			   ArrayList dateList = new ArrayList();
//			   dateList.add(type);
//			   dateList.add(codeitemid);
//			   dateList.add(orgName);
//			   dateList.add(parentCodeitemid);
//			   dateList.add(codeitemid);
//			   dateList.add(parentGrade+1);
//			   dateList.add(getDate(sdf.format(new Date())));
//			   dateList.add(getDate("9999-12-31"));
//			   dateList.add(corCode);
//			   dateList.add(a0000);
//			   
//			   dao.insert(sqlstr.toString(), dateList);
			   
			   dao.addValueObject(vo);
			   
			   sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			   String sql = "";
			   //初始化主集信息
			   if("@K".equalsIgnoreCase(type)) {
                   sql = "insert into k01(e01a1,e0122,CreateTime,ModTime) values('"+codeitemid+"',"+parentInfo.get("parentCodeitemid")+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+")";
               } else {
                   sql = "insert into b01(b0110,CreateTime,ModTime) values('"+codeitemid+"',"
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+") ";
               }
			   dao.update(sql);
			   
			   // 更新父节点的子节点
			   sql = " update organization set childid='"+codeitemid+"' where codeitemid='"+parentCodeitemid+"'";
			   dao.update(sql);	
			   
			   
			   RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD", sconn);
				if(unit_code_field_constant_vo!=null)
				{
				  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
				  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
				  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){

					sql = "update B01 set "+unit_code_field+"='"+corCode+"',modtime="+Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))+"  where b0110='"+codeitemid+"'";
					  dao.update(sql);
				  }
				}
			   
		   }catch(Exception e){
			   e.printStackTrace();
			   System.out.println("SyncOrgInfo.class Error......method>>insert");
			   mess = "新增失败";
		   }finally{
			   try{
				   if(sconn!=null) {
                       sconn.close();
                   }
				   if(rs!=null){
					   rs.close();
				   }
			   }catch(Exception e){
				   e.printStackTrace();
			   }
		   }
		   
		   return mess;
	   }
	   
	   
	   /**
	    * addOrg
	    * @param type  "@K","UN","UM"
	    * @param orgName 
	    * @param corCode 
	    * @param parentID 父节点的corCode// not null // 如果顶级节点 请传“root”
	    * @param a0000 
	    * @return message, success:ok
	    */
	   public String insert(String type,String orgName,String corCode,String parentID,String a0000){
		   String mess = "ok";
		   RowSet rs = null;
		   Connection sconn = null;
		   try
		   {	
			   String parent_id = parentID;
	    	   
			   sconn = AdminDb.getConnection();
			   ContentDAO dao = new ContentDAO(sconn);
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			   if(parentID==null || parentID.trim().length()<=0 || "".equals(parentID)){
				   if(!"UN".equalsIgnoreCase(type)) {
                       return "根节点下不能建部门和岗位!";
                   }
			   }
			    
			   
			   String parentCodesetid = "UN";
			   String parentCodeitemid = "";
			   int  parentGrade = 0;
			   String maxChild = "";
			   String codeitemid = "";
			   Map parentInfo = new HashMap();
			   AddOrgInfo aoi= new AddOrgInfo();
			   
			   if(parentID==null || parentID.trim().length()<=0 || "".equals(parentID)){
				   parentCodesetid = "UN";
				   parentGrade = 0;
				   maxChild = getRootMaxChild(dao,rs);
				   codeitemid = aoi.GetNext(maxChild,"");
				   parentCodeitemid = codeitemid;
			   }else{
				   parentInfo = getOrgID(type,parent_id,dao,rs);
				   if(parentInfo==null || parentInfo.size()<=0) {
                       return "请先同步上一级！";
                   }
				   parentCodesetid = parentInfo.get("parentCodesetid").toString();
				   parentCodeitemid = parentInfo.get("parentCodeitemid").toString();
				   parentGrade = Integer.parseInt(parentInfo.get("parentGrade").toString());
				   maxChild = (String) parentInfo.get("maxChild");
				   codeitemid = parentCodeitemid+aoi.GetNext(maxChild, parentCodeitemid);
			   }
				   
			   
			   if("UN".equalsIgnoreCase(type) && "UM".equalsIgnoreCase(parentCodesetid)) {
                   return "部门下不能建单位!";
               }
			      
			   //新建机构
			   StringBuffer sqlstr = new StringBuffer();
			   sqlstr.append(" insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,grade,start_date,end_date,corCode,a0000) ");
			   sqlstr.append(" values(?,?,?,?,?,?,?,?,?,?)");
			   ArrayList dateList = new ArrayList();
//			   dateList.add(type);
//			   dateList.add(codeitemid);
//			   dateList.add(orgName);
//			   dateList.add(parentCodeitemid);
//			   dateList.add(codeitemid);
//			   dateList.add(parentGrade+1+"");
//			   dateList.add(getDate(sdf.format(new Date())));
//			   dateList.add(getDate("9999-12-31"));
//			   dateList.add(corCode);
//			   dateList.add(a0000+"");
			   
//			   dao.insert(sqlstr.toString(), dateList);
			   
			   RecordVo vo = new RecordVo("organization");
			   vo.setString("codesetid", type);
			   vo.setString("codeitemid", codeitemid);
			   vo.setString("codeitemdesc", orgName);
			   vo.setString("parentid", parentCodeitemid);
			   vo.setString("childid", codeitemid);
			   vo.setInt("grade", parentGrade+1);
			   vo.setDate("start_date", new Date());
			   vo.setDate("end_date", DateUtils.getDate("9999-12-31", "yyyy-MM-dd"));
			   vo.setString("corcode", corCode);
			   vo.setInt("a0000", Integer.parseInt(a0000));
			   
			   dao.addValueObject(vo);
			   
			   sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			   String sql = "";
			   //初始化主集信息
			   if("@K".equalsIgnoreCase(type)) {
                   sql = "insert into k01(e01a1,e0122,CreateTime,ModTime) values('"+codeitemid+"',"+parentInfo.get("parentCodeitemid")+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+")";
               } else {
                   sql = "insert into b01(b0110,CreateTime,ModTime) values('"+codeitemid+"',"
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+","
                                 +Sql_switcher.dateValue(sdf.format(new Date()))+") ";
               }
			   dao.update(sql);
			   
			   // 更新父节点的子节点
			   sql = " update organization set childid='"+codeitemid+"' where codeitemid='"+parentCodeitemid+"'";
			   dao.update(sql);	
			   
			   
			   RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD", sconn);
				if(unit_code_field_constant_vo!=null)
				{
				  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
				  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
				  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){

					sql = "update B01 set "+unit_code_field+"='"+corCode+"',modtime="+Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))+"  where b0110='"+codeitemid+"'";
					  dao.update(sql);
				  }
				}
			   
		   }catch(Exception e){
			   e.printStackTrace();
			   System.out.println("SyncOrgInfo.class Error......method>>insert");
			   mess = "新增失败";
		   }finally{
			   try{
				   if(sconn!=null) {
                       sconn.close();
                   }
				   if(rs!=null){
					   rs.close();
				   }
			   }catch(Exception e){
				   e.printStackTrace();
			   }
		   }
		   
		   return mess;
	   }
	   
	   
	   /**
	    * 
	    * @param type 
	    * @param orgName
	    * @param corCode
	    * @param parentID
	    * @param a0000  如果不修改则传空值
	    * @param bolishFlag  撤销标示 true撤销/////false修改
	    * @return message, success:ok
	    */
	   public String update(String type,String orgName,String corCode,String parentID,String unitID,String a0000,boolean bolishFlag,String canceldate)
	   {
		   String mess = "ok";
		   try
		   {
			   String parent_id = parentID;
			   if(parentID==null || parentID.trim().length()<=0 || "0".equalsIgnoreCase(parentID)) {
                   parentID = unitID;
               }
			   
			   conn = AdminDb.getConnection();
			   frowset = null;
			   ContentDAO dao = new ContentDAO(conn);
			   String codeitemid = "";
			   String parentid = "";
			   String end_date = "";
		       String sql = "select codeitemid,codesetid,parentid,end_date from organization where corCode = '"+corCode+"' and codesetid='"+type+"'";
		       frowset = dao.search(sql);
		       if(frowset.next()){
		    	   codeitemid = frowset.getString("codeitemid");
		           parentid = frowset.getString("parentid");
		           end_date = DateStyle.dateformat(frowset.getDate("end_date"),"yyyy-MM-dd");
		       }else {
                   return "未找到代码为"+corCode+"的机构";
               }
		       
		       if(bolishFlag){
		    	   bolishOrg(codeitemid,dao,canceldate);
		       }else
		       {
		    	   Map parentInfo = getOrgID(type,parent_id,unitID,dao,frowset);
		    	   
		    	   String str = (String) parentInfo.get("parentCodeitemid");
		    	   
		    	   
		    	   if (str == null) {
		    		   System.out.println("type:" + type + "parent_id:" + parent_id + " unitId:" + unitID);
		    	   }
		    	   
		    	   if(parentID==null || parentID.trim().length()<=0 || parentid.equalsIgnoreCase(str))
		    	   {
		    		   StringBuffer sqlstr = new StringBuffer();
		    		   sqlstr.append(" update organization set codeitemdesc='"+orgName+"' ,end_date="+Sql_switcher.dateValue("9999-12-31"));
		    		   if(a0000!=null &&a0000.trim().length()>0) {
                           sqlstr.append(" , a0000='"+a0000+"'");
                       }
		    		   sqlstr.append(" where codeitemid='"+codeitemid+"'");
		    		   dao.update(sqlstr.toString());
		    	   }else{
		    		   RecordVo vo = new RecordVo("organization");
		    		   vo.setString("codeitemid", codeitemid);
		    		   vo.setString("codesetid",type);
		    		   vo = dao.findByPrimaryKey(vo);
		    		   ArrayList list = new ArrayList();
		    		   list.add(vo);
		    		   transferOrg(conn,frowset,parentInfo.get("parentCodeitemid").toString(),list,end_date);
		    		   for(int i=0;i<resultlist.size();i++){
		    			   String neworgid = resultlist.get(i).toString();
		    			   StringBuffer sqlstr = new StringBuffer();
		    			   sqlstr.append(" update organization set codeitemdesc='"+orgName+"' ,end_date="+Sql_switcher.dateValue("9999-12-31"));
			    		   if(a0000!=null &&a0000.trim().length()>0) {
                               sqlstr.append(" , a0000='"+a0000+"' ");
                           }
			    		   sqlstr.append(" where codeitemid='"+neworgid+"'");
		    			   dao.update(sqlstr.toString());
		    		   }
		    	   }
		       }
		       
		    }catch(Exception e){
		    	e.printStackTrace();
		    	mess = "更新失败";
		    }finally{
		    	try{
					   if(conn!=null) {
                           conn.close();
                       }
					   if(frowset!=null) {
                           frowset.close();
                       }
				   }catch(Exception e){
					   e.printStackTrace();
				   }
		    }
		    return mess;
	   }
	   
	   
	   /**
	    * 
	    * @param type 
	    * @param orgName
	    * @param corCode
	    * @param parentID
	    * @param a0000  如果不修改则传空值
	    * @param bolishFlag  撤销标示 true撤销/////false修改
	    * @return message, success:ok
	    */
	   public String update(String type,String orgName,String corCode,String parentID,String a0000,boolean bolishFlag,String canceldate)
	   {
		   String mess = "ok";
		   try
		   {
			   String parent_id = parentID;
			   
			   conn = AdminDb.getConnection();
			   frowset = null;
			   ContentDAO dao = new ContentDAO(conn);
			   String codeitemid = "";
			   String parentid = "";
			   String end_date = "";
		       String sql = "select codeitemid,codesetid,parentid,end_date from organization where corCode = '"+corCode+"' and codesetid='"+type+"'";
		       frowset = dao.search(sql);
		       if(frowset.next()){
		    	   codeitemid = frowset.getString("codeitemid");
		           parentid = frowset.getString("parentid");
		           end_date = DateStyle.dateformat(frowset.getDate("end_date"),"yyyy-MM-dd");
		       }else {
                   return "未找到代码为"+corCode+"的机构";
               }
		       
		       if(bolishFlag){
		    	   bolishOrg(codeitemid,dao,canceldate);
		       }else
		       {
		    	   Map parentInfo = getOrgID(type,parent_id,dao,frowset);
		    	   if(parentID==null || parentID.trim().length()<=0 || parentid.equalsIgnoreCase(parentInfo.get("parentCodeitemid").toString()))
		    	   {
		    		   StringBuffer sqlstr = new StringBuffer();
		    		   sqlstr.append(" update organization set codeitemdesc='"+orgName+"' ,end_date="+Sql_switcher.dateValue("9999-12-31"));
		    		   if(a0000!=null &&a0000.trim().length()>0) {
                           sqlstr.append(" , a0000='"+a0000+"'");
                       }
		    		   sqlstr.append(" where codeitemid='"+codeitemid+"'");
		    		   dao.update(sqlstr.toString());
		    	   }else{
		    		   RecordVo vo = new RecordVo("organization");
		    		   vo.setString("codeitemid", codeitemid);
		    		   vo.setString("codesetid",type);
		    		   vo = dao.findByPrimaryKey(vo);
		    		   ArrayList list = new ArrayList();
		    		   list.add(vo);
		    		   transferOrg(conn,frowset,parentInfo.get("parentCodeitemid").toString(),list,end_date);
		    		   for(int i=0;i<resultlist.size();i++){
		    			   String neworgid = resultlist.get(i).toString();
		    			   StringBuffer sqlstr = new StringBuffer();
		    			   sqlstr.append(" update organization set codeitemdesc='"+orgName+"' ,end_date="+Sql_switcher.dateValue("9999-12-31"));
			    		   if(a0000!=null &&a0000.trim().length()>0) {
                               sqlstr.append(" , a0000='"+a0000+"' ");
                           }
			    		   sqlstr.append(" where codeitemid='"+neworgid+"'");
		    			   dao.update(sqlstr.toString());
		    		   }
		    	   }
		       }
		       
		    }catch(Exception e){
		    	e.printStackTrace();
		    	mess = "更新失败";
		    }finally{
		    	try{
					   if(conn!=null) {
                           conn.close();
                       }
					   if(frowset!=null) {
                           frowset.close();
                       }
				   }catch(Exception e){
					   e.printStackTrace();
				   }
		    }
		    return mess;
	   }
	   
	   /**
	    * deleteOrg
	    * @param corCode 
	    * @return message, success:ok
	    */
	   public String delete(String corCode,String type){
		   String mess = "ok";
		   RowSet rs = null;
		   Connection sconn = null;
		   Statement sta = null;
		   Savepoint sp = null;
		   DbSecurityImpl dbS = new DbSecurityImpl();
		   try{
			   sconn = AdminDb.getConnection();
			   sconn.setAutoCommit(false);
			   sp = sconn.setSavepoint();			   
			   ContentDAO dao = new ContentDAO(sconn);
			   String codeitemid = "";
			   String codesetid = "";
			   String keyItem = "";
			   String sql = " select codeitemid,codesetid from organization where corCode='"+corCode+"' and codesetid='"+type+"'";
			   rs = dao.search(sql.toString());
			   if(rs.next()){
				   codeitemid = rs.getString("codeitemid");
			       codesetid = rs.getString("codesetid");
			   }else{
				   return  "未找到代码为"+corCode+"的机构";
			   }
			   ArrayList fieldset = new ArrayList();
			   if("@K".equalsIgnoreCase(codesetid)){
				   fieldset = DataDictionary.getFieldSetList(Constant.POS_FIELD_SET, Constant.ALL_FIELD_SET);
			       keyItem = "e01a1";
			   }else{
				   fieldset = DataDictionary.getFieldSetList(Constant.UNIT_FIELD_SET, Constant.ALL_FIELD_SET);
				   keyItem = "b0110";
			   }
			   
			   sta = sconn.createStatement();
			   for(int i=0;i<fieldset.size();i++){
				   FieldSet fs = (FieldSet)fieldset.get(i);
				   sql = " delete "+fs.getFieldsetid()+" where "+keyItem+" = '"+codeitemid+"'";
				   sta.addBatch(sql);
			   }
			   
			   sql = " delete organization where codeitemid = '"+codeitemid+"'";
			   sta.addBatch(sql);
			   dbS.open(conn, sql);
			   sta.executeBatch();
			   
			   sconn.commit();
			   
		   }catch(Exception e){
			   e.printStackTrace();
			   System.out.println("SyncOrgInfo.class Error......method>>delete");
			    try {
				   sconn.rollback(sp);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			  mess ="删除失败";
		   }finally{
			   try {
					// 关闭Wallet
					dbS.close(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			   try{
				   if(sconn!=null) {
                       sconn.close();
                   }
				   if(rs!=null) {
                       rs.close();
                   }
				   if(sta!=null) {
                       sta.close();
                   }
			   }catch(Exception e){
				   e.printStackTrace();
			   }
		   }
		   return mess;
	   }
	   
	   private Map getOrgID(String type,String corCode,String unitID,ContentDAO dao,RowSet rs)
	   {
		   Map info = new HashMap();
		   try
		   {
			   StringBuffer sql = new StringBuffer();
			   if(type!=null && type.trim().length()>0 && "UM".equalsIgnoreCase(type))
			   {
				   sql.append("select codeitemid,codesetid,grade,(select MAX(codeitemid) from ");
				   sql.append(" (select codeitemid,parentid from organization  union all ");
				   sql.append(" select codeitemid,parentid from vorganization ) a where parentid=ORG.codeitemid) maxChild ");
				   
				   if(corCode==null || corCode.trim().length()<=0 || "0".equalsIgnoreCase(corCode)) {
                       sql.append(" from organization ORG where corCode='"+unitID+"' and codesetid = 'UN' ");
                   } else
				   {
					   sql.append(" from organization ORG where corCode='"+corCode+"' ");
				   }
				   sql.append(" order by codesetid desc ");
			   } else {
				   sql.append("select codeitemid,codesetid,grade,b.maxChild from organization c left join (");
					        
						   sql.append("select  max(codeitemid) maxChild,parentid from ("); 
						   sql.append("select codeitemid,parentid from organization ");
						   sql.append(" union all ");
						   sql.append("select codeitemid,parentid from vorganization ) a group by  parentid");
					        
					        
					        
						   sql.append(") b on b.parentid=c.codeitemid where ");
						   
						   if(corCode==null || corCode.trim().length()<=0 || "0".equalsIgnoreCase(corCode)) {
                               sql.append("  corCode='"+unitID+"' and codesetid = 'UN' ");
                           } else {
                               sql.append("  corCode='"+corCode+"' and codesetid = 'UM' ");
                           }
						   
						   sql.append(" order by codesetid desc ");
			   }
			   rs = dao.search(sql.toString());
			   if (rs.next())
			   {
				//   if(type!=null && type.trim().length()>0 && type.equalsIgnoreCase("UN"))
				//   {
					   
				//   }
				   info.put("parentCodeitemid",rs.getString("codeitemid"));
				   info.put("parentCodesetid",rs.getString("codesetid"));
				   info.put("parentGrade",rs.getString("grade"));
				   info.put("maxChild", rs.getString("maxChild"));
			   }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return info;
	   }
	   
	   private Map getOrgID(String type,String corCode,ContentDAO dao,RowSet rs)
	   {
		   Map info = new HashMap();
		   try
		   {
			   StringBuffer sql = new StringBuffer();
			   if(Sql_switcher.searchDbServer() == 1) {
				   sql.append("select codeitemid,codesetid,grade,(select MAX(codeitemid) from ");
				   sql.append(" (select codeitemid from organization where parentid=ORG.codeitemid union all ");
				   sql.append(" select codeitemid from vorganization where parentid=ORG.codeitemid ) a) maxChild ");
				   if(type!=null && type.trim().length()>0 && "UM".equalsIgnoreCase(type))
				   {
					   
						   sql.append(" from organization ORG where corCode='"+corCode+"' ");
				   }
				   else
				   {
					   sql.append(" from organization ORG where corCode='"+corCode+"' ");
				   }
				   sql.append(" order by codesetid desc ");
			   } else {
				   sql.append("select codeitemid,codesetid,grade,b.maxChild from organization c left join (");
			        
				   sql.append("select  max(codeitemid) maxChild,parentid from ("); 
				   sql.append("select codeitemid,parentid from organization ");
				   sql.append(" union all ");
				   sql.append("select codeitemid,parentid from vorganization ) a group by  parentid");
			        
			        
			        
				   sql.append(") b on b.parentid=c.codeitemid where ");
				   
				   
					   sql.append("  corCode='"+corCode+"' ");
				   
				   sql.append(" order by codesetid desc ");
				   
			   }
			   rs = dao.search(sql.toString());
			   if (rs.next())
			   {
				//   if(type!=null && type.trim().length()>0 && type.equalsIgnoreCase("UN"))
				//   {
					   
				//   }
				   info.put("parentCodeitemid",rs.getString("codeitemid"));
				   info.put("parentCodesetid",rs.getString("codesetid"));
				   info.put("parentGrade",rs.getString("grade"));
				   info.put("maxChild", rs.getString("maxChild"));
			   }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return info;
	   }
	   
	   private String getRootMaxChild(ContentDAO dao, RowSet rs){
		   String codeitemid="";
		   String sql = " select max(codeitemid) maxChild from (select codeitemid from organization where parentid=codeitemid union all "+
			            " select codeitemid from vorganization where parentid=codeitemid ) a";
		   try{
			   rs = dao.search(sql);
			   if(rs.next()) {
                   codeitemid = rs.getString("maxChild");
               }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return codeitemid;
	   }
	   
	   private String getDate(String dateStr){
		   String rev=dateStr;
		   try{
		          if (Sql_switcher.searchDbServer()== Constant.MSSQL) {
                      rev = dateStr;
                  } else {
                      rev =   "TO_DATE('" + dateStr + "', 'YYYY-MM-DD')";
                  }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return rev;
	   }
	   
	   private void bolishOrg(String codeitemid,ContentDAO dao,String canceldate) throws SQLException
	   {
		   try
		   {
			   Date date = new Date();
		       date.setDate(date.getDate()-1);		       
		       if(canceldate!=null && canceldate.trim().length()>0)
		       {
			       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			       date = sdf.parse(canceldate);
		       }		       
		       String sql = " update organization set end_date="+Sql_switcher.dateValue(DateStyle.dateformat(date, "yyyy-MM-dd"))+" where codeitemid like '"+codeitemid+"'";
		       dao.update(sql);
		       
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   
	   ArrayList msgb0110 = new ArrayList();
	   private void transferOrg(Connection conn,RowSet rs,String transfercodeitemid,ArrayList delorglist,String orgEndDate){
		   //if(this.getUserView().getVersion()>=50){
			//	version = true;
			//}
		   ContentDAO dao = new ContentDAO(conn);
			transfercodeitemid=transfercodeitemid.toUpperCase();
			 checkorg(dao);
			if(delorglist==null||delorglist.size()==0) {
                return;
            }
	        try
	        {
	        	
	        	//msgb0110.add(transfercodeitemid);
	        	String tarcodesetid="";
	        	rs=dao.search("select codesetid from organization where codeitemid='" + transfercodeitemid + "'");
	            if(rs.next())
	            {
	            	tarcodesetid=rs.getString("codesetid");
	            }
	        	ArrayList combineorg=new ArrayList();
	        	ArrayList codelist = new ArrayList();
	        	ArrayList peopleOrgList = new ArrayList();//人员变动前的机构 xuj 2010-4-28
	        	//System.out.println("------GetNextId----->");
	            for(int i=0;i<delorglist.size();i++){
	      	        RecordVo vo=(RecordVo)delorglist.get(i);
	      	        OrganizationView orgview=new OrganizationView();
		    		orgview.setCodesetid(vo.getString("codesetid"));
		    		orgview.setCodeitemid(vo.getString("codeitemid"));
		    		if(transfercodeitemid.equals(vo.getString("codeitemid")) || transfercodeitemid.equals(vo.getString("parentid"))) {
                        throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.notransself"),"",""));
                    }
		    		if("@K".equalsIgnoreCase(vo.getString("codesetid"))&& "UN".equalsIgnoreCase(tarcodesetid)) {
                        throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.kknottoun"),"",""));
                    }
		    		combineorg.add(orgview);
		    		peopleOrgList.add(orgview);
		    		codelist.add(vo.getString("codesetid")+vo.getString("codeitemid"));
		    		msgb0110.add(vo.getString("codeitemid"));
	      	     }
	                peopleOrgChange("transfer",peopleOrgList,conn,rs);
	                doTransfer(combineorg,transfercodeitemid,tarcodesetid,orgEndDate);
	                checkorg(dao);
	           }catch(Exception sqle){
			    
			       sqle.printStackTrace();
			   }
	   }
	        
	        private void  checkorg(ContentDAO dao)
	    	{
	    		 StringBuffer sql =new StringBuffer();
	    		 try{
	    			 sql.delete(0,sql.length());
	    		     sql.append("UPDATE ");
	    		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
	    		     sql.append("organization d");
	    		     sql.append(" WHERE d.parentid = ");
	    			 sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid and d.codesetid=organization.codesetid)");
	    		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
	    		     sql.append("organization c");
	    		     sql.append(" WHERE c.parentid = ");
	    		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid and c.codesetid=organization.codesetid)");
	    		 //  System.out.println(sql.toString());
	    		     dao.update(sql.toString());
	    		     //清除掉没有子节点childid不正确的
	    		     StringBuffer updateParentcode=new StringBuffer();
	    	     		updateParentcode.delete(0,updateParentcode.length());
	    	     		updateParentcode.append("UPDATE ");
	    	     		updateParentcode.append("organization SET childid =codeitemid  ");
	    	     		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
	    	     		updateParentcode.append("organization c");
	    	     		updateParentcode.append(" WHERE c.parentid = ");
	    	     		updateParentcode.append("organization.codeitemid and c.parentid<>c.codeitemid) and organization.childid <> organization.codeitemid");
	    	           // System.out.println(updateParentcode.toString());
	    			     dao.update(updateParentcode.toString());
	    	     }catch(Exception e)
	    	     {
	    	    	 e.printStackTrace();
	    	     }
	    	}  
	        
	        private void peopleOrgChange(String peopleOrg,ArrayList peopleOrgList,Connection conn,RowSet rs) throws GeneralException{
	    		try{
	    			if (peopleOrg == null || "".equals(peopleOrg)
	    					|| peopleOrgList == null || peopleOrgList.size() == 0) {
	    				return;
	    			}
	    			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
	    			String tempid = "";
	    			if ("combine".equalsIgnoreCase(peopleOrg)) {
	    				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
	    						"combine");
	    				if (tempid == null || "".equals(tempid)) {
                            return;
                        }
	    			} else if ("transfer".equalsIgnoreCase(peopleOrg)) {
	    				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
	    						"transfer");
	    				if (tempid == null || "".equals(tempid)) {
                            return;
                        }
	    			} else if ("bolish".equalsIgnoreCase(peopleOrg)) {
	    				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
	    						"bolish");
	    				if (tempid == null || "".equals(tempid)) {
                            return;
                        }
	    			}
	    			StringBuffer sql = new StringBuffer();
	    			ArrayList dblist = DataDictionary.getDbpreList();
	    			ContentDAO dao = new ContentDAO(conn);
	    			int nyear = 0;
	    			int nmonth = 0;
	    			nyear = DateUtils.getYear(new Date());
	    			nmonth = DateUtils.getMonth(new Date());
	    			RecordVo vo = new RecordVo("tmessage");
	    			vo.setString("username", "");
	    			vo.setInt("state", 0);
	    			vo.setInt("nyear", nyear);
	    			vo.setInt("nmonth", nmonth);
	    			vo.setInt("type", 0);
	    			vo.setInt("flag", 0);
	    			vo.setInt("sourcetempid", 0);
	    			vo.setInt("noticetempid", Integer.parseInt(tempid));
	    			StringBuffer changepre = new StringBuffer();
	    			StringBuffer change = new StringBuffer();
	    			for (int i = 0; i < peopleOrgList.size(); i++) {
	    				OrganizationView orgview = (OrganizationView) peopleOrgList
	    						.get(i);
	    				String codesetid = orgview.getCodesetid();
	    				String codeitemid = orgview.getCodeitemid();
	    				for (int n = 0; n < dblist.size(); n++) {
	    					String pre = (String) dblist.get(n);
	    					sql.setLength(0);
	    					sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
	    							+ pre + "A01 where ");
	    					if ("UN".equalsIgnoreCase(codesetid)) {
	    						sql.append("b0110 like '" + codeitemid + "%'");
	    					} else if ("UM".equalsIgnoreCase(codesetid)) {
	    						sql.append("e0122 like '" + codeitemid + "%'");
	    					} else if ("@K".equalsIgnoreCase(codesetid)) {
	    						sql.append("e01a1 ='" + codeitemid + "'");
	    					}
	    					rs = dao.search(sql.toString());
	    					vo.setString("db_type", pre);
	    					while (rs.next()) {
	    						String a0100 = rs.getString("a0100");
	    						String a0101 = rs.getString("a0101");
	    						a0101 = a0101 != null ? a0101 : "";
	    						String b0110 = rs.getString("b0110");
	    						String e0122 = rs.getString("e0122");
	    						String e01a1 = rs.getString("e01a1");
	    						vo.setString("a0100", a0100);
	    						vo.setString("a0101", a0101);
	    						changepre.setLength(0);
	    						change.setLength(0);
	    						if (b0110 != null && !"".equals(b0110)) {
	    							changepre.append("B0110=" + b0110 + ",");
	    							change.append("B0110,");
	    						}
	    						if (e0122 != null && !"".equals(e0122)) {
	    							changepre.append("E0122=" + e0122 + ",");
	    							change.append("E0122,");
	    						}
	    						if (e01a1 != null && !"".equals(e01a1)) {
	    							changepre.append("E01A1=" + e01a1 + ",");
	    							change.append("E01A1,");
	    						}
	    						if (a0101 != null && !"".equals(a0101)) {
	    							changepre.append("A0101=" + a0101 + ",");
	    							change.append("A0101,");
	    						}
	    						vo.setString("changepre", changepre.toString());
	    						vo.setString("change", change.toString());
	    						/** max id access mssql此字段是自增长类型 */
	    						if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
	    							int nid = DbNameBo.getPrimaryKey("tmessage", "id",
	    									conn);
	    							vo.setInt("id", nid);
	    						}
	    						dao.addValueObject(vo);
	    					}
	    				}
	    			}
	    		}catch(Exception e){
	    			throw GeneralExceptionHandler.Handle(e);
	    		}
	    	}
	        private void doTransfer(ArrayList combineorg,String transfercodeitemid,String targetset,String orgEndDate) throws GeneralException
	    	{
	        	ArrayList newidlist = new ArrayList();
	    		String codeitemid="";
	    		String childid="";
	    		String Level="";
	    		boolean ishavechild=false;
	    		StringBuffer sqlstr=new StringBuffer();
	    	    ContentDAO dao=new ContentDAO(this.conn);	  
	    	    try{
	    	    	sqlstr.delete(0,sqlstr.length());
	    			sqlstr.append("select codesetid,codeitemid,parentid,childid,grade from organization where codeitemid='");
	    			sqlstr.append(transfercodeitemid);
	    			sqlstr.append("'");
	    			sqlstr.append(" union select codesetid,codeitemid,parentid,childid,grade from vorganization where codeitemid='");
	    			sqlstr.append(transfercodeitemid);
	    			sqlstr.append("'");
	    		    this.frowset=dao.search(sqlstr.toString());      //父结点的信息
	    			 if(this.frowset.next())
	    			 {
	    			 	codeitemid=this.frowset.getString("codeitemid");
	    			 	childid=this.frowset.getString("childid");
	    			 	if(codeitemid.equals(childid)) {
                            ishavechild=false;
                        } else {
                            ishavechild=true;
                        }
	    			 	Level=String.valueOf(Integer.parseInt(this.frowset.getString("grade")));
	    			 } 
	    			ArrayList dblist=DataDictionary.getDbpreList();
	    			String gradeori="0";
	    			
	    			
	    			RecordVo vo = new RecordVo("organization");
	    			boolean flag = vo.hasAttribute("guidkey");
	    			
	    			for(int i=0;i<combineorg.size();i++)
	    			{
	    				OrganizationView orgview=(OrganizationView)combineorg.get(i);
	    				updateA0000_transfer(orgview.getCodeitemid(),transfercodeitemid);   
	    				//System.out.println("fasdfdsf" + transfercodeitemid + "F" +ishavechild);
	    				String GetNextId=GetNextId(dao,childid,transfercodeitemid,ishavechild,Level);
	    				childid=GetNextId;
	    				newidlist.add(GetNextId);
	    				msgb0110.add(GetNextId);
	    				//System.out.println("------GetNextId----->" + GetNextId);
	    				
	    				 this.frowset=dao.search("select grade from organization where codeitemid='" + orgview.getCodeitemid() + "'");      //父结点的信息
	    				 if(this.frowset.next())
	    				 {
	    					 gradeori=String.valueOf(Integer.parseInt(this.frowset.getString("grade")));
	    				 } 
	    				
	    				
	    				String temptable="t#syncorg_hr_org_t";
	    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    				
	    				sqlstr.delete(0,sqlstr.length());
	    				sqlstr.append("update organization set codeitemid='");
	    				sqlstr.append(GetNextId);
	    				sqlstr.append("',parentid='");
	    				sqlstr.append(transfercodeitemid);
	    				sqlstr.append("',childid='");
	    				sqlstr.append(GetNextId);
	    				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("childid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("childid") + "-" + orgview.getCodeitemid().length()));
	    				sqlstr.append(",grade= grade + 1 - ");
	    				sqlstr.append(gradeori);
	    				sqlstr.append(" + ");
	    				sqlstr.append(Level);
	    				sqlstr.append(",start_date="+Sql_switcher.dateValue(sdf.format(new Date()))+",end_date="+Sql_switcher.dateValue("9999-12-31"));
	    				sqlstr.append(" where codeitemid='");
	    				sqlstr.append(orgview.getCodeitemid());
	    				sqlstr.append("'");
	    				//System.out.println(sqlstr.toString());
	    				dao.update(sqlstr.toString());
	    				
	    				sqlstr.delete(0,sqlstr.length());
	    				sqlstr.append("update organization set codeitemid='");
	    				sqlstr.append(GetNextId);
	    				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("codeitemid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("codeitemid") + "-" + orgview.getCodeitemid().length()));
	    				sqlstr.append(",parentid='");				
	    				sqlstr.append(GetNextId);
	    				sqlstr.append("'" + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("parentid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("parentid") + "-" + orgview.getCodeitemid().length()));
	    				sqlstr.append(",childid='");
	    				sqlstr.append(GetNextId);
	    				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("childid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("childid") + "-" + orgview.getCodeitemid().length()));
	    				sqlstr.append(",grade= grade + 1 - ");
	    				sqlstr.append(gradeori);
	    				sqlstr.append(" + ");
	    				sqlstr.append(Level);
	    				sqlstr.append(",start_date="+Sql_switcher.dateValue(sdf.format(new Date()))+",end_date="+Sql_switcher.dateValue("9999-12-31"));
	    				sqlstr.append(" where codeitemid<>'");
	    				sqlstr.append(orgview.getCodeitemid());
	    				sqlstr.append("' and codeitemid like '");
	    				sqlstr.append(orgview.getCodeitemid());
	    				sqlstr.append("%' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
	    				dao.update(sqlstr.toString());
	    				
	    				
	    				
	    				if(!ishavechild)
	    				{
	    					sqlstr.delete(0,sqlstr.length());
	    					sqlstr.append("update organization set childid='");
	    					sqlstr.append(GetNextId);
	    					sqlstr.append("' where codeitemid='");
	    					sqlstr.append(transfercodeitemid);
	    					sqlstr.append("'");
	    					childid=GetNextId;
	    					ishavechild=true;
	    					//System.out.println(sqlstr.toString());
	    					dao.update(sqlstr.toString());
	    				}	
	    				for(int j=0;j<dblist.size();j++)
	    				{
	    					if("UN".equalsIgnoreCase(orgview.getCodesetid()))
	    					{
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("B0110='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("B0110",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("B0110") + "-" + orgview.getCodeitemid().length()));

	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where B0110 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%'");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E0122='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E0122",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E0122") + "-" + orgview.getCodeitemid().length()));
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E0122 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%'");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E01A1='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E01A1",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E01A1")  + "-" + orgview.getCodeitemid().length())) ;
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E01A1 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%'");
	    						dao.update(sqlstr.toString());	
	    						
	    					}else if("UM".equalsIgnoreCase(orgview.getCodesetid()))
	    					{
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("B0110=");
	    						if("UN".equalsIgnoreCase(targetset))
	    						{
	    						   sqlstr.append("'");
	    						   sqlstr.append(transfercodeitemid);	
	    						   sqlstr.append("'");
	    						}else if("UM".equalsIgnoreCase(targetset))
	    						{
	    							sqlstr.append("'");
	    							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
	    							sqlstr.append("'");
	    						}
	    						
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("', 1, LEN('");
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("') - (LEN(E0122) - LEN(B0110))) ");
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su'");
	    						sqlstr.append(" where E0122 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%' and B0110 IS NOT NULL");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E0122='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E0122",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E0122") + "-" + orgview.getCodeitemid().length()));
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E0122 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%'");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E01A1='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("E01A1",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E01A1") + "-" + orgview.getCodeitemid().length()));
	    						//sqlstr.append(orgview.getCodeitemid().length() +1);
	    						//sqlstr.append(",len(E01A1)-");
	    						//sqlstr.append(orgview.getCodeitemid().length());
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E01A1 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%'");
	    						dao.update(sqlstr.toString());	
	    						
	    					}else
	    					{
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("B0110=");
	    						if("UN".equalsIgnoreCase(targetset))
	    						{
	    						   sqlstr.append("'");
	    						   sqlstr.append(transfercodeitemid);	
	    						   sqlstr.append("'");
	    						}else if("UM".equalsIgnoreCase(targetset))
	    						{
	    							sqlstr.append("'");
	    							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
	    							sqlstr.append("'");
	    						}
	    						//sqlstr.append(Sql_switcher.substr("'" + GetNextId + "'","1",Sql_switcher.length("'" + GetNextId + "'") +"-(" + Sql_switcher.length("E01A1") + "-" + Sql_switcher.length("B0110") + ")" ));						
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("', 1, LEN('");
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("') - (LEN(E01A1) - LEN(B0110))) ");
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E01A1 like '");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("%' and B0110 IS NOT NULL");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E0122=");
	    						sqlstr.append("'");
	    						sqlstr.append(transfercodeitemid);	
	    						sqlstr.append("'");
	    						
	    						//sqlstr.append(Sql_switcher.substr("'" +GetNextId + "'","1",Sql_switcher.length("'" + GetNextId +  "'") + "-(" + Sql_switcher.length("E01A1") + "-" + Sql_switcher.length("E0122") +")"));
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("', 1, LEN('");
	    						//sqlstr.append(GetNextId);
	    						//sqlstr.append("') - (LEN(E01A1) - LEN(E0122))) ");
	    						sqlstr.append(",modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E01A1='");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("' and E0122 IS NOT NULL");
	    						dao.update(sqlstr.toString());	
	    						sqlstr.delete(0,sqlstr.length());
	    						sqlstr.append("update ");
	    						sqlstr.append(dblist.get(j));
	    						sqlstr.append("A01 set ");
	    						sqlstr.append("E01A1='");
	    						sqlstr.append(GetNextId);
	    						sqlstr.append("',modtime=");
	    						sqlstr.append(PubFunc.DoFormatSystemDate(false));
	    						sqlstr.append(",modusername='su' ");
	    						sqlstr.append(" where E01A1 ='");
	    						sqlstr.append(orgview.getCodeitemid());
	    						sqlstr.append("'");
	    						dao.update(sqlstr.toString());						
	    					}
	    				}
	    				SysnK(orgview.getCodeitemid(),GetNextId,dao);
	    			}
	    			initLayer();
	    			this.resultlist = newidlist;
	    		}catch(Exception e){
	    		  e.printStackTrace();
	    		  throw GeneralExceptionHandler.Handle(e);
	    		}	
	    		
	    	}	        	
	        
	        private void initLayer(){
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		StringBuffer sql = new StringBuffer();
	    		try {
	    			sql.append(SetLayerNull("organization"));
	    			dao.update(sql.toString());
	    			sql.delete(0,sql.length());
	    			sql.append(InitLayer("organization"));
	    			dao.update(sql.toString());
	    			sql.delete(0,sql.length());
	    			int i=1;
	    			while(true){
	    				sql.append(NextLayer("organization",i));
	    				int j = dao.update(sql.toString());
	    				if(j==0) {
                            break;
                        }
	    				i++;
	    				sql.delete(0,sql.length());
	    			}
	    			sql.delete(0,sql.length());
	    			sql.append(SetLayerNull("vorganization"));
	    			dao.update(sql.toString());
	    			sql.delete(0,sql.length());
	    			sql.append(InitLayer("vorganization"));
	    			dao.update(sql.toString());
	    			sql.delete(0,sql.length());
	    			i=1;
	    			while(true){
	    				sql.append(NextLayer("vorganization",i));
	    				int j = dao.update(sql.toString());
	    				if(j==0) {
                            break;
                        }
	    				i++;
	    				sql.delete(0,sql.length());
	    			}
	    			
	    		} catch (SQLException e) {
	    			e.printStackTrace();
	    		}
	    	}
	        
	        private void SysnK(String fromcode,String tocode,ContentDAO dao) throws Exception
	    	{
	    		StringBuffer orgsql=new StringBuffer();
	    		ArrayList voList = new ArrayList();
	    		List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    		for(int k=0;k<infoSetList.size();k++)
	    		{
	    			FieldSet fieldset=(FieldSet)infoSetList.get(k);
	    				
	    				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
	    				String sql = "select * from "+fieldset.getFieldsetid()+" where b0110 like '"+fromcode+"%'";
	    				voList = this.getRecordVoList(sql, dao, fieldset.getFieldsetid());
	    				
	    			orgsql.delete(0,orgsql.length());
	    			orgsql.append("update  ");
	    			orgsql.append(fieldset.getFieldsetid());
	    			orgsql.append(" set B0110='");
	    			orgsql.append(tocode);
	    			orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("b0110",String.valueOf(fromcode.length() +1),Sql_switcher.length("b0110")  + "-" + fromcode.length())) ;

	    			orgsql.append(" where b0110 like '");
	    			orgsql.append(fromcode);
	    			orgsql.append("%'");
	    	   		dao.update(orgsql.toString());
	    	   		
	    	   			
	    	   		//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
	    				for(int i=0;i<voList.size();i++){
	    					RecordVo vo = (RecordVo)voList.get(i);
	    					dao.addValueObject(vo);
	    				}
	    				voList.clear();
	    	   			
	    		}
	    		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    		for(int k=0;k<infoSetListPos.size();k++)
	    		{
	    			FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
	    			String temptable = "temp_"+fieldset.getFieldsetid()+"_syncorg";
	    				
	    				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
	    				String sql = "select * from "+fieldset.getFieldsetid()+" where e01a1 like '"+fromcode+"%'";
	    				voList = this.getRecordVoList(sql, dao, fieldset.getFieldsetid());
	    				
	    			if("K01".equalsIgnoreCase(fieldset.getFieldsetid())){
	    				
	    				orgsql.delete(0,orgsql.length());
	    				orgsql.append("update ");
	    				orgsql.append(fieldset.getFieldsetid());
	    				orgsql.append(" set e01a1='");
	    				orgsql.append(tocode);
	    				orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("e01a1",String.valueOf(fromcode.length() +1),Sql_switcher.length("e01a1")  + "-" + fromcode.length())) ;
	    				orgsql.append(" ,e0122='"+getTargetUMCodeitemid(tocode) +"'");
	    				orgsql.append(" where e01a1 like '");
	    				orgsql.append(fromcode);
	    				orgsql.append("%'");
	    			}else{
	    				orgsql.delete(0,orgsql.length());
	    				orgsql.append("update ");
	    				orgsql.append(fieldset.getFieldsetid());
	    				orgsql.append(" set e01a1='");
	    				orgsql.append(tocode);
	    				orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("e01a1",String.valueOf(fromcode.length() +1),Sql_switcher.length("e01a1")  + "-" + fromcode.length())) ;
	    	
	    				orgsql.append(" where e01a1 like '");
	    				orgsql.append(fromcode);
	    				orgsql.append("%'");
	    			}
	    			dao.update(orgsql.toString());
	    			
	    				for(int i=0;i<voList.size();i++){
	    					RecordVo vo = (RecordVo)voList.get(i);
	    					dao.addValueObject(vo);
	    				}
	    				voList.clear();
	           			
	    		}
	    	}
	        
	        private String getTargetUMCodeitemid(String code)
	    	{
	    		String pre="@K";
	    		String uncodeitemid="";
	         	StringBuffer strsql=new StringBuffer();
	         	ResultSet rs = null;
	    		try{
	    			
	    			
	    				ContentDAO db=new ContentDAO(this.conn);
	    				strsql.delete(0,strsql.length());
	    				strsql.append("select codesetid from organization where codeitemid='"+code+"'");
	    				rs = db.search(strsql.toString());
	    				while(rs.next()){
	    					pre=rs.getString("codesetid");
	    					uncodeitemid = code;
	    				}
	    				while("@K".equalsIgnoreCase(pre))
	    				{
	    					strsql.delete(0,strsql.length());
	    					strsql.append("select * from organization");
	    					strsql.append(" where codeitemid='");
	    					strsql.append(code);
	    					strsql.append("'");					
	    					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
	    					if(this.frowset.next())
	    					{
	    						pre=this.frowset.getString("codesetid");
	    						code=this.frowset.getString("parentid");
	    						if("@K".equalsIgnoreCase(pre)) {
                                    uncodeitemid=code;
                                }
	    					}			
	    				}				
	    			 
	    			}catch (SQLException sqle){
	    				sqle.printStackTrace();
	    			}		
	    		return uncodeitemid;
	    	}
	        
	        private String getTargetUNCodeitemid(String code)
	    	{
	    		//System.out.println("pos" + code + kind);
	    		String pre="UM";
//	    	    Connection conn = null;
//	    		Statement stmt = null;
//	    		ResultSet rs=null;
	    		String uncodeitemid="";
	         	StringBuffer strsql=new StringBuffer();
	    		try{
	    			/*if("UN".equals(pre))
	    			{
	    				strsql.append("select * from organization");
	    				strsql.append(" where codeitemid='");
	    				strsql.append(code);
	    				strsql.append("'");		
//	    				conn=;
	    				ContentDAO db=new ContentDAO(this.getFrameconn());
	    				this.frowset =db.search(strsql.toString());	
	    				if(this.frowset.next())
	    				{
	    				    uncodeitemid=this.frowset.getString("codeitemid");
	    				}
	    			}
	    			else*/
	    			{
//	    				conn=this.getFrameconn();
	    				ContentDAO db=new ContentDAO(this.conn);
	    				while(!"UN".equalsIgnoreCase(pre))
	    				{
	    					strsql.delete(0,strsql.length());
	    					strsql.append("select * from organization");
	    					strsql.append(" where codeitemid='");
	    					strsql.append(code);
	    					strsql.append("'");					
	    					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
	    					if(this.frowset.next())
	    					{
	    						pre=this.frowset.getString("codesetid");
	    						code=this.frowset.getString("parentid");
	    						if(!"UN".equalsIgnoreCase(pre)) {
                                    uncodeitemid=code;
                                }
	    					}			
	    				}				
	    			  }
	    			}catch (SQLException sqle){
	    				sqle.printStackTrace();
	    			}		
//	    			finally{
//	    				try{
//	    					if (rs != null){
////	    						rs.close();
//	    					}
//	    					if (stmt != null){
//	    						stmt.close();
//	    					}				
//	    				}catch (SQLException sql){
//	    					sql.printStackTrace();
//	    				}
//	    			}

	    		return uncodeitemid;
	    	}
	        
	        private void addCodeitem(String combineorg,ContentDAO dao) throws Exception 
	    	{
	    		try{
	    			this.frowset=dao.search("select * from organization where codeitemid like '" + combineorg + "%'");
	    			while(this.frowset.next())
	    			{
	    				CodeItem item=new CodeItem();
	    				item.setCodeid(this.frowset.getString("codesetid"));
	    				item.setCodename(this.frowset.getString("codeitemdesc"));
	    				item.setPcodeitem(this.frowset.getString("parentid"));
	    				item.setCcodeitem(this.frowset.getString("childid"));
	    				item.setCodeitem(this.frowset.getString("codeitemid"));
	    				item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
	    				AdminCode.addCodeItem(item);
	    				//AdminCode.updateCodeItemDesc(this.frowset.getString("codesetid"),this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
	    			}
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			throw GeneralExceptionHandler.Handle(e);
	    		}
	    	}
	        
	        private void updateA0000_transfer(String codeitemid,String destOrgId) throws GeneralException
	    	{
	    		//for(int i=0;i<transferorg.size();i++)
	    		//{
	    			//OrganizationView orgview=(OrganizationView)transferorg.get(i);
	    			//String ss=orgview.getCodeitemid();
	    			 // 计算源节点节点数
	    			int srcChildCount = getOrgChildCount(codeitemid);
	    			 // newA0000 = 目的节点最后一个子节点的 A0000 + 1
	    			int  NewA0000 = getOrgChildA0000_Max(destOrgId) + 1;
	    			 // 后面节点序号后移
	    			IncOrgA0000(NewA0000, srcChildCount);
	    			 //更新源节点所有子节点的 A0000 从 NewA0000 开始编号包括原节点
	    			updateOrgA0000(codeitemid, NewA0000, true);
	    		//}
	    	}
	        
	        
	        private int getOrgChildA0000_Max(String parentId)throws GeneralException 
	    	{
	    		int n=0;
	            try{ 
	            	String s="SELECT MAX(A0000) as a0000 FROM Organization  WHERE codeitemid LIKE '" + parentId + "%'";
	           	// if(parentId!="")
	       	      //   s = s + " AND CodeItemId <> '" + parentId + "'";
	        	    ContentDAO dao=new ContentDAO(this.conn);	
	    			this.frowset=dao.search(s);
	    	        if(this.frowset.next())
	    	        {
	    	        	n=this.frowset.getInt("a0000");
	    	        }
	            }
	    	    catch(Exception sqle)
	    	    {
	    	       sqle.printStackTrace();
	    	      throw GeneralExceptionHandler.Handle(sqle);
	    	    }
	            return n;
	    	}
	        
	        private int getOrgChildCount(String parentId) throws GeneralException
	    	{
	    		int n=0;
	    		try{
	    			String s="SELECT count(*) as count FROM Organization WHERE codeitemid LIKE '" + parentId + "%'";
	    			ContentDAO dao=new ContentDAO(this.conn);	
	    			this.frowset=dao.search(s);
	    	        if(this.frowset.next())
	    	        {
	    	        	n=this.frowset.getInt("count");
	    	        }
	    		}
	    	    catch(Exception sqle)
	    	    {
	    	       sqle.printStackTrace();
	    	      throw GeneralExceptionHandler.Handle(sqle);
	    	    }
	            return n;
	    	}
	        
	        private void IncOrgA0000(int StartA0000, int Increment)throws GeneralException
	    	{
	            String strSet="";
	    	    if(Increment == 0) {
                    return;
                }
	    	    if(Increment > 0) {
                    strSet = "A0000 = A0000 + " + Increment;
                } else {
                    strSet = "A0000 = A0000 - " + Math.abs(Increment);
                }
	     	    strSet= "update Organization set " + strSet + " where A0000 >= "  + StartA0000;
	     	     try{ 
	     			    ContentDAO dao=new ContentDAO(this.conn);	
	     				dao.update(strSet); 		       
	     	        }
	     		    catch(Exception sqle)
	     		    {
	     		       sqle.printStackTrace();
	     		    }
	    	}
	        
	        private void updateOrgA0000(String orgId,int StartA0000,boolean IncludeRoot) throws GeneralException
	    	{
	           String s;
	           String strOn;
	           String strWhere;
	           String strSet;
	           String strSelect;
	    	   String tempTable;  // 临时表
	    	   StringBuffer sql=new StringBuffer();
	           tempTable = "t#org_order_temp";
	            sql.delete(0,sql.length());
	    		sql.append("drop table ");
	    		sql.append(tempTable);
	    		try{
	    		  ExecuteSQL.createTable(sql.toString(),this.conn);
	    		}catch(Exception e)
	    		{
	    			//e.printStackTrace();
	    		}
	    		sql.delete(0,sql.length());
	    	    //创建排序临时表
	    		switch(Sql_switcher.searchDbServer())
	    		{
	    		  case Constant.MSSQL:
	    		  {
	    			  sql.append("CREATE TABLE ");
	    			  sql.append(tempTable);
	    			  sql.append(" (orgId varchar(50), seqId Int IDENTITY(1,1), OrgA0000 Int)");
	    			  break;
	    		  }
	    		  case Constant.DB2:
	    		  {
	    			  sql.append("CREATE TABLE ");
	    			  sql.append(tempTable);
	    			  sql.append(" (OrgId varchar(50),seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),OrgA0000 INTEGER)");
	    		  	 break;
	    		  }
	    		  case Constant.ORACEL:
	    		  {
	    			  sql.append("CREATE TABLE ");
	    			  sql.append(tempTable);
	    			  sql.append(" (orgId varchar2(50), seqId int, OrgA0000 int)");
	    			  break;
	    		  }
	    		}
	    		try{
	    			  ExecuteSQL.createTable(sql.toString(),this.conn);
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			throw GeneralExceptionHandler.Handle(e);
	    		}

	    	    strSelect = "select CodeItemId from Organization " +
	    	                 " where CodeItemId Like '" + orgId + "%'";
	    	    if(!IncludeRoot && (!"".equals(orgId)))  // 不包括根节点
                {
                    strSelect = strSelect + " and CodeItemId <> '" + orgId + "'";
                }
	            strSelect = strSelect + " Order by A0000 ";

	            sql.delete(0,sql.length());
	            //设置 SeqId
	            switch(Sql_switcher.searchDbServer())
	    		{
	    		  case Constant.MSSQL:
	    		  {
	    			  sql.append("Insert into ");
	    			  sql.append(tempTable);
	    			  sql.append("(orgId) ");
	    			  sql.append(strSelect);
	    			  break;
	    		  }
	    		  case Constant.DB2:
	    		  {
	    			  sql.append("Insert into ");
	    			  sql.append(tempTable);
	    			  sql.append("(orgId) ");
	    			  sql.append(strSelect);
	    		      break;
	    		  }
	    		  case Constant.ORACEL:
	    		  {
	    			  sql.append("Insert into ");
	    			  sql.append(tempTable);
	    			  sql.append(" (orgId, SeqId) ");
	    			  sql.append(" select a.CodeItemId, RowNum from (");
	    			  sql.append("   ");
	    			  sql.append(strSelect);
	    			  sql.append("   ) a");  // 别名			
	    			  break;
	    		  }
	    		}
	            try{
	            	ContentDAO dao=new ContentDAO(this.conn);	
	    			dao.update(sql.toString()); 
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			throw GeneralExceptionHandler.Handle(e);
	    		}

	    	    // 设置orgA0000 = seqId
	    		sql.delete(0,sql.length());
	    	    sql.append("update ");
	    	    sql.append(tempTable);
	    	    sql.append(" set orgA0000 = SeqId");
	    	    try{
	            	ContentDAO dao=new ContentDAO(this.conn);	
	    			dao.update(sql.toString()); 
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			throw GeneralExceptionHandler.Handle(e);
	    		}
	    		sql.delete(0,sql.length());
	    	    //现在 orgA0000 从 1 开始, 更新 orgA0000 从 startA0000 开始
	    	    if(StartA0000 > 1){ 
	    	   
	    	      // MSSQL 中，不能直接更新标识列
	    	      // s := 'update ' + tempTable + ' set SeqId = SeqId + ' + IntToStr(startA0000 - 1);
	    	      sql.append("update ");
	    	      sql.append(tempTable);
	    	      sql.append(" set orgA0000 = orgA0000 + ");
	    	      sql.append(StartA0000 - 1);
	    	      try{
	    	        	ContentDAO dao=new ContentDAO(this.conn);	
	    				dao.update(sql.toString()); 
	    			}catch(Exception e)
	    			{
	    				e.printStackTrace();
	    				throw GeneralExceptionHandler.Handle(e);
	    			}
	    	    }
	    	   
	    	    //更新 A0000
	    	    strOn = "organization.CodeItemId = " + tempTable + ".orgId";
	    	    strSet = "organization.A0000 = " + tempTable + ".orgA0000";
	    	    strWhere = "organization.CodeItemId like '" + orgId + "%'";
	    	    if(!IncludeRoot && (!"".equals(orgId))){   // 不包括根节点
	    	      strWhere = strWhere + " and organization.CodeItemId <> '" + orgId + "'";
	    	    }
	    	    sql.delete(0,sql.length());
	    	    //设置 SeqId
	    	    /*例：
	    	    SQLSERVER:
	    	      Update destTable
	    	      Set destTable.F1 = srcTable.FA
	    	        From DestTable Left Join srcTable
	    	          On DestTable.FB = srcTable.FB
	    	        WHERE srcWhere
	    	    ACCESS:
	    	      Update destTable
	    	        Left Join srcTable
	    	          On DestTable.FB = srcTable.FB
	    	        Set destTable.F1 = srcTable.FA
	    	        WHERE srcWhere
	    	      WHERE destWhere*/
	    	    /*例:
	    	    	ORACLE, DB2:
	    	    	  Update destTable
	    	    	  Set (destTable.F1, destTable.F2) =
	    	    	    (SELECT srcTable.F1, srcTable.F2
	    	    	     FROM srcTable
	    	    	     WHERE strOn and srcWhere
	    	    	    )
	    	    	  WHERE destWhere*/
	    	    //getDBOper.RecordUpdate("organization", tempTable, strOn, strSet, strWhere, strWhere);
	            switch(Sql_switcher.searchDbServer())
	    		{
	    		  case Constant.MSSQL:
	    		  {
	    			  sql.append("Update organization Set ");
	    			  sql.append("organization.A0000 = " + tempTable + ".orgA0000");
	    		      sql.append(" from organization left join ");
	    			  sql.append(tempTable);
	    			  sql.append(" on organization.CodeItemId = " + tempTable + ".orgId");
	    	          sql.append(" where ");
	    			  sql.append(strWhere);
	    			  break;
	    		  }
	    		  case Constant.DB2:
	    		  { 
	    			  sql.append("Update organization set ");
	    			  sql.append("(organization.A0000)=(SELECT ");
	    			  sql.append(tempTable);
	    			  sql.append(".orgA0000 from ");
	    			  sql.append(tempTable);
	    			  sql.append(" where ");
	    			  sql.append(strOn);
	    			  sql.append(" and ");
	    			  sql.append(strWhere);
	    			  sql.append(")");
	    			  sql.append(" where ");
	    			  sql.append(strWhere);			
	    		      break;
	    		  }
	    		  case Constant.ORACEL:
	    		  {
	    			  sql.append("Update organization set ");
	    			  sql.append("(organization.A0000)=(SELECT ");
	    			  sql.append(tempTable);
	    			  sql.append(".orgA0000 from ");
	    			  sql.append(tempTable);
	    			  sql.append(" where ");
	    			  sql.append(strOn);
	    			  sql.append(" and ");
	    			  sql.append(strWhere);
	    			  sql.append(")");
	    			  sql.append(" where ");
	    			  sql.append(strWhere);		
	    			  break;
	    		  }
	    		}      	   
	            try{
	            	ContentDAO dao=new ContentDAO(this.conn);	
	    			dao.update(sql.toString()); 
	      		}catch(Exception e)
	      		{
	      			e.printStackTrace();
	      			throw GeneralExceptionHandler.Handle(e);
	      		}	
	    	    // 删除临时表
	    	    sql.delete(0,sql.length());
	    		sql.append("drop table ");
	    		sql.append(tempTable);
	    		try{
	    		  ExecuteSQL.createTable(sql.toString(),this.conn);
	    		}catch(Exception e)
	    		{
	    			//e.printStackTrace();
	    		}	 
	    	}
	        
	        private String GetNextId(ContentDAO dao,String SrcCode,String DesCode,boolean ishavechild,String Level)
	    	{
	    		String strDesMaxChild="";
	    		String result="";
	    		if(ishavechild)
	    		{
	    	      strDesMaxChild=getMaxChildid(dao,DesCode);
	         	}
	    		else
	    		{
	    		   strDesMaxChild=DesCode+BackLevLenStr(dao,Integer.parseInt(Level));
	    		}
	    		 result=GetNextIdStr(SrcCode,DesCode,strDesMaxChild);
	    		return result;
	    	}
	        
	        private String getMaxChildid(ContentDAO dao,String codeitemid)
	    	{
	    		 String maxchildid="";
	    		 StringBuffer sqlstr=new StringBuffer();
	    		  try{
	    	    	sqlstr.append("select codeitemid from organization where parentid='");
	    	    	sqlstr.append(codeitemid);
	    	    	sqlstr.append("' and codeitemid<>parentid");
	    	    	
	    	    	sqlstr.append(" union select codeitemid from vorganization where parentid='");
	    	    	sqlstr.append(codeitemid);
	    	    	sqlstr.append("' and codeitemid<>parentid order by codeitemid");
	    	    	
	    	    	this.frowset=dao.search(sqlstr.toString());
	    	    	while(this.frowset.next())
	    	    	{
	    	    		if(this.frowset.getString("codeitemid").compareTo(maxchildid)>0)
	    	    		{
	    	    			maxchildid=this.frowset.getString("codeitemid");	    			
	    	    		}
	    	    	}
	    	    }catch(Exception e)
	    		{
	    	    	e.printStackTrace();	    	
	    	    }
	    	    return maxchildid;
	    	} 
	        
	        private String BackLevLenStr(ContentDAO dao,int nLev)
	    	{
	    		int I;
	    		String Result="";
	    		try{
	    		if(BackQryOnLev(dao,nLev+1)==0)
	    		{
	    			if(nLev==1)
	    			{
	    			  String strsql="select codeitemid from organization where Grade="+nLev;
	    			  this.frowset=dao.search(strsql);
	    		     if(this.frowset.next()) {
                         for(I=0;I<this.frowset.getString("codeitemid").length();I++)
                         {
                             Result="0" + Result;
                         }
                     }
	    			}
	    			else
	    			{
	    			  String strsql="select codeitemid from organization where Grade="+(nLev-1);
	    		      this.frowset=dao.search(strsql);
	    		      String StrParentId="";
	    		      if(this.frowset.next()) {
                          StrParentId=this.frowset.getString("codeitemid");
                      }
	    		      strsql="select codeitemid from organization where Grade="+nLev;
	    		      this.frowset=dao.search(strsql);
	    		      String StrNowId="";
	    		      if(this.frowset.next()) {
                          StrNowId= this.frowset.getString("codeitemid");
                      }
	    		      for(I=0;I<StrNowId.length()-StrParentId.length();I++) {
                          Result="0"+Result;
                      }
	    			}
	    		}
	    		else
	    		{
	    			String strsql="select codeitemid from organization where Grade=" + (nLev+1);
	    			this.frowset=dao.search(strsql);
	    			String  StrChildId="";
	    			if(this.frowset.next()) {
                        StrChildId=this.frowset.getString("codeitemid");
                    }
	    			strsql="select codeitemid from organization where Grade=" + nLev;
	    			String  StrNowId="";
	    			this.frowset=dao.search(strsql);
	    			if(this.frowset.next()) {
                        StrNowId=this.frowset.getString("codeitemid");
                    }
	    			 for(I=0;I<StrChildId.length()-StrNowId.length();I++) {
                         Result="0"+Result;
                     }
	    		}	
	    		}catch(Exception e){
	    		  e.printStackTrace();
	    		}
	          return Result.length()==0?"01":Result;
	    	}
	        
	        private int BackQryOnLev(ContentDAO dao,int nLev)
	    	{
	    		try{
	    			String StrSql="select count(*) as ncount from ORGANIZATION where Grade="+nLev;
	    			this.frowset=dao.search(StrSql);
	    			if(this.frowset.next()) {
                        return this.frowset.getInt("ncount");
                    }
	    			return 0;
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}	
	    		return 0;
	    	}
	    	private String GetNextIdStr(String src,String des,String desMaxChild)
	    	{
	    		if(desMaxChild=="")  //如果是第一个子结点
	    		{
	    			return GetNext(src,des);
	    		}
	    		else
	    		{
	    			return GetNext(desMaxChild,des);
	    		}
	    	}
	    	
	    	private String GetNext(String src,String des)
	    	{
	    		int nI,nTag;
	    		String ch;
	    		String result="";
	    		nTag=1;    //进位为1
	    		src=src.toUpperCase();
	    		for(nI=src.length();nI>des.length();nI--)
	    		{
	    			ch=src.substring(nI-1,nI);
	    			if(nTag==1) {
                        ch=GetNextChar(ch);
                    }
	    			result=ch+result;
	    			if("0".equals(ch) && !"0".equals(src.subSequence(nI-1,nI)))
	    			{
	    				nTag=1;
	    			}
	    			else
	    			{
	    				nTag=0;
	    			}
	    			
	    		}	
	    		return des + result;
	    	}
	    	
	    	private String  GetNextChar(String ch)                   //获得下一个进位
	    	{
	    		String result="";
	    		switch(ch.charAt(0))
	    		{
	    			case '0':
	    			{
	    				result="1";
	    				break;
	    			}
	    			case '1':
	    			{
	    				result="2";
	    				break;
	    			}
	    			case '2':
	    			{
	    				result="3";
	    				break;
	    			}
	    			case '3':
	    			{
	    				result="4";
	    				break;
	    			}
	    			case '4':
	    			{
	    				result="5";
	    				break;
	    			}
	    			case '5':
	    			{
	    				result="6";
	    			   break;
	    			}
	    			case '6':
	    			{
	    				result="7";
	    				break;
	    			}
	    			case '7':
	    			{
	    				result="8";
	    				break;
	    			}
	    			case '8':
	    			{
	    				result="9";
	    				break;
	    			}
	    			case '9':
	    			{
	    				result="A";
	    				break;
	    			}
	    			case 'A':
	    			{
	    				result="B";
	    				break;
	    			}
	    			case 'B':
	    			{
	    				result="C";
	    				break;
	    			}
	    			case 'C':
	    			{
	    				result="D";
	    				break;
	    			}
	    			case 'D':
	    			{
	    				result="E";
	    				break;
	    			}
	    			case 'E':
	    			{
	    				result="F";
	    				break;
	    			}
	    			case 'F':
	    			{
	    				result="G";
	    				break;
	    			}
	    			case 'G':
	    			{
	    				result="H";
	    				break;
	    			}
	    			case 'H':
	    			{
	    				result="I";
	    				break;
	    			}
	    			case 'I':
	    			{
	    				result="J";
	    				break;
	    			}
	    			case 'J':
	    			{
	    				result="K";
	    				break;
	    			}
	    			case 'K':
	    			{
	    				result="L";
	    				break;
	    			}
	    			case 'L':
	    			{
	    				result="M";
	    				break;
	    			}
	    			case 'M':
	    			{
	    				result="N";
	    				break;
	    			}
	    			case 'N':
	    			{
	    				result="O";
	    				break;
	    			}
	    			case 'O':
	    			{
	    				result="P";
	    				break;
	    			}
	    			case 'P':
	    			{
	    				result="Q";
	    				break;
	    			}
	    			case 'Q':
	    			{
	    				result="R";
	    				break;
	    			}
	    			case 'R':
	    			{
	    				result="S";
	    				break;
	    			}
	    			case 'S':
	    			{
	    				result="T";
	    				break;
	    			}
	    			case 'T':
	    			{
	    				result="U";
	    				break;
	    			}
	    			case 'U':
	    			{
	    				result="V";
	    				break;
	    			}
	    			case 'V':
	    			{
	    				result="W";
	    				break;
	    			}
	    			case 'W':
	    			{
	    				result="X";
	    				break;
	    			}
	    			case 'X':
	    			{
	    				result="Y";
	    				break;
	    			}
	    			case 'Y':
	    			{
	    				result="Z";
	    				break;
	    			}
	    			case 'Z':
	    			{
	    				result="0";
	    				break;
	    			}
	    		}
	    	  return result;	
	    	}
	    	
	    	private String SetLayerNull(String tbname){
	    		String sql = "update "+tbname+" set layer = null";
	    		return sql;
	    	}
	    	private String InitLayer(String tbname){
	    		String sql = "update "+tbname+" set layer=1 where (codeitemid=parentid) or "+
	    	    " not (parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid))";
	    		return sql;
	    	}
	    	private String NextLayer(String tbname,int lay){
	    		String sql = "update "+tbname+" set layer='"+(lay+1)+"' where codeitemid<>parentid and "+
	    	       " parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid and B.layer='"+lay+"')";;
	    	    return sql;
	    	}
	    	
	    	private ArrayList getRecordVoList(String sql,ContentDAO dao,String tablename) throws Exception {
	    		ArrayList voList=new ArrayList();
	    		try
	    	    {
	    	      DBMetaModel dbmeta = new DBMetaModel();
	    	      TableModel tableModel = dbmeta.searchTable(tablename.toLowerCase());
	    	      RowSet oSet = dao.search(sql);
	    	      ModelField[] fields = tableModel.getFields();
	    	      while(oSet.next()){
	    		      HashMap oMap = new HashMap();
	    		      for (int i = 0; i < fields.length; ++i)
	    		      {
	    		        if (fields[i].getFieldType() == 0)
	    		        {
	    		          if (oSet.getObject(fields[i].getTableField()) != null)
	    		          {
	    		            if (oSet.getObject(fields[i].getTableField()) instanceof Clob)
	    		            {
	    		              oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getTableField()));
	    		            }
	    		            else {
                                oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getTableField()));
                            }
	    		          }
	    		        }
	    		        else if ((fields[i].getFieldType() == 1) && 
	    		          (oSet.getObject(fields[i].getAttribute()) != null))
	    		        {
	    		          if (oSet.getObject(fields[i].getAttribute()) instanceof Clob)
	    		          {
	    		            oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getAttribute()));
	    		          }
	    		          else {
                              oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getAttribute()));
                          }
	    		        }
	    		      }
	    	
	    		      RecordVo ret = new RecordVo(tablename.toLowerCase());
	    		      ret.setValues(oMap);
	    		      voList.add(ret);
	    	      }
	    	    }
	    	    catch (SQLException sqle)
	    	    {
	    	      sqle.printStackTrace();
	    	      throw new SQLException("instantiate model class[" + tablename.toLowerCase() + "] failed");
	    	    }
	    	    catch (Exception e)
	    	    {
	    	      e.printStackTrace();
	    	    }
	    		return voList;
	    	}
	    	
}
