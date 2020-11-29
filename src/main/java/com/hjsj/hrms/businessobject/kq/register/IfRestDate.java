package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * <p>Title:</p>
 * <p>Description:处理工作日期</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-14:9:18:40</p>
 * @author kf-1
 * @version 1.0
 *
 */

public class IfRestDate {
	public static String is_RestDate(String cur_date,UserView userView,String rest_date,String b0110,Connection conn){
		String rest_state=ResourceFactory.getProperty("kq.date.work");//默认工作日
		String restdate=ResourceFactory.getProperty("kq.date.rest");
		String feast_name=if_Feast(cur_date,conn);
		if(feast_name!=null&&feast_name.length()>0)//判断是不是节假日
		{
			String turn_date=getTurn_Date(b0110,cur_date,conn);
			if(turn_date==null||turn_date.length()<=0)			
			{
				rest_state=feast_name;
			}else
			{
				if(if_Rest(cur_date,userView,rest_date))
				{
					rest_state=feast_name;
				}
			}
			
		}else{
			//String rest_date=search_RestOfWeek(b0110,userView); 
			if(if_Rest(cur_date,userView,rest_date))//判断公休日
		     {   
			     String turn_date=getTurn_Date(b0110,cur_date,conn);			     
			     if(turn_date==null||turn_date.length()<=0)
			     {
				   
			    	 rest_state=restdate;
			     }
		     }else
		     {
		    	 String g_rest_date=getWeek_Date(b0110,cur_date,conn);//公休是否倒休		    	
			     if(g_rest_date!=null&&g_rest_date.length()>0)//有倒休日期，上班
			     {
			    	 rest_state=restdate;
			     }			   
		      }
		}
		return rest_state;
	}
	/**
	 * 判断当前是否是节假日
	 * @param cur_date2 当前日期
	 * @param conn 数据库连接
	 * @return 如果是节假日，返回节假日名称；如果不是节假日，返回空字符窜
	 */
	public static String if_Feast(String cur_date2,Connection conn)
    {
    	RowSet rowSet = null;
    	
    	ContentDAO dao = new ContentDAO(conn);
    	Date date=DateUtils.getDate(cur_date2,"yyyy.MM.dd");
    	String shortDate = OperateDate.dateToStr(date, "MM.dd");
        String longDate = OperateDate.dateToStr(date, "yyyy.MM.dd");
        
        //zxj 20140916 原sql条件仍有遗漏的情况（末尾没有逗号的feastdates无法匹配），现已改进
    	StringBuffer sql = new StringBuffer();    	
    	sql.append("SELECT feast_name,feast_dates from kq_feast");
    	sql.append(" where ','" + Sql_switcher.concat() + "cast(feast_dates as varchar(1000))" + Sql_switcher.concat() + "','");
    	sql.append(" like '%," + shortDate + ",%'");
    	sql.append(" or ','" + Sql_switcher.concat() + "cast(feast_dates as varchar(1000))" + Sql_switcher.concat() + "','");
    	sql.append(" like '%" + longDate + "%'");
    	try{ 
    		rowSet = dao.search(sql.toString()); 	
    	if (rowSet.next())
    	{
    	    return rowSet.getString("feast_name");    			
    	}
    	}catch(Exception e){
    		e.printStackTrace();    		 
    	}finally
	    {
			KqUtilsClass.closeDBResource(rowSet);
	    } 
    	return "";
    }
	/****
     * 判断是否是公休
     * @param  cur_date 当前考勤期间
     * @return 
     *        boolean true:是,false 不是
     * */
    public static boolean if_Rest(String cur_date,UserView userView,String rest_date)
    {
    	
    	boolean isCorrect=false;
    	
    	Date date=DateUtils.getDate(cur_date,"yyyy.MM.dd");
    	String EE = KqUtilsClass.getWeekName(date);
   	
    	isCorrect = rest_date.indexOf(EE)!=-1;

    	return isCorrect;
    }
    /**
     * 得到公休日
     * @param b0110 单位名称代码
     * @param userView 用户权限
     * @return restList 1,公休日;2,该公休日的部门代码（用于查看倒休日）
     * **/
    public static ArrayList search_RestOfWeek(String b0110,UserView userView,Connection conn)
    {
    	/*if(userView.isSuper_admin())
		{
    		b0110="UN";
		}*/
    	if(b0110==null||b0110.length()<=0) {
            b0110=userView.getUserOrgId();
        }
    	RowSet rowSet=null;    	
    	String strRest="";
    	ArrayList restList=new ArrayList();    	
    	String parentid="";
    	String save_sql="SELECT B0110, rest_weeks from kq_restofweek";
    	ContentDAO dao = new ContentDAO(conn);
    	try
    	{
    	  rowSet = dao.search(save_sql);
		  String codeitemid=b0110;
		  if(b0110.indexOf("UN")==-1)
		  {
			b0110="UN"+b0110;
		  }
		  if(!rowSet.next())
		  {   
			ArrayList list=new ArrayList();
			list.add("");
			list.add("UN");
			return list;
		  }
    	  String sql="SELECT b0110,rest_weeks from kq_restofweek where b0110 = '"+b0110+"'";    	
    	  
    	  rowSet = dao.search(sql.toString());    	   
    	   if(rowSet.next())
    	   {
    		  
    		  String rest_weeks=rowSet.getString("rest_weeks")!=null?rowSet.getString("rest_weeks").toString():"";
    		  String b0110_field=rowSet.getString("b0110")!=null?rowSet.getString("b0110").toString():"";
    		  strRest= KQRestOper.getRestStr(rest_weeks);
    		  restList.add(strRest);
    		  restList.add(b0110_field);
    	   }else{
    		   restList=getParent(parentid,codeitemid,conn);
    		  /* strRest= KQRestOper.getRestStr("");
     		   restList.add(strRest);
     		   restList.add(b0110);*/
    	   }
    	   
    	}catch(Exception e){    		
    		  e.printStackTrace();   
    		  
        }finally
	    {
            KqUtilsClass.closeDBResource(rowSet);
	    }         	
    	return restList;
    }
    
