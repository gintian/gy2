package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrgRegister {
	public static ArrayList getStatDateListSqlss(ArrayList work_date_list,String wherestr,String statcolumnstr,String org_id ,ArrayList whereINList){
		   ArrayList statlist=new ArrayList();
		   StringBuffer sqlstr= new StringBuffer();		  
		   sqlstr.append("select "+org_id+",Q03Z0, ");
		   sqlstr.append(statcolumnstr);	   
		   sqlstr.append(wherestr);
		   
		   for(int i=0;i<work_date_list.size();i++){
			   String sql=sqlstr.toString()+" and Q03Z0='"+work_date_list.get(i).toString()+"'";
			   
			   for(int r=0;r<whereINList.size();r++)
			    {
				     
		 	    	 if(r>0)
		 			   {
		 	    		sql=sql+" or ";  
		 			   }else{
		 				  sql=sql+" and ( ";  
		 			   }
		 	    	sql=sql+" a0100 in(select a0100 "+whereINList.get(r).toString()+") ";
		 	    	if(r==whereINList.size()-1) {
                        sql=sql+" )";
                    }
			    } 
			   
			   sql=sql+" group by "+org_id+",Q03Z0";			   
			   statlist.add(sql);
		   }		   
		   return statlist;	   
	 }
	
	public static String insertSQL(String insertcolumn){
	   	   StringBuffer insertsql=new StringBuffer();
	   	   StringBuffer valuesql= new StringBuffer();    	
	   	   int i=0;
		   int r=0;		  
		   insertcolumn=insertcolumn+",";
		   insertsql.append("insert into Q07 (");
		   insertsql.append("b0110,Q03Z0,");
		   valuesql.append(" values(");
		   while(i!=-1){		  
		   i=insertcolumn.indexOf(",",r);			   
		     if(i!=-1){
		       String str=insertcolumn.substring(r,i);
		       str=str.trim();	
		       insertsql.append(str+",");
		       valuesql.append("?,");	      
		     }
		     r=i+1;
		   }
		   insertsql.append("setid,Q03Z5,i9999)");
		   valuesql.append("?,?,?,?,?)");
		   String sqlstr=insertsql.toString()+valuesql.toString();
	   	return sqlstr;
	   }
	public static String where_Date(String start_date,String end_date){
		   StringBuffer sqlstr= new StringBuffer();
		   sqlstr.append(" and Q03Z0 >= '"+start_date+"'");
		   sqlstr.append(" and Q03Z0 <= '"+end_date+"'");
		   return sqlstr.toString();
	}
	public static String where_Date(){
		   StringBuffer sqlstr= new StringBuffer();
		   sqlstr.append(" and Q03Z0 >= ?");
		   sqlstr.append(" and Q03Z0 <= ?");
		   return sqlstr.toString();
	}
	 public static String whereSQL(String org_value,String start_date,String end_date,String codesetid){		   
		   StringBuffer where_org=new StringBuffer();
		   where_org.append(" where");		   
		   where_org.append(" b0110 = '"+org_value+"'");
		   where_org.append(" and Q03Z0 >= '"+start_date+"'");
		   where_org.append(" and Q03Z0 <= '"+end_date+"'");
		   where_org.append(" and setid = '"+codesetid+"'");
		   return where_org.toString();
	 }
	 
	 public static String whereSumSQL(String org_value,String kq_duration,String codesetid){		   
		   StringBuffer where_org=new StringBuffer();
		   where_org.append(" where");		   
		   where_org.append(" b0110 = '"+org_value+"'");
		   where_org.append(" and Q03Z0 = '"+kq_duration+"'");		  
		   return where_org.toString();
	 }
	 public static ArrayList getSqlstr(ArrayList fieldsetlist,String start_date,String end_date,String code,String tablename,String cur_date,ArrayList whereINList){
	 	   
			StringBuffer wheresql=new StringBuffer();	
			
			//生成没有高级条件的from后的sql语句
			StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldsetlist.size();i++){
				FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
				
				column.append(fielditem.getItemid()+",");
			}
			int l=column.toString().length()-1;
			String columnstr=column.toString().substring(0,l);
			String sqlstr="select "+columnstr+" ";
			wheresql.append(" from "+tablename+" ");	
			wheresql.append(" where b0110 like '"+code+"%'");
			wheresql.append(where_Date(start_date,end_date));
			if(cur_date!=null && cur_date.length()>0) {
                wheresql.append(" and q03z0='"+cur_date+"'");
            }
			for(int i=0;i<whereINList.size();i++)
			   {   
				   if(i>0)
				   {
					   wheresql.append(" or ");  
				   }else
				   {
					   wheresql.append(" and ( ");    
				   }
				   wheresql.append("  b0110 in(select distinct e0122 "+whereINList.get(i).toString()+") "); 
				   if(i==whereINList.size()-1) {
                       wheresql.append(")");
                   }
			   }
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
		    list.add(1,wheresql.toString());
		    list.add(2,columnstr);		    
		    return list;
	  }
	 public static ArrayList getSumSqlstr(ArrayList fieldsetlist,String coursedate,String code,String tablename){
	 	   
			StringBuffer wheresql=new StringBuffer();		
			
			//生成没有高级条件的from后的sql语句
			StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldsetlist.size();i++){
				FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
				
				column.append(fielditem.getItemid()+",");
			}
			int l=column.toString().length()-1;
			String columnstr=column.toString().substring(0,l);
			String sqlstr="select "+columnstr+" ";
			wheresql.append(" from "+tablename+" ");			
			wheresql.append(" where b0110 = '"+code+"'");
			wheresql.append(" and Q03Z0 = '"+coursedate+"'");
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
		    list.add(1,wheresql.toString());
		    list.add(2,columnstr);		    
		    return list;
	  }
	 public static ArrayList getSumSqlstrIN(ArrayList fieldsetlist,String coursedate,String code,String tablename){
	 	   
			StringBuffer wheresql=new StringBuffer();		
			
			//生成没有高级条件的from后的sql语句
			StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldsetlist.size();i++){
				FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
				
				column.append(fielditem.getItemid()+",");
			}
			int l=column.toString().length()-1;
			String columnstr=column.toString().substring(0,l);
			String sqlstr="select "+columnstr+" ";
			wheresql.append(" from "+tablename+" ");	
			
			wheresql.append(" where 1=1 ");
			if(code!=null&&code.length()>0)
			{   if(!"'".equals(code.substring(0,1))) {
                wheresql.append("and b0110 in ('"+code+"')");
            } else {
                wheresql.append("and b0110 in ("+code+")");
            }
			}
			 
			wheresql.append(" and Q03Z0 = '"+coursedate+"'");
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
		    list.add(1,wheresql.toString());
		    list.add(2,columnstr);		    
		    return list;
	  }
	 
	 public static ArrayList getSumSqlstrLike(ArrayList fieldsetlist,String coursedate,String code,String tablename,ArrayList whereINList){
	 	   
			StringBuffer wheresql=new StringBuffer();		
			
			//生成没有高级条件的from后的sql语句
			StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldsetlist.size();i++){
				FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
				
				column.append(fielditem.getItemid()+",");
			}
			int l=column.toString().length()-1;
			String columnstr=column.toString().substring(0,l);
			String sqlstr="select "+columnstr+" ";
			wheresql.append(" from "+tablename+" ");	
			
			wheresql.append(" where 1=1 ");
			if(code!=null&&code.length()>0)
			{  
				  wheresql.append("and b0110 like'"+code+"%'");
			}
			 
			wheresql.append(" and Q03Z0 = '"+coursedate+"'");
			for(int i=0;i<whereINList.size();i++)
			   {   
				   if(i>0)
				   {
					   wheresql.append(" or ");  
				   }else
				   {
					   wheresql.append(" and ( ");    
				   }
				   wheresql.append("  b0110 in(select distinct e0122 "+whereINList.get(i).toString()+") "); 
				   if(i==whereINList.size()-1) {
                       wheresql.append(")");
                   }
			   }
			
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
		    list.add(1,wheresql.toString());
		    list.add(2,columnstr);		    
		    return list;
	  }
	 public static String getOrgidListSql(String org_id,String org_value,String wherestr,String statcolumnstr){
		  
		   StringBuffer sqlstr= new StringBuffer();		  
		   sqlstr.append("select "+org_id+",Q03Z0, ");
		   sqlstr.append(statcolumnstr);	   
		   sqlstr.append(wherestr);	 
		   sqlstr.append(" and "+org_id+" = '"+org_value+"'");
		   sqlstr.append(" group by "+org_id+",Q03Z0");
		   return sqlstr.toString();
	 }
	 public static String insertSumSQL(String insertcolumn){
	   	   StringBuffer insertsql=new StringBuffer();
	   	   StringBuffer valuesql= new StringBuffer();    	
	   	   int i=0;
		   int r=0;		  
		   insertcolumn=insertcolumn+",";
		   insertsql.append("insert into Q09 (");
		   insertsql.append("b0110,Q03Z0,");
		   valuesql.append(" values(");
		   while(i!=-1){		  
		   i=insertcolumn.indexOf(",",r);			   
		     if(i!=-1){
		       String str=insertcolumn.substring(r,i);
		       str=str.trim();	
		       insertsql.append(str+",");
		       valuesql.append("?,");	      
		     }
		     r=i+1;
		   }
		   insertsql.append("scope,setid,Q03Z5,i9999)");
		   valuesql.append("?,?,?,?,?,?)");
		   String sqlstr=insertsql.toString()+valuesql.toString();
	   	return sqlstr;
	   }
	 public static String kq_dateSQL(String kq_start){
			StringBuffer selectSQL=new StringBuffer();
			selectSQL.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration where ");
			selectSQL.append(" kq_start="+Sql_switcher.dateValue(kq_start)+" ");			
			return selectSQL.toString();
		}
	 public static int getI9999(String tablename,String b0110,String work_date){
	    	int i9999=1;
	    	String sql="select max(i9999) as i9999 from "+tablename+" where  b0110='"+b0110+"' and Q03Z0='"+work_date+"'";
	    	List rs=null;
	    	rs = ExecuteSQL.executeMyQuery(sql);
		       if(!rs.isEmpty())
		       {
		    	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);	    	  
		    	  String str_i9999=rec.get("i9999")!=null?rec.get("i9999").toString():"1";	  
		    	  if(str_i9999!=null&&str_i9999.length()>0){
		    		  i9999=Integer.parseInt(str_i9999); 
		    		  i9999=i9999+1;
		    	  }
		       }
	    	return i9999;
	    }
	 
    public static ArrayList newFieldItemList(ArrayList fielditemlist) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            String itemId = fielditem.getItemid();

            if ("i9999".equalsIgnoreCase(itemId) || "state".equalsIgnoreCase(itemId) || "nbase".equalsIgnoreCase(itemId)
                    || "a0100".equalsIgnoreCase(itemId) || "a0101".equalsIgnoreCase(itemId) || "e01a1".equalsIgnoreCase(itemId)
                    || "b0110".equalsIgnoreCase(itemId) || "e0122".equalsIgnoreCase(itemId) || "q03z3".equalsIgnoreCase(itemId)
                    || "q03z5".equalsIgnoreCase(itemId) || "c010k".equalsIgnoreCase(itemId) || "a0177".equalsIgnoreCase(itemId)
                    || "modtime".equalsIgnoreCase(itemId) || "modusername".equalsIgnoreCase(itemId)) {
                continue;
            }

            fielditem.setVisible("1".equals(fielditem.getState()));
            list.add(fielditem.clone());
        }
        
        return list;
    }
	 
	 /**根据权限,生成select.IN中的查询串	    
	* @return 返回查询串
	* 
	*/   
			
	/**得到部门代码，
	* 
	* return 返回是部门统计还是单位统计
	* 
	*/
   public static String getOrgType(UserView userView){
			 String org_id="";
			 if(!userView.isSuper_admin())
			 {
	           if("UN".equals(RegisterInitInfoData.getKqPrivCode(userView)))
	           {	   
	        	   org_id="b0110";           
	           }else if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
	           {		  
	        	   org_id="e0122";
			   }else{
				      org_id="b0110";
			   }
			}else{
				org_id="b0110"; 
			}
			   return org_id;
   }
   // 得到在考勤范围内的部门员工编号，并添加到list中
   public static ArrayList getQrgE0122List(Connection conn,String strsql,String org_id)throws GeneralException
   {     
	   
       ContentDAO dao = new ContentDAO(conn);
       ArrayList orglist=new ArrayList();
       RowSet rowSet=null;       
       try{
    	   rowSet = dao.search(strsql.toString());
         while(rowSet.next()){
        	 String orgvalue=rowSet.getString(org_id);
        	 if(orgvalue!=null&&orgvalue.length()>0)
        	 {
        		 orglist.add(orgvalue);
        	 }
        	 
          }
       }catch(Exception e){
	     throw GeneralExceptionHandler.Handle(e); 
       }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     } 
       return orglist;
   }
   public static String selcet_kq_OrgId(String start_date,String end_date,String org_id,ArrayList whereINList,String b0110){
	   StringBuffer sqlstr= new StringBuffer();
	   sqlstr.append("select distinct "+org_id+" from Q03");	  
	   sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
	   if(b0110!=null&&b0110.length()>0)
	   {
		   sqlstr.append(" and b0110 like '"+b0110+"%'  ");
	   }
	   for(int i=0;i<whereINList.size();i++)
	   {   
		   if(i>0)
		   {
			   sqlstr.append(" or ");  
		   }else
		   {
			   sqlstr.append(" and ( ");    
		   }
		   sqlstr.append("  "+org_id+" in(select distinct "+org_id+" "+whereINList.get(i).toString()+") "); 
		   if(i==whereINList.size()-1) {
               sqlstr.append(")");
           }
	   }
	   
	  // sqlstr.append(" and Q03Z5 not in ('01','07','08')");
	   return sqlstr.toString();
   } 
   public static String selcet_kq_AllOrgId(String start_date,String end_date,String org_id,ArrayList whereINList){
	   StringBuffer sqlstr= new StringBuffer();
	   sqlstr.append("select distinct "+org_id+" from Q03");	
	   sqlstr.append(" where 1=1 ");
	   /*sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");*/
	   
	   for(int i=0;i<whereINList.size();i++)
	   {   
		   if(i>0)
		   {
			   sqlstr.append(" or ");  
		   }else
		   {
			   sqlstr.append(" and ( ");    
		   }
		   sqlstr.append("  "+org_id+" in(select distinct "+org_id+" "+whereINList.get(i).toString()+") "); 
		   if(i==whereINList.size()-1) {
               sqlstr.append(")");
           }
	   }	  
	   return sqlstr.toString();
   }
   /***************检索月考勤期间******************/
   public static String getMonthRegisterDate(String start_date,String end_date)
   {
	   String 	kq_period="";
	   start_date=start_date.replaceAll("-","\\.");
	   end_date=end_date.replaceAll("-","\\.");		
	   kq_period=start_date+"-"+end_date;
	   return kq_period;
   }
   /**
    * 
    * @param fieldsetlist
    * @param b0110list
    * @param cur_date
    * @param code
    * @param tablename
    * @return
    */
   public static ArrayList getSqlstrHistory(ArrayList fieldsetlist,String  b0110s ,String cur_date,String code,String tablename){
 	   
		StringBuffer wheresql=new StringBuffer();	
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
			
			column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from "+tablename+" where ");		
		condition.append("  Q03Z0 ='"+cur_date+"'");	
		if(b0110s!=null&&b0110s.length()>0)
		{
			condition.append(" and b0110 in ("+b0110s+")");
		}else		
		{
			condition.append(" and b0110 = '"+code+"'");
		}
		ArrayList list= new ArrayList();
		wheresql.append(" "+condition.toString());
		list.add(0,sqlstr);
	    list.add(1,wheresql.toString());
	    list.add(2,"order by b0110");
	    list.add(3,columnstr);	
	    list.add(4,condition.toString());
	    return list;
 }
   /**
    * szk部门不定期高级授权
    * @param fieldsetlist
    * @param b0110list
    * @param cur_date
    * @param code
    * @param tablename
    * @return
    */
   public static ArrayList getSqlstrOrg(ArrayList fieldsetlist,String  b0110s ,String cur_date,String code,String tablename){
 	   
		StringBuffer wheresql=new StringBuffer();	
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
			
			column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from "+tablename+" where ");		
		condition.append("  Q03Z0 ='"+cur_date+"'");	
	
		if(b0110s!=null&&b0110s.length()>0)
		{
			condition.append(" and b0110 in ("+b0110s+") and b0110 like '"+code+"%'");
		}else		
		{
			condition.append(" and b0110 like '"+code+"%'");
		}
		ArrayList list= new ArrayList();
		wheresql.append(" "+condition.toString());
		list.add(0,sqlstr);
	    list.add(1,wheresql.toString());
	    list.add(2,"order by b0110");
	    list.add(3,columnstr);	
	    list.add(4,condition.toString());
	    return list;
 }
   public static ArrayList getStatDateListSql(ArrayList work_date_list,String wherestr,String statcolumnstr,String org_id ,String  org_value){
	   ArrayList statlist=new ArrayList();
	   StringBuffer sqlstr= new StringBuffer();		  
	   sqlstr.append("select "+org_id+",Q03Z0, ");
	   sqlstr.append(statcolumnstr);	   
	   sqlstr.append(wherestr);
	   	
	   for(int i=0;i<work_date_list.size();i++){
		   String sql=sqlstr.toString()+" and Q03Z0='"+work_date_list.get(i).toString()+"'";
		   sql=sql+" and "+org_id+"='"+org_value+"'";		   
		   sql=sql+" group by "+org_id+",Q03Z0";			   
		   statlist.add(sql);
	   }		   
	   return statlist;	   
   }
   public static ArrayList newFieldItemListQ09(ArrayList list,String codesetid)
   {
	     FieldItem fielditem=new FieldItem();
		 fielditem.setFieldsetid("Q09");
		 fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.deptname"));
		 fielditem.setItemid("b0110");
		 fielditem.setItemtype("A");		
		 fielditem.setCodesetid(codesetid);
		 fielditem.setVisible(true);
		 list.add(0,fielditem);
		 FieldItem fielditem1=new FieldItem();
		 fielditem1.setFieldsetid("Q09");
		 fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.codesetid"));
		 fielditem1.setItemid("setid");
		 fielditem1.setItemtype("A");
		 fielditem1.setCodesetid("0");
		 fielditem1.setVisible(false);
		 list.add(fielditem1);		 
		 FieldItem fielditem2=new FieldItem();
		 fielditem2.setFieldsetid("Q09");
		 fielditem2.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
		 fielditem2.setItemid("scope");
		 fielditem2.setItemtype("A");
		 fielditem2.setCodesetid("0");
		 fielditem2.setVisible(false);
		 list.add(fielditem2);
		 return list;
   }
   public static ArrayList newFieldItemListQ07(ArrayList list,String codesetid)
   {
	     FieldItem fielditem=new FieldItem();
		 fielditem.setFieldsetid("Q07");
		 fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.deptname"));
		 fielditem.setItemid("b0110");
		 fielditem.setItemtype("A");		
		 fielditem.setCodesetid(codesetid);
		 fielditem.setVisible(true);
		 list.add(0,fielditem); 	
		 FieldItem fielditem1=new FieldItem();
		 fielditem1.setFieldsetid("Q07");
		 fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.codesetid"));
		 fielditem1.setItemid("setid");
		 fielditem1.setItemtype("A");
		 fielditem1.setCodesetid("0");
		 fielditem1.setVisible(false);
		 list.add(fielditem1);
		 return list;
   }
   public static String selcet_kq_AllOrgId(String org_id,ArrayList whereINList,String b0110){
	   StringBuffer sqlstr= new StringBuffer();
	   sqlstr.append("select distinct "+org_id+" from Q03 where 1=1 ");
	   if(b0110!=null&&b0110.length()>0)
	   {
		   sqlstr.append(" and b0110 like '"+b0110+"%'  ");
	   }
	   for(int i=0;i<whereINList.size();i++)
	   {   
		   if(i>0)
		   {
			   sqlstr.append(" or ");  
		   }else
		   {
			   sqlstr.append(" and ( ");    
		   }
		   sqlstr.append("  "+org_id+" in(select distinct "+org_id+" "+whereINList.get(i).toString()+") "); 
		   if(i==whereINList.size()-1) {
               sqlstr.append(")");
           }
	   }	  
	   return sqlstr.toString();
   }
}
