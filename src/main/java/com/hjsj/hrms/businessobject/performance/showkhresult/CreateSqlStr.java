package com.hjsj.hrms.businessobject.performance.showkhresult;


import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class CreateSqlStr {
   private static String initinfo;
   private static final String left=" left join ";
   private static final String on=" on ";
   public static String getSql(ArrayList guildlist,String flag){
	 StringBuffer sbsql=new StringBuffer();  
	 sbsql.append("select ");
	 sbsql.append("mcname,mdname,ocname,odname,pt.object_id,pt.mainbody_id,pt.a0101 as mainame,objs.a0101 as objectname,");
	 for(int i=0;i<guildlist.size();i++){
		 String guild = (String) guildlist.get(i);
		 if(i!=guildlist.size()-1){
			 if("score".equalsIgnoreCase(flag))
			 {
				// CONVERT(NUMERIC(10,2),1213.3456)
				sbsql.append("CAST(aa"+guild+"."+flag+" as numeric(10,2)) as aa"+guild+",");
			 }
			 else
			 {
	    		 sbsql.append("aa"+guild+"."+flag+" as aa"+guild+",");
			 }
		 }else{
			 if("score".equalsIgnoreCase(flag))
			 {
				 sbsql.append("CAST(aa"+guild+"."+flag+" as numeric(10,2)) as aa"+guild+" ");
			 }
			 else
			 {
				 sbsql.append("aa"+guild+"."+flag+" as aa"+guild+" ");
			 }
		 }
	 
	 }
	   return sbsql.toString();
   }
   public static String getWhere(ArrayList guildlist,String flag,String plan_id,String who){
	   StringBuffer sbwhere=new StringBuffer();  
	   sbwhere.append("from ");
	   sbwhere.append("(select * from per_mainbody where plan_id="+plan_id+") pt ");
	   sbwhere.append(left);
	   sbwhere.append("(");
	   sbwhere.append("select obj.a0101,obj.object_id,org.codeitemid as odid,org.codeitemdesc as odname,orgs.codeitemid as ocid,orgs.codeitemdesc as ocname from per_object obj");
	   sbwhere.append(left);
	   sbwhere.append("(select * from organization )org on obj.e0122=org.codeitemid left join (select * from organization )orgs on obj.b0110=orgs.codeitemid where plan_id="+plan_id);	   
	   sbwhere.append(" )objs");
	   sbwhere.append(on);
	   sbwhere.append("pt.object_id=objs.object_id");
	   sbwhere.append(left);
	   sbwhere.append("(select mainbody.mainbody_id,mainbody.E0122,mainbody.b0110,mainbody.a0101,org.codeitemid as mdid,org.codeitemdesc as mdname,orgs.codeitemid as mcid,orgs.codeitemdesc as  mcname ");
	   sbwhere.append("from per_mainbody mainbody left join(select * from organization )org on mainbody.e0122=org.codeitemid left join ");
	   sbwhere.append("(select * from organization )orgs on mainbody.b0110=orgs.codeitemid where plan_id="+plan_id+" group by mainbody.mainbody_id,mainbody.E0122,mainbody.b0110,mainbody.a0101,org.codeitemid,org.codeitemdesc,orgs.codeitemid,orgs.codeitemdesc )mains on pt.mainbody_id=mains.mainbody_id");
	   
	   for(int i=0;i<guildlist.size();i++){
		   sbwhere.append(left);
		   String guild = (String) guildlist.get(i);
		   sbwhere.append("(select * from per_table_"+plan_id+" where point_id='"+guild+"') aa"+guild +" ");
		   sbwhere.append(" on pt.object_id=aa"+guild+".object_id and pt.mainbody_id=aa"+guild+".mainbody_id");
	   }
	   if(!"all".equals(who)){
		   String tempwho[]=who.split(",");
		   if("object_id".equals(tempwho[1])){
			   sbwhere.append(" where pt.object_id='"+tempwho[0]+"'");
		   }else{
			   sbwhere.append(" where pt.mainbody_id='"+tempwho[0]+"'");
		   }
	   }
	   return sbwhere.toString();
   }
   public static String getcolumns(ArrayList guildlist,String flag){
	   StringBuffer sbcolumns=new StringBuffer();
	   sbcolumns.append("mcname,mdname,ocname,odname,object_id,mainbody_id,mainame,objectname,");
	   for(int i=0;i<guildlist.size();i++){
		   String guild=(String) guildlist.get(i);
		   if(i!=guildlist.size()-1){
			   sbcolumns.append("aa"+guild+",");
		   }else{
			   sbcolumns.append("aa"+guild);
		   }
	   }
	   
	   return sbcolumns.toString();
   }
   public static String getOrderby(){
	   StringBuffer sborderby=new StringBuffer();
	   sborderby.append(" order by pt.object_id,pt.mainbody_id");
	   return sborderby.toString();
   }
   public static String getSelstr(ContentDAO dao,String inputplan_id,String modelType) throws SQLException{
	   StringBuffer sbsel=new StringBuffer();
	   /**只找几名的360度考评*/
	   String sql = "select plan_id,name,template_id,"+Sql_switcher.isnull("A0000", "999999")+" as norder from per_plan where plan_type=1 and method=1";
	   if("ALL".equalsIgnoreCase(modelType))
	   {
		   sql+=" and status=7";
	   }
	   else if("UN".equalsIgnoreCase(modelType))
		   sql+=" and object_type=3  and status=7 ";
	   else if("UM".equalsIgnoreCase(modelType))
		   sql+=" and (object_type=1 or object_type=4) and status=7 ";
	   else if("@K".equalsIgnoreCase(modelType))
		   sql+=" and object_type=2 and status=7 ";
	   sql+=" order by norder asc,plan_id desc ";
	   
	   sbsel.append("<select name=\"plan_id\" onchange=\"onscore();\">");
	   RowSet rs=dao.search(sql);	  
	   while(rs.next()){
		   String plan_id=rs.getString("plan_id");
		   String name=rs.getString("name");
		   String template_id=rs.getString("template_id");
		   sbsel.append("<option value=\""+plan_id+","+template_id+"\"");
		   if(inputplan_id.equals(plan_id)){
			   sbsel.append(" selected=\"selected");
		   }
		   sbsel.append("\">");
		   sbsel.append(name);
		   sbsel.append("</option>");
		  
	   }
	   sbsel.append("</select>");
	   return sbsel.toString();
   }
   public static ArrayList getPointname(ContentDAO dao,ArrayList pointlist) throws Exception{
	   ArrayList pointinfo=new ArrayList();
	   StringBuffer sb=new StringBuffer();
	   sb.append("select point_id,pointname,pointkind from per_point where");
	   for(int i=0;i<pointlist.size();i++){
		   String point_id=(String)pointlist.get(i);
		   if(i==0){
			   sb.append(" point_id='"+point_id+"'");
		   }else{
			  sb.append(" or point_id='"+point_id+"'");
		   }
	   }
	   if(pointlist.size()>1){
		   sb.append("or point_id='"+(String)pointlist.get(pointlist.size()-1)+"'");
	   }
//	   System.out.println(sb.toString());
	   RowSet rs=dao.search(sb.toString());
	   while(rs.next()){
		   String point_id=rs.getString("point_id");
		   String pointname=rs.getString("pointname");
		   String pointkind=rs.getString("pointkind");
		   pointinfo.add(point_id+","+pointname+","+pointkind);
	   }
	   return pointinfo;
   }
   public static ArrayList getFieldList(ArrayList pointlist){
	   ArrayList myList=new ArrayList();
	   FieldItem fielditem=new FieldItem();
	   for(int i=0;i<pointlist.size();i++){
		   fielditem=new FieldItem();
		   String point_id=(String) pointlist.get(i);
		   fielditem.setItemdesc(point_id);
		   fielditem.setItemid(point_id);
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("0");
		   fielditem.setVisible(true);
		   myList.add(fielditem);
	   }
	   return myList;
   }
   public static ArrayList getObjectInfo(ContentDAO dao,String plan_id) throws SQLException{
	   ArrayList objectList=new ArrayList();
	   StringBuffer sbsql=new StringBuffer();
	   sbsql.append("select object_id from per_object where plan_id=");
	   sbsql.append(plan_id);
	   RowSet rs=dao.search(sbsql.toString());
	   while(rs.next()){
		   objectList.add(rs.getString("object_id"));
	   }
	   return objectList;
   }
   public static ArrayList getMainbodyInfo(ContentDAO dao,String plan_id) throws SQLException{
	   ArrayList mainbodyList=new ArrayList();
	   StringBuffer sbsql=new StringBuffer();
	   sbsql.append("select mainbody_id from per_mainbody where plan_id=");
	   sbsql.append(plan_id);
	   sbsql.append(" group by mainbody_id");
	   RowSet rs=dao.search(sbsql.toString());
	   while(rs.next()){
		   mainbodyList.add(rs.getString("mainbody_id"));
	   }
	   return mainbodyList;
   }
   /**
    * 
    * @param dao
    * @param modelType区别单位部门和个人
    * @return
    * @throws SQLException
    */
   public static String getInitinfo(ContentDAO dao,String modelType) throws SQLException {
	   initinfo=null;
	   String sql = "select plan_id,name,template_id from per_plan where plan_type=1 and method=1";
	   if("ALL".equalsIgnoreCase(modelType))
	   {
		   sql+=" and status=7 ";
	   }
	   else if("UN".equalsIgnoreCase(modelType))
		   sql+=" and object_type=3 and status=7 ";
	   else if("UM".equalsIgnoreCase(modelType))
		   sql+=" and (object_type=1 or object_type=4) and status=7 ";
	   else if("@K".equalsIgnoreCase(modelType))
		   sql+=" and object_type=2 and status=7 ";
	   sql+=" order by plan_id ";
	   RowSet rs=dao.search(sql);
	   int i=0;
	   while(rs.next()){
		   String plan_id=rs.getString("plan_id");
		   String template_id=rs.getString("template_id");
		   if(i==0){
			   initinfo=(plan_id+","+template_id);
		   }
		   i++;
	   }
	   return initinfo;
   }
   public static String[] getObjectInfo(ContentDAO dao,String plan_id,String object_id) throws GeneralException{
	   String[] ret=new String[3];
	   StringBuffer sbsql=new StringBuffer();
	   sbsql.append("select obj.a0101,org.codeitemdesc as odname,orgs.codeitemdesc as ocname from per_object obj left join ");
	   sbsql.append("(select * from organization )org ");
	   sbsql.append("on obj.e0122=org.codeitemid ");
	   sbsql.append("left join ");
	   sbsql.append("(select * from organization )orgs ");
	   sbsql.append("on obj.b0110=orgs.codeitemid ");
	   sbsql.append("where plan_id="+plan_id+" and object_id='"+object_id+"'");
	   try {
		RowSet rs=dao.search(sbsql.toString());
//		rs.getString("ocname");
		while(rs.next()){
			ret[0]=rs.getString("ocname");
			ret[1]=rs.getString("odname");
			ret[2]=rs.getString("a0101");
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.nofind.objectinfo"),"",""));			
	}
	   return ret;
   }
   public static String[] getMainbodyInfo(ContentDAO dao,String plan_id,String mainbody_id) throws GeneralException{
	   String[] ret=new String[3];
	   StringBuffer sbsql=new StringBuffer();
	   sbsql.append("select mainbody.a0101,org.codeitemdesc as mdname,orgs.codeitemdesc as mcname from ");
	   sbsql.append("per_mainbody mainbody ");
	   sbsql.append("left join");
	   sbsql.append(" (select * from organization )org ");
	   sbsql.append("on mainbody.e0122=org.codeitemid ");
	   sbsql.append("left join ");
	   sbsql.append("(select * from organization )orgs ");
	   sbsql.append("on mainbody.b0110=orgs.codeitemid ");
	   sbsql.append("where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"' ");
	   sbsql.append("group by mainbody.a0101,org.codeitemid,org.codeitemdesc,orgs.codeitemdesc");
	   try {
		RowSet rs = dao.search(sbsql.toString());
		while(rs.next()){
			ret[0]=rs.getString("mcname");
			ret[1]=rs.getString("mdname");
			ret[2]=rs.getString("a0101");
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block			
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.nofind.mainbodyinfo"),"",""));		
	}
	   return ret;
   }
   public static ArrayList getMOinfo(ContentDAO dao,String plan_id) throws GeneralException {
	   ArrayList MOlist=new ArrayList();
	   StringBuffer sbsql=new StringBuffer();
	   sbsql.append("select a.*,b.* from ");
	   sbsql.append(" (select main.object_id,main.a0101 as mainbodyname,main.mainbody_id,org1.codeitemdesc as mcname,org2.codeitemdesc as mdname ");
	   sbsql.append(" from per_mainbody main,organization org1,organization org2 where main.b0110=org1.codeitemid and main.e0122=org2.codeitemdesc  ");
	   sbsql.append(" and main.plan_id="+plan_id+") a,");
	   sbsql.append("(select obj.object_id as objid,obj.a0101 as objectname,org1.codeitemdesc as ocname,org2.codeitemdesc as odname");
	   sbsql.append(" from per_object obj,organization org1,organization org2 where obj.b0110=org1.codeitemid and obj.e0122=org2.codeitemdesc");
	   sbsql.append(" and obj.plan_id="+plan_id+") b where a.object_id=b.objid");
	   try {
		   MOlist=(ArrayList) ExecuteSQL.executeMyQuery(sbsql.toString());
		   
	   } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.nofind.objectormainbodyinfo"),"",""));

	}
	   return MOlist;
   }
}