    public static ArrayList getParent(String parentid,String codeitemid,Connection conn)
    {
    	String strRest="";  
    	ArrayList restList=new ArrayList();
    	if(codeitemid.indexOf("UN")!=-1)
    	{
    		int i=codeitemid.indexOf("UN");
    		codeitemid=codeitemid.substring(i+2);
    	} 
    	
    	if(codeitemid.indexOf("UM")!=-1)
    	{
    		int i=codeitemid.indexOf("UM");
    		codeitemid=codeitemid.substring(i+2);
    	} 
    	
    	try
    	{   String b0110_filed="";
    		//do												//非su用户都不选择的时候，这里默认为su用户的公休天数；注销这改为自己的；
    		//{
    			ArrayList list=getStrRest(parentid,codeitemid,conn);
    			strRest=list.get(0).toString();
    			codeitemid=list.get(1).toString(); 
    			b0110_filed=list.get(2).toString();
    		//}
    		//while(strRest==null||strRest.length()<=0);    
    		if(strRest!=null&& "-1".equals(strRest)) {
                strRest="";
            }
    		restList.add(strRest);
    		restList.add(b0110_filed);
    	}catch(Exception e){
    		  e.printStackTrace();    		  
        }
    	
    	return restList;
    }
    
    public static ArrayList  getStrRest(String parentid,String codeitemid,Connection conn)
    {
    	String strRest="";   
    	RowSet rowSet=null;
        RowSet rs=null;
    	try
    	{  
    		String orgSql="SELECT parentid,codeitemid from organization where codeitemid='"+ codeitemid +"'";
    		ContentDAO dao = new ContentDAO(conn);
    		rowSet = dao.search(orgSql);
    		if(rowSet.next())
    		{
    			parentid=rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"UN";
    			codeitemid=rowSet.getString("codeitemid")!=null?rowSet.getString("codeitemid"):"UM";
    		} 
    		String b0100="";
    			       
            if(codeitemid==null||codeitemid.length()<=0)
    		{
    			b0100="UN";
    		}else
    	    {
    			b0100="UN"+codeitemid;
    		}
    	    String  sql="SELECT b0110,rest_weeks from kq_restofweek where b0110 = '"+b0100+"'";    	   
    	   
    	    rs = dao.search(sql.toString()); 
    	    if(rs.next())
    	    {    	     
   		      String rest_weeks=rs.getString("rest_weeks")!=null?rs.getString("rest_weeks").toString():"";
   		      String b0110_filed=rs.getString("b0110")!=null?rs.getString("b0110").toString():"";
   		      strRest= KQRestOper.getRestStr(rest_weeks);
   		      ArrayList list= new ArrayList();
   	    	  list.add(strRest);
   	    	  list.add(parentid); 
   	    	  list.add(b0110_filed); 
   	    	  return list;
    	    }else
    	    {
    	    	ArrayList realList=new ArrayList();
    	    	if(parentid!=null&&parentid.length()>0) {
                    realList = getReal(parentid,codeitemid,conn);
                } else
    	    	{
    	    		realList.add("");
    	    		realList.add("");
    	    		realList.add(codeitemid);    				
    	    	}
    	    	return realList;
//    	    	 if(parentid.equals(codeitemid))
//    	    	 {
//    	    		if(strRest==null||strRest.length()<=0)
//    	    		{
//    	    			 sql="SELECT rest_weeks from kq_restofweek where b0110 = 'UN'";  
//       	    		     rs=null;
//            	         rs = dao.search(sql.toString()); 
//            	         if(rs.next())
//            	         {
//            	    	    String rest_weeks=rs.getString("rest_weeks")!=null?rs.getString("rest_weeks"):"";
//            	    	    if(rest_weeks==null||rest_weeks.length()<=0)
//            	    	    {
//            	    	    	ArrayList list= new ArrayList();
//               	    	        list.add("-1");
//               	    	        list.add(parentid);
//               	    	        list.add("UN"); 
//               	    	        return list;
//            	    	    }
//           		            strRest= KQRestOper.getRestStr(rest_weeks);
//           		            ArrayList list= new ArrayList();
//           	    	        list.add(strRest);
//           	    	        list.add(parentid);
//           	    	        list.add("UN"); 
//           	    	        return list;
//            	          }else
//            	          {
//            	    	    strRest=KQRestOper.getRestStr("7"); 
//            	    	    ArrayList list= new ArrayList();
//            	    	    list.add(strRest);
//            	    	    list.add(parentid); 
//            	    	    list.add("UN");
//            	    	    return list;
//            	          } 
//    	    		 }       	    		 
//    	    	  }
    	       } 
        }catch(Exception e){
    		  e.printStackTrace();    		  
        }finally
	    {
            KqUtilsClass.closeDBResource(rowSet);
            KqUtilsClass.closeDBResource(rs);
	    }  
        
    	ArrayList list= new ArrayList();
   	    list.add(strRest);
   	    list.add(parentid);
   	    list.add("UN");   
   	    
   	    return list;
    }
    
    public static ArrayList getReal(String parentid,String codeitemid,Connection conn){
    	ArrayList list = new ArrayList();
    	String strRest="";
    	RowSet rowSet=null;
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(conn);
    	if(parentid.equals(codeitemid)){
	    	try{
	    		if(strRest==null||strRest.length()<=0)
	    		{
	    		 String sql="SELECT rest_weeks from kq_restofweek where b0110 = 'UN'";  
  	    		 rs=null;
       	         rs = dao.search(sql.toString()); 
       	         if(rs.next())
       	         {
       	    	    String rest_weeks=rs.getString("rest_weeks")!=null?rs.getString("rest_weeks"):"";
       	    	    if(rest_weeks==null||rest_weeks.length()<=0)
       	    	    {
       	    	    	
          	    	        list.add("-1");
          	    	        list.add(parentid);
          	    	        list.add("UN"); 
          	    	        return list;
       	    	    }
      		            strRest= KQRestOper.getRestStr(rest_weeks);
      		            
      	    	        list.add(strRest);
      	    	        list.add(parentid);
      	    	        list.add("UN"); 
      	    	        return list;
       	          }else
       	          {
       	    	    strRest=KQRestOper.getRestStr("7"); 
       	    	   
       	    	    list.add(strRest);
       	    	    list.add(parentid); 
       	    	    list.add("UN");
       	    	    return list;
       	          } 
	    		 }
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally
		    {
	    	    KqUtilsClass.closeDBResource(rs);
		    } 
    	}else{
    		try{
        		String orgSql="SELECT parentid,codeitemid from organization where codeitemid='"+ parentid +"'";
        	
        		rowSet = dao.search(orgSql);
        		if(rowSet.next())
        		{
        			parentid=rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"UN";
        			codeitemid=rowSet.getString("codeitemid")!=null?rowSet.getString("codeitemid"):"UM";
        		} 
        		String b0100="";
        			       
                if(codeitemid==null||codeitemid.length()<=0)
        		{
        			b0100="UN";
        		}else
        	    {
        			b0100="UN"+codeitemid;
        		}
                
                String  sql="SELECT b0110,rest_weeks from kq_restofweek where b0110 = '"+b0100+"'";    	   
        	    rs = dao.search(sql.toString()); 
        	    if(rs.next())
        	    {    	     
       		      String rest_weeks=rs.getString("rest_weeks")!=null?rs.getString("rest_weeks").toString():"";
       		      String b0110_filed=rs.getString("b0110")!=null?rs.getString("b0110").toString():"";
       		      strRest= KQRestOper.getRestStr(rest_weeks);
       		  
       	    	  list.add(strRest);
       	    	  list.add(parentid); 
       	    	  list.add(b0110_filed); 
       	    	  return list;
        	    }else{
        	    	if(parentid!=null&&parentid.length()>0) {
                        list = getReal(parentid,codeitemid,conn);
                    } else
        	    	{
        	    		 list.add("");
              	    	 list.add(""); 
              	    	 list.add(codeitemid); 
        	    	}
        	    }
        		
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally
		    {
    		    KqUtilsClass.closeDBResource(rowSet);
		    } 
    	}
    	return list;
    }
    
    /**
     * 检索考勤业务表_倒休     
     * @param cur_date
     *              考勤期间
     * @return   得到倒休日期     
     * */
    public static String getTurn_Date(String b0110,String cur_date,Connection conn)
    {
    	String turn_date="";    	
    	StringBuffer dateSQL= new StringBuffer();    	
        dateSQL.append("SELECT ");
        dateSQL.append(" b0110,turn_date");
		dateSQL.append(" from kq_turn_rest where ");
		dateSQL.append(" week_date="+Sql_switcher.dateValue(cur_date));	
		dateSQL.append(" and b0110='"+b0110+"'");
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rowSet=null;
 	    try
 	    {
 	       rowSet=dao.search(dateSQL.toString());
	       if(rowSet.next())
	       {
	    	  Date d1=rowSet.getDate("turn_date");
		      SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
	    	  turn_date=format1.format(d1);		    	
	       }
 	    }catch(Exception e){
 	    	e.printStackTrace(); 	    	
 	    }finally
	    {
 	       KqUtilsClass.closeDBResource(rowSet);
	    } 
    	return turn_date;
    }
    /**
     * 检索考勤业务表_倒休_不是公休日,  
     * @param cur_date
     *              考勤期间
     * @return   得到倒休日期     
     * */
    public static String getWeek_Date(String b0110,String cur_date,Connection conn)
    {
    	String week_date="";    	
    	StringBuffer dateSQL= new StringBuffer();
    	
        dateSQL.append("SELECT ");
        dateSQL.append(" b0110,week_date");
		dateSQL.append(" from kq_turn_rest where ");
		dateSQL.append(" turn_date="+Sql_switcher.dateValue(cur_date)); 
		dateSQL.append(" and b0110='"+b0110+"'");
		ContentDAO dao = new ContentDAO(conn);
    	RowSet rowSet=null;
    	
 	    try
 	    {
 	       rowSet=dao.search(dateSQL.toString());
	       if(rowSet.next())
	       {
	    	  Date d1=rowSet.getDate("week_date");
	    	  SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
	    	  week_date=format1.format(d1);	    	
	       }
 	    }catch(Exception e){
 	    	e.printStackTrace(); 	    	
 	    }finally
	    {
 	       KqUtilsClass.closeDBResource(rowSet);
	    } 
    	return week_date;
    }
    
    /**
     * 此方法用于公休日到休设置
     * @param cur_date
     * @param userView
     * @param rest_date
     * @param b0110
     * @param conn
     * @return
     */
    public static String is_RestDate2(String cur_date,UserView userView,String rest_date,String b0110,Connection conn){
    	String rest_state=ResourceFactory.getProperty("kq.date.work");
    	String restdate=ResourceFactory.getProperty("kq.date.rest");
		String feast_name=if_Feast(cur_date,conn);
		if(feast_name!=null&&feast_name.length()>0)//判断是不是节假日
		{
			String turn_date=getTurn_Date(b0110,cur_date,conn);
			if(turn_date==null||turn_date.length()<=0)			
			{
				rest_state=feast_name;
			}
			
		}else{
			//String rest_date=search_RestOfWeek(b0110,userView); 
			if(if_Rest(cur_date,userView,rest_date))//判断公休日
		     {   
			    rest_state=restdate;			     
		     }
		}
		return rest_state;
	}
}
