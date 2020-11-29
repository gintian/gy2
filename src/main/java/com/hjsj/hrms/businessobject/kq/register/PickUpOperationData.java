package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
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
import java.util.Date;
import java.util.List;

public class PickUpOperationData {
    /**
     * 提取业务数据
     * */
	private Connection conn=null;	
	private UserView userView=null;	
	public PickUpOperationData()
	{
		
	}
	public PickUpOperationData(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;		
		
	}
	/**********请假***********/
	/**
	 * @param userbase  库前缀
     * @param e0122 部门编号
	 * @param start_date 开始时间
	 * @param end_date  结束时间
	 * @param whereIN  人员库权限
	 * @param columnlist 可操作字段
	 * @param rest_date 休息日
	 * @param timelist  工作时间
	 * @param rest_b0110 休息日单位代码
	 * */
    public String kq_Q15(String userbase,String a0100_num,String b0110,String start_date,String end_date,String whereIN,ArrayList columnlist,String rest_date,ArrayList timelist,String rest_b0110,String kqTempTable,String kq_type)throws GeneralException
    {
    	StringBuffer sql= new StringBuffer();
    	sql.append("select a0100,e0122,q1503,q15z1,q15z3 from Q15 ");
    	sql.append(" where q15z5=? "); 
    	sql.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"' ");
    	if(a0100_num!=null&&a0100_num.length()>0)
    	{
    		sql.append(" and a0100='"+a0100_num+"' ");
    	}
    	sql.append(KQRestOper.kq_Q15_dateSQL(start_date,end_date));
    	sql.append(" and b0110=?");
        sql.append(" and q15z0=?"); 
        sql.append(" and a0100 in(select a0100 "+whereIN+")");
        
    	ArrayList list=new ArrayList();
    	list.add("03");    	
    	list.add(b0110);
        list.add("01");    	
    	String fielditemid="";    	
    	RowSet rowSet=null;
    	String[] sw_times=KQRestOper.getTimeSlice (timelist.get(0).toString()); 
    	String sb_time=sw_times[0];
    	String[] ed_times=KQRestOper.getTimeSlice (timelist.get(timelist.size()-1).toString());
    	String ed_time=ed_times[1];
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);  
    		rowSet=dao.search(sql.toString(),list);
    		
    		//检索员工是否在当前考勤日期,有请假    	   
    	    while(rowSet.next())
    		{
    	    	String a0100=rowSet.getString("a0100");  
    	    	String e0122=rowSet.getString("e0122");   
    			String leave_type=rowSet.getString("q1503");    			
    			//String start_str= rowSet.getString("q15z1");
    			Date start_d=rowSet.getDate("q15z1");
    			Date start_t=rowSet.getTime("q15z1");
    			String start_str_d=DateUtils.format(start_d,"yyyy-MM-dd");
    			String start_str_t=DateUtils.format(start_t,"HH:mm");
    			String start_str=start_str_d+" "+start_str_t;
    			Date kq_d_start=DateUtils.getDate(start_str,"yyyy-MM-dd HH:mm"); 
    			Date end_d=rowSet.getDate("q15z3");
    			Date end_t=rowSet.getTime("q15z3");
    			String end_str_d=DateUtils.format(end_d,"yyyy-MM-dd");
    			String end_str_t=DateUtils.format(end_t,"HH:mm");
    			String end_str=end_str_d+" "+end_str_t;    			
    	    	Date  kq_d_end=DateUtils.getDate(end_str,"yyyy-MM-dd HH:mm");   	    	
    	    	String op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd");
    	    	String op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd"); 
    	    	String end_op_date=DateUtils.format(kq_d_end,"yyyy-MM-dd"); 
    	    	/*********判断时间是否是00：00********/
    	    	Date op_start_date=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
    	    	String kq_sta_hhmm=DateUtils.format(kq_d_start,"HH:mm");
    	    	String kq_end_hhmm=DateUtils.format(kq_d_end,"HH:mm");
    	    	/********判断请假开始时间是否小于上班时间*******/    	    	
    	    	float f_sb=KQRestOper.toHourFormMinute(kq_d_start,op_start_date);
    	    	if(kq_sta_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_start=op_start_date;
    	    	}else if(f_sb>0)
    	    	{
    	    		kq_d_start=op_start_date;
    	    	}
    	    	if(kq_end_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_end=DateUtils.getDate(end_op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	/***********结束**********/
    	    	
    	    	ArrayList itemlist=count_Leave(leave_type);
    	    	/*******考勤项目内没有定义********/
    	    	if(itemlist!=null&&itemlist.size()>0)
    	    	{
    	    		fielditemid=itemlist.get(0).toString(); 
    	    		if(fielditemid==null||fielditemid.length()<=0)
    	    		{
    	    			continue ;
    	    		}
        	    	String has_rest=itemlist.get(1).toString();
        	    	String has_feast=itemlist.get(2).toString();
        	    	String item_unit=itemlist.get(4).toString();
        	    	//得到时间差
        	    	float diffhour=KQRestOper.toHourFormMinute(kq_d_start,kq_d_end);
        	    	int diff=RegisterDate.diffDate(kq_d_start,kq_d_end);    	    	
        	    	float work_time=KQRestOper.getWork_Time(timelist);
        	    	String lxtype=KQRestOper.getLXtype(columnlist,fielditemid);    	    	   	    	   
        	    	boolean is_annual=false;
        	    	 	        
        	    	     
        	        
        	    	for(int i=0;i<=diff;i++)
           	    	{  
        	    	  float unit=0; 
        	    	  if(i>0)
           	    	  {
        	    		  kq_d_start=DateUtils.addDays(kq_d_start,1);    	    		  
        	    	      op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd");
        	    	      op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd");   	    	      
           	    	  }
        	    	  if(diff>0)
        	    	  {    	    		  
        	    		 if(diff==i)
        	    		 {
        	    			 String[] times=KQRestOper.getTimeSlice (timelist.get(0).toString());    	    			 
        	    			 kq_d_start=DateUtils.getDate(op_date+" "+times[0],"yyyy-MM-dd HH:mm");
        	    			 Date op_end_date=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
        	    			 float f_xb=KQRestOper.toHourFormMinute(op_end_date,kq_d_end);
        	    			 if(f_xb>0)
        	    			 {
        	    				 kq_d_end=op_end_date;
        	    			 }
        	    			 float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,kq_d_start,kq_d_end,timelist);
        	    		     float l_unit= diffhour-i*24;
        	    			 if(l_unit>work_time)
        	    			 {
        	    			     unit=KQRestOper.con_Unit(item_unit,work_time,work_time);    	    			     
        	    			 }else
        	    			 {
        	    				 unit=count_Unit(item_unit,work_time,rest_diff_time);     	    				
        	    			 }
        	    		  }else{
        	    			  if(i==0)
        	    			  {
        	    				     	    			 
        	    				  Date d_end=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
        	    				  float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,kq_d_start,d_end,timelist);  
        	    				  unit=count_Unit(item_unit,work_time,rest_diff_time); 
        	    			  }else{
        	    				 unit=KQRestOper.con_Unit(item_unit,work_time,work_time);   
        	    			  }
        	    		  }    	    		  
        	    	  }else
        	    	  {
//        	    		  Date op_end_date=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
//     	    			  float f_xb=KQRestOper.toHourFormMinute(op_end_date,kq_d_end);
//     	    			  if(f_xb>0)
//     	    			  {
//     	    				 kq_d_end=op_end_date;
//     	    			  }
        	    		  if(diffhour<work_time)
        	    		  {
        	    			  
        	    			  float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,kq_d_start,kq_d_end,timelist);  
        	    			  unit=count_Unit(item_unit,work_time,rest_diff_time);  
        	    		  }else
        	    		  {
        	    			  unit=KQRestOper.con_Unit(item_unit,work_time,work_time); 
        	    		  }
        	    	  }
        	       
        	       if("06".equals(leave_type))
        	       {
        	    	   is_annual=kq_Q17(a0100,userbase,op_date,unit);
        	    		  /**判断是否是年假，如果是，看是否超出年假范围**/
        	    	   if(is_annual)
        	    	   {
        	    		    String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
        					if(feast_name!=null&&feast_name.length()>0)
        					{
        						String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
        						if(turn_date==null||turn_date.length()<=0)			
        						{
        							if("1".equals(has_feast))
             	    	            {
            	    		    	   if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
            	    		    	   {
            	    		    		  restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
            	    		    	   }
            	    		    	   kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);	//计算
             	    	    			
             	    	    	    } 
        						}else
        						{
        							 if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
        							 {
        								  if("1".equals(has_rest))
        	    	    	    		  {
        								     saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        	    	    	    		  }
        	    	    	    	 }else
        							 {
        	    	    	    		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);	
        							 } //计算
        						}
        	    		         
        	    			}else if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
        	    	    	{
        	    				 String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
        	    				 if(turn_date==null||turn_date.length()<=0)			
         						{
        	    					 if("1".equals(has_rest))
            	    	    		 {
            	    	    			if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
                	    		    	{
                	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
                	    		    	}
            	    	    			kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);	//计算
            	    	    			
            	    	    		  }
         						}else
         						{
         							if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
            	    		    	{
            	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
            	    		    	}
         							kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);//计算
         						}
        	    	    		 
        	    	    	}else
        	    		    {
        	    	    		kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
        	    			    
        	    		    }
        	    	      }
        	    	   }else
        	    	   {  
        	    		   String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
       					   if(feast_name!=null&&feast_name.length()>0)
        	    		   {
       						   String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
       						   if(turn_date==null||turn_date.length()<=0)			
    						   {
    							  if("1".equals(has_feast))
         	    	              {
        	    		    	     if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
        	    		    	     {
        	    		    		    restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
        	    		    	     }
        	    		    	     kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
         	    	    		  } 
    						  }else
    						  {
    							  if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
    							 {
    								  if("1".equals(has_feast))
    	    	    	    		  {
    								     saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
    	    	    	    		  }
    	    	    	    	 }else
    							 {
    	    	    	    		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);	
    							 }    							
    						  }
        	    		   }else if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
        	    	       {
        	    			 
        	    		      String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
        	    		      //判断是否在倒休日里请假    	    		      
        	    		      if(turn_date!=null&&turn_date.length()>0)
    	    				  {
        	    		    	  if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
          	    		    	  {
          	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
          	    		    	  }
        	    		    	  kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
    	    					 
    	    				  }else
    	    				  {
    	    	    		     if("1".equals(has_rest))
    	    	    		     {
    	    	    		    	 if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
             	    		    	 {
             	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
             	    		    	 }
    	    	    		    	 kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
    	    	    			 }
    	    				  }    	    		  
        	    	      }else
        	    	      {  
        	    	    	  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date,this.conn);
        	    	          if(week_date==null||week_date.length()<=0)
        	    	          {
        	    	        	  if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
          	    		    	  {
          	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
          	    		    	  }
        	    	        	  kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
        	    	          }else
        	    	          {
        	    	        	  if("1".equals(has_rest))
     	    	    		     {
     	    	    		    	 if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
              	    		    	 {
              	    		    		 restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
              	    		    	 }
     	    	    		    	 kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
     	    	    			 }
        	    	          }//计算
        	    	      }
        	            }
           	         }
    	    	}
    	    	 
    		}
    	   	    	
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
        
    	return fielditemid;
    	
    }

    /***************请假结束*****************/
    /***************加班******************
     * kq_overtime
     */
    public String kq_Q11(String userbase,String a0100_num,String b0110,String start_date,String end_date,String whereIN,ArrayList columnlist,String rest_date,ArrayList timelist,String rest_b0110,String kqTempTable,String kq_type)throws GeneralException
    {
    	RowSet rowSet=null;
    	ContentDAO dao = new ContentDAO(this.conn);  
    	StringBuffer sql= new StringBuffer();
    	sql.append("select a0100,e0122,q1103,q11z1,q11z3 from q11 ");
    	sql.append(" where q11z5=?");
    	sql.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"' ");
    	if(a0100_num!=null&&a0100_num.length()>0)
    	{
    		sql.append(" and a0100='"+a0100_num+"' ");
    	}
    	sql.append(KQRestOper.kq_Q11_dateSQL(start_date,end_date));
    	sql.append(" and b0110=?");
        sql.append(" and q11z0=?"); 
        sql.append(" and a0100 in(select a0100 "+whereIN+")");
    	ArrayList list=new ArrayList();
    	list.add("03");     	
    	list.add(b0110);
        list.add("01");  	
    	String fielditemid="";  
    	String[] sw_times=KQRestOper.getTimeSlice (timelist.get(0).toString()); 
    	String sb_time=sw_times[0];
    	String[] ed_times=KQRestOper.getTimeSlice (timelist.get(timelist.size()-1).toString());
    	String ed_time=ed_times[1];
    	try
    	{
    	    rowSet=dao.search(sql.toString(),list);
    		//检索员工是否在当前考勤日期,有加班
    	    while(rowSet.next())
    		{
    			String a0100=rowSet.getString("a0100");
    			String e0122=rowSet.getString("e0122");
    			String overtime_type=rowSet.getString("q1103");    			
    			Date start_d=rowSet.getDate("q11z1");
    			Date start_t=rowSet.getTime("q11z1");
    			String start_str_d=DateUtils.format(start_d,"yyyy-MM-dd");
    			String start_str_t=DateUtils.format(start_t,"HH:mm");
    			String start_str=start_str_d+" "+start_str_t;
    			Date kq_d_start=DateUtils.getDate(start_str,"yyyy-MM-dd HH:mm"); 
    			Date end_d=rowSet.getDate("q11z3");
    			Date end_t=rowSet.getTime("q11z3");
    			String end_str_d=DateUtils.format(end_d,"yyyy-MM-dd");
    			String end_str_t=DateUtils.format(end_t,"HH:mm");
    			String end_str=end_str_d+" "+end_str_t;    			
    	    	Date  kq_d_end=DateUtils.getDate(end_str,"yyyy-MM-dd HH:mm");   	    	
    	    	String op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd");
    	    	String op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd");	
    	    	String end_op_date=DateUtils.format(kq_d_end,"yyyy-MM-dd"); 
    	    	/*********判断时间是否是00：00********/
    	    	String kq_sta_hhmm=DateUtils.format(kq_d_start,"HH:mm");
    	    	String kq_end_hhmm=DateUtils.format(kq_d_end,"HH:mm");
    	    	if(kq_sta_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_start=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	if(kq_end_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_end=DateUtils.getDate(end_op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	/***********结束**********/
    	    	ArrayList itemlist=count_Leave(overtime_type);
    	    	/*******考勤项目内没有定义********/
    	    	if(itemlist!=null&&itemlist.size()>0)
    	    	{
    	    		fielditemid=itemlist.get(0).toString();
    	    		if(fielditemid==null||fielditemid.length()<=0)
    	    		{
    	    			continue ;
    	    		}
        	    	String has_rest=itemlist.get(1).toString();
        	    	String has_feast=itemlist.get(2).toString();
        	    	String item_unit=itemlist.get(4).toString();
        	    	//得到时间差
        	    	//float diffhour=KQRestOper.toHourFormMinute(kq_d_start,kq_d_end);
        	    	int diff=RegisterDate.diffDate(kq_d_start,kq_d_end);
        	    	   	
        	    	float work_time=KQRestOper.getWork_Time(timelist);
        	    	String lxtype=KQRestOper.getLXtype(columnlist,fielditemid);  	    	
        	    	
        	        for(int i=0;i<=diff;i++)
           	    	{  
      	    	        float unit=0; 
      	    	        if(i>0)
         	    	    {
      	    		     kq_d_start=DateUtils.addDays(kq_d_start,1);	
      	    	         op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd");
      	    	         op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd"); 	    	      
         	    	    }
      	    	        if(diff>0)
      	    	        {    	    	
      	    	    	  
      	    		      if(diff==i)
      	    		      {
      	    		    	String[] times=KQRestOper.getTimeSlice (timelist.get(0).toString());    	    			 
       	    			    kq_d_start=DateUtils.getDate(op_date+" "+times[0],"yyyy-MM-dd HH:mm");
       	    			    float rest_diff_time=KQRestOper.getTime_StartDiff( op_date,kq_d_start,kq_d_end,timelist,"1");
       	    		        unit=count_Unit(item_unit,work_time,rest_diff_time);     	    				
      	    			  }else
      	    			  {
      	    		    	 if(i==0)
      	    			     {
      	    		    		Date d_end=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
      	    				    float rest_diff_time=KQRestOper.getTime_StartDiff( op_date,kq_d_start,d_end,timelist,"0");  
      	    				    unit=count_Unit(item_unit,work_time,rest_diff_time); 
      	    			     }else{
      	    				    unit=KQRestOper.con_Unit(item_unit,work_time,work_time);   
      	    			     }
      	    		     }    	    		  
      	    	       }else
      	    	       {
      	    	    	   float rest_diff_time=0;
      	    	    	   rest_diff_time=KQRestOper.getTime_StartDiff(op_date,kq_d_start,kq_d_end,timelist,"3");
      	    	    	    	    	    	   
      	    	    	   unit=count_Unit(item_unit,work_time,rest_diff_time);  
      	    		   } 
                      String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
  					   if(feast_name!=null&&feast_name.length()>0)
        	    	   {
  						   String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
						   if((turn_date==null||turn_date.length()<=0)&&"11".equals(overtime_type))			
						   {
							  
							   saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
	        	    		 
						   }else if("11".equals(overtime_type))
						   {
							   
							   saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
						   }else
						   {
							   if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
  							   {
  								  if("1".equals(has_feast))
  	    	    	    		  {
  								     saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
  	    	    	    		  }
  	    	    	    	   }else
  							   {
  	    	    	    		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);	
  							   } 
						   }
        	    		  
        	    	   }else
        	    	   {
        	    	      if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
        	    	      {
        	    	    	 String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
        	    	    		     //判断是否在倒休日里请假
        	    	    	 if((turn_date==null||turn_date.length()<=0)&&"10".equals(overtime_type))
        		    		 {    	    	    			  
        	    	    	    
        	    	    		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
      		    	    			  
      		    	    		
        		    		 }else
        		    		 {
        		    			 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        		    		 }    	    	    		  
        	    	      }else
        	    	      {
        	    	    	  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date,this.conn);       	    	    	  
        	    	          if(week_date!=null&&week_date.length()>0&&"10".equals(overtime_type))
        	    	          {
        	    	        	  
        	    	        	  saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        		    	    	  
        	    	          }else
        	    	          {
        	    	        	     if(!"10".equals(overtime_type)&&!"11".equals(overtime_type))
        	    	        	     {
        	    	        	    	 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        	    	        	    }
          	    	    	        
        		    	     }
        	    	     }
        	    	  }
        		  } 
    	    	}
    	    	   	       
    	   }  	
    	}catch(Exception e){
    		e.printStackTrace();
    		//throw GeneralExceptionHandler.Handle(e);
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
    	return fielditemid;
    }
    /*************加班结束***************/
    /***************公出******************
     * 
     */
    public String kq_Q13(String userbase,String a0100_num,String b0110,String start_date,String end_date,String whereIN,ArrayList columnlist,String rest_date,ArrayList timelist,String rest_b0110,String kqTempTable,String kq_type)throws GeneralException
    {
    	RowSet rowSet=null;
    	ContentDAO dao = new ContentDAO(this.conn);  
    	StringBuffer sql= new StringBuffer();
    	sql.append("select a0100,e0122,q1303,q13z1,q13z3 from Q13 ");
    	sql.append(" where q13z5=?"); 
    	sql.append(" and nbase='"+userbase.toUpperCase()+"' ");
    	if(a0100_num!=null&&a0100_num.length()>0)
    	{
    		sql.append(" and a0100='"+a0100_num+"' ");
    	}
    	sql.append(KQRestOper.kq_Q13_dateSQL(start_date,end_date));
    	sql.append("and b0110=? ");
        sql.append("and q13z0=?"); 
        sql.append(" and a0100 in(select a0100 "+whereIN+")");
    	ArrayList list=new ArrayList();
    	list.add("03");    	
    	list.add(b0110);
        list.add("01");    	
    	String fielditemid="";    
    	String[] sw_times=KQRestOper.getTimeSlice (timelist.get(0).toString()); 
    	String sb_time=sw_times[0];    	
    	String[] ed_times=KQRestOper.getTimeSlice (timelist.get(timelist.size()-1).toString());
    	String ed_time=ed_times[1];
    	try
    	{
    	    rowSet=dao.search(sql.toString(),list);
    		//检索员工是否在当前考勤日期,有加班
    	    while(rowSet.next())
    		{
    			String a0100=rowSet.getString("a0100");
    			String e0122=rowSet.getString("e0122");
    			String away_type=rowSet.getString("q1303");    			
    			Date start_d=rowSet.getDate("q13z1");
    			Date start_t=rowSet.getTime("q13z1");
    			String start_str_d=DateUtils.format(start_d,"yyyy-MM-dd");
    			String start_str_t=DateUtils.format(start_t,"HH:mm");
    			String start_str=start_str_d+" "+start_str_t;
    			Date kq_d_start=DateUtils.getDate(start_str,"yyyy-MM-dd HH:mm"); 
    			Date end_d=rowSet.getDate("q13z3");
    			Date end_t=rowSet.getTime("q13z3");
    			String end_str_d=DateUtils.format(end_d,"yyyy-MM-dd");
    			String end_str_t=DateUtils.format(end_t,"HH:mm");
    			String end_str=end_str_d+" "+end_str_t;    			
    	    	Date  kq_d_end=DateUtils.getDate(end_str,"yyyy-MM-dd HH:mm");   	    	
    	    	String op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd");
    	    	String op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd");	
    	    	String end_op_date=DateUtils.format(kq_d_end,"yyyy-MM-dd"); 
    	    	/*********判断时间是否是00：00********/
    	    	String kq_sta_hhmm=DateUtils.format(kq_d_start,"HH:mm");
    	    	String kq_end_hhmm=DateUtils.format(kq_d_end,"HH:mm");
    	    	if(kq_sta_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_start=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	if(kq_end_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		kq_d_end=DateUtils.getDate(end_op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	/***********结束**********/
    	    	ArrayList itemlist=count_Leave(away_type);
    	    	/*******考勤项目内没有定义********/
    	    	if(itemlist!=null&&itemlist.size()>0)
    	    	{
    	    		fielditemid=itemlist.get(0).toString();
    	    		if(fielditemid==null||fielditemid.length()<=0)
    	    		{
    	    			continue ;
    	    		}
        	    	String has_rest=itemlist.get(1).toString();
        	    	String has_feast=itemlist.get(2).toString();
        	    	String item_unit=itemlist.get(4).toString();
        	    	//得到时间差
        	    	
        	    	//float diffhour=KQRestOper.toHourFormMinute(kq_d_start,kq_d_end);
        	    	int diff=RegisterDate.diffDate(kq_d_start,kq_d_end); 	
                      	    	
        	    	float work_time=KQRestOper.getWork_Time(timelist);	    	
        	    	String lxtype=KQRestOper.getLXtype(columnlist,fielditemid); 
        	    	  	       
       	            
       	           
        	    	for(int i=0;i<=diff;i++)
        	    	{  
        	    		float unit=0; 
      	    	        if(i>0)
         	    	    {
      	    		     kq_d_start=DateUtils.addDays(kq_d_start,1);	
      	    	         op_date=DateUtils.format(kq_d_start,"yyyy-MM-dd");
      	    	         op_date_to=DateUtils.format(kq_d_start,"yyyy.MM.dd"); 	    	      
         	    	    }
      	    	        if(diff>0)
      	    	        {    	    	
      	    	    	  
      	    		      if(diff==i)
      	    		      {
      	    		    	String[] times=KQRestOper.getTimeSlice (timelist.get(0).toString());    	    			 
       	    			    kq_d_start=DateUtils.getDate(op_date+" "+times[0],"yyyy-MM-dd HH:mm");
       	    			    float rest_diff_time=KQRestOper.getTime_StartDiff( op_date,kq_d_start,kq_d_end,timelist,"1");
       	    		        unit=count_Unit(item_unit,work_time,rest_diff_time);     	    				
      	    			  }else
      	    			  {
      	    		    	 if(i==0)
      	    			     {
      	    		    		Date d_end=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
      	    				    float rest_diff_time=KQRestOper.getTime_StartDiff( op_date,kq_d_start,d_end,timelist,"0");  
      	    				    unit=count_Unit(item_unit,work_time,rest_diff_time); 
      	    			     }else{
      	    				    unit=KQRestOper.con_Unit(item_unit,work_time,work_time);   
      	    			     }
      	    		      }    	    		  
      	    	       }else
      	    	       {
      	    	    	   float rest_diff_time=KQRestOper.getTime_StartDiff( op_date,kq_d_start,kq_d_end,timelist,"3");
      	    	    	   unit=count_Unit(item_unit,work_time,rest_diff_time);  
      	    		   } 	
      	    	       String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
  					   if(feast_name!=null&&feast_name.length()>0)
        	    	   {
  						  String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
  						  if(turn_date==null||turn_date.length()<=0)			
  						  {
  							 if("1".equals(has_feast))
          	    	         {
  								 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
           	    		     }
  						  }else
  						  {
  							  if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
							  {
								  if("1".equals(has_feast))
	    	    	    		  {
								     saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
	    	    	    		  }
	    	    	    	  }else
							  {
	    	    	    		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);	
							  } 
  						  }        	    	      
        	    	   }else if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
        	    	   {
        	    		   String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
	    	    		     //判断是否在倒休日里请假
	    	    	       if(turn_date==null||turn_date.length()<=0)
		    		       { 
        	    	           if("1".equals(has_rest))
        	    		      {
        	    	        	   saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        	    		      }
		    		       }else
		    		       {
		    		    	   saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
		    		       }
        	    	   }else
        	    	   {
        	    		   String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date,this.conn);
     	    	           if(week_date==null||week_date.length()<=0)
     	    	           {
     	    	        	  saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
     	    	           }else
     	    	           {
     	    	        	  if("1".equals(has_rest))
        	    		      {
     	    	        		 saveRecold(kqTempTable,a0100,b0110,e0122,userbase,fielditemid,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);
        	    		      }
     	    	           }
     	    	       }
     	    	           
     	    	           
           	    	 }//for结束
    	    	}//结束
    	    	
    	    }//while结束    		
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
    	return fielditemid;
    }
    /*************公出结束***************/
    /********年休******/
    public boolean kq_Q17(String a0100,String userbase,String cur_date,float unit)throws GeneralException
    {
    	boolean isCorrect = false;
    	StringBuffer strSQL=new StringBuffer();
    	String cur_year=cur_date.substring(0,4);
    	strSQL.append("select q1703,q1705,q1707 from q17 ");
    	strSQL.append("where UPPER(nbase)='"+userbase.toUpperCase()+"' ");
    	strSQL.append("and A0100='"+a0100+"' ");
    	strSQL.append(" and q1701='"+cur_year+"' ");    	
    	strSQL.append(KQRestOper.kq_Q17_dateSQL(cur_date)); 
    	ContentDAO dao1 = new ContentDAO(this.conn);
    	RowSet row=null;
    	try
    	{
    		row = dao1.search(strSQL.toString());
    		if(row.next())
    		{
    			float rest_days=row.getFloat("q1703");
    			float rested_days=row.getFloat("q1705");
    			float resting_days=row.getFloat("q1707");
    			if(rest_days>=rested_days&&resting_days>=0&&rest_days>0) {
                    isCorrect=true;
                }
    			
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally
	     {
			if(row!=null) {
                try {
                    row.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
    	return isCorrect;
    	
    }
    /**
     * 修改年休表
     * @param rested_days 已休天数
     * @param resting_days 可休天数
     * 
     * 
    public void update_Annual_Rest(String a0100,String userbase,String cur_year,float rested_days,float resting_days,float unit)throws GeneralException
    {
    	StringBuffer sql=new StringBuffer();
    	float q1705=rested_days+unit;
    	float q1707=resting_days-unit;
    	sql.append("update q17 set ");
    	sql.append(" q1705 ='"+q1705+"',q1707 ='"+q1707+"' ");
    	sql.append(" where a0100='"+a0100+"' and nbase='"+userbase+"' and q1701='"+cur_year+"'");    	
    	ContentDAO dao = new ContentDAO(this.conn);  
    	try
    	{
    		dao.update(sql.toString());
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);	
    	}
    }
    /*********年休结束*********/
    /**
     * 修改纪录
     * @param a0100
     *               人员编号
     * @param userbase 
     *            当前人员库前缀
     * @param cur_date
     *            考期日期
     *            
     * */
    public void updateRecord2(String a0100,String userbase,String op_date,String fielditemid,float unit,String lxtype,String start_date,String end_date)throws GeneralException
    {
    	Date d_cur=DateUtils.getDate(op_date,"yyyy.MM.dd");
    	Date d_start=DateUtils.getDate(start_date,"yyyy.MM.dd");
    	Date d_end=DateUtils.getDate(end_date,"yyyy.MM.dd");
    	int diff_end=DateUtils.dayDiff(d_cur,d_end);
    	int diff_start=DateUtils.dayDiff(d_start,d_cur);  
    	
    	if(diff_end>=0&&diff_start>=0)
    	{
    	   StringBuffer updateSQL=new StringBuffer();
    	   updateSQL.append("update Q03 set ");
    	   updateSQL.append( fielditemid+"=? " );
    	   updateSQL.append(" where a0100=? " );
    	   updateSQL.append(" and Q03Z0=? " );
    	   updateSQL.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"'" ); 
    	   updateSQL.append(" and Q03Z5 in ('01','07')");    	   
    	   ArrayList valuelist= new ArrayList();
    	   if("float".equals(lxtype)){
    	       valuelist.add(new Float(unit));
    	   }else{
    	       valuelist.add(new Integer((int)unit));
    	   }    	
    	   valuelist.add(a0100);
    	   valuelist.add(op_date);    	       	   
    	   ContentDAO dao = new ContentDAO(this.conn);
    	   try{    		
    	      dao.update(updateSQL.toString(),valuelist);
    	      
           }catch(Exception e){
        	  e.printStackTrace();
        	  throw GeneralExceptionHandler.Handle(e);
           }
       }
    } 
    /**
     * 查找休息日该员工是否有记录
     * **/
    public boolean if_EmpRecord(String nbase,String a0100,String b0110,String whereIN,String cur_date)throws GeneralException
    {
       boolean isCorrect=false;
       StringBuffer strsql=new StringBuffer();       
 	   strsql.append("select ");	   
 	   strsql.append("A0100,B0110,E0122,A0101,E01A1,Q03Z3"); 	   
 	   strsql.append(" from "); 	   
 	   strsql.append(" q03 "); 	   
 	   strsql.append(" where A0100='"+a0100+"'"); 	 
 	   strsql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"' and b0110='"+b0110+"' and q03z0='"+cur_date+"'"); 	  
 	   strsql.append(" and a0100 in(select a0100 "+whereIN+")"); 	   
 	   RowSet rowSet=null; 	   
 	   ContentDAO dao = new ContentDAO(this.conn); 	  
 	   try
 	   {
 		  rowSet=dao.search(strsql.toString()); 	 	   
 	 	   if(rowSet.next())
 	 	   {
 	 		 isCorrect=true;
 	 	   }
 	   }catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 	     
 	   return isCorrect;
    }
    /**
     * 节假日添加数据
     * @param a0100
     *               人员编号
     * @param userbase 
     *            当前人员库前缀
     * @param cur_date
     *            考期日期
     *            
     * */
    public void restInterRecord(String a0100,String b0110,String nbase,String op_date,float unit,String lxtype,String start_date,String end_date,String whereIN,String rest_date,String kq_type)throws GeneralException
    {
    	Date d_cur=DateUtils.getDate(op_date,"yyyy.MM.dd");
    	Date d_start=DateUtils.getDate(start_date,"yyyy.MM.dd");
    	Date d_end=DateUtils.getDate(end_date,"yyyy.MM.dd");
    	int diff_end=DateUtils.dayDiff(d_cur,d_end);
    	int diff_start=DateUtils.dayDiff(d_start,d_cur);  
    	
    	if(diff_end>=0&&diff_start>=0)
    	{
    	   RecordVo vo= selectEmpRecord(nbase,a0100,b0110,whereIN,op_date,rest_date,kq_type);    	   
    	   if(vo!=null)
    	   {
    		   ContentDAO dao = new ContentDAO(this.conn);
        	   try{         		  
        	      dao.addValueObject(vo);        	      
               }catch(Exception e){
            	  e.printStackTrace();
            	  throw GeneralExceptionHandler.Handle(e);
               } 
    	   }
    	   
       }
    } 
    /**
     *  通过类型操作考勤项目标,计算请假
     *   @param item_id 
     *                   类型,项目编号
     *   @param  start_date
     *                    开始时间
     *   @param  end_date
     *                    结束时间
     *   @return                 
     **/  
    public ArrayList count_Leave(String item_id)throws GeneralException
    {
    	List rs=null;
    	ArrayList itemlist= new ArrayList();
    	String kq_item_sql="select has_rest,has_feast,item_unit,fielditemid,c_expr from kq_item where item_id='"+item_id+"'";    	    	
    	
    	try
    	{
    	   rs = ExecuteSQL.executeMyQuery(kq_item_sql.toString(),this.conn);
    	   if(!rs.isEmpty())
    	   { 
    		   LazyDynaBean rec=(LazyDynaBean)rs.get(0);
    		   itemlist.add(0,rec.get("fielditemid")!=null?rec.get("fielditemid").toString():"");
    		   itemlist.add(1,PubFunc.DotstrNull(rec.get("has_rest")!=null?rec.get("has_rest").toString():""));
    		   itemlist.add(2,PubFunc.DotstrNull(rec.get("has_feast")!=null?rec.get("has_feast").toString():""));
    		   itemlist.add(3,PubFunc.DotstrNull(rec.get("c_expr")!=null?rec.get("c_expr").toString():""));    		   
    		   itemlist.add(4,PubFunc.DotstrNull(rec.get("item_unit")!=null?rec.get("item_unit").toString():"1"));    		  		   
    	   }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return itemlist;
    	
    }
    public ArrayList getSXB_Time(){
    	ArrayList list= new ArrayList();
    	
    	return list;
    }
    public float count_Unit(String item_unit,float work_time,float rest_diff_time)
    {
    	float unit=0;    	
    	//判断一天的时间
    	if(item_unit!=null&&item_unit.length()>0)
		   {
		     unit=KQRestOper.getUnit(item_unit,work_time,rest_diff_time);//得到计算值  
		   }else
		   {
			  
			 unit=KQRestOper.getUnit("01",work_time,rest_diff_time);
		   }    	
    	return unit;
    }
    /**
     * 得到请假类型编号
     * 
     * */
    public ArrayList Leave_CodeItem()throws GeneralException
    {
    	RowSet rowSet=null;
    	String sqlcode=KQRestOper.kq_CodeItemSQL();
    	ArrayList list = new ArrayList();
    	list.add("27");
    	list.add("0");
    	list.add("0");
    	ArrayList leavelist=new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);    	
    	try
    	{
    		rowSet=dao.search(sqlcode.toString(),list);
    		while(rowSet.next()){
    			leavelist.add(rowSet.getString("codeitemid"));
    		}    		
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e); 
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
    	return leavelist;
    }
    /**
     * 查找一个员工的基本纪录
     * */
    public RecordVo selectEmpRecord(String nbase,String a0100,String b0110,String whereIN,String cur_date,String rest_date,String kq_type)throws GeneralException
    {
       StringBuffer strsql=new StringBuffer();       
 	   strsql.append("select ");	   
 	   strsql.append("A0100,B0110,"+Sql_switcher.isnull("E0122","''")+" as E0122,A0101,"+Sql_switcher.isnull("E01A1","''")+" as E01A1");
 	   if(kq_type!=null&&kq_type.length()>0)
	   {
		   strsql.append(","+kq_type+"");
	   }
 	   strsql.append(" from ");
 	   strsql.append(nbase);
 	   strsql.append("A01 "); 	   
 	   strsql.append(" where A0100='"+a0100+"' and b0110='"+b0110+"' "); 	   
 	   strsql.append(" and a0100 in(select a0100 "+whereIN+")"); 	   
 	   RowSet rowSet=null;
 	   ContentDAO dao = new ContentDAO(this.conn); 
 	   RecordVo vo = new RecordVo("Q03");
 	   try
 	   {
 		  rowSet=dao.search(strsql.toString()); 	 	   
 	 	   if(rowSet.next())
 	 	   {
 	 		  vo.setString("nbase", nbase);
 	 	      vo.setString("a0100", a0100); 	       
 	 	      vo.setString("q03z0", cur_date);
 	 	      vo.setString("b0110", rowSet.getString("B0110"));
 	 	      vo.setString("e0122", rowSet.getString("E0122"));
 	 	      vo.setString("a0101", rowSet.getString("A0101"));
 	 	      vo.setString("e01a1", rowSet.getString("E01A1"));
 	 	      vo.setString("q03z5","01");
 	 	      if(kq_type!=null&&kq_type.length()>0)
 	 	      {
 	 	    	 if(rowSet.getString(kq_type)!=null)
 	 	 	     {
 	 	 	        vo.setString("q03z3",rowSet.getString(kq_type));
 	 	 	     }else{
 	 	 	    	 vo.setString("q03z3","01"); 
 	 	 	     }
 	 	      }else
 	 	      {
 	 	    	 vo.setString("q03z3","01");
 	 	      }
 	 	      vo.setInt("i9999",RegisterInitInfoData.getI9999("Q03",nbase,a0100)); 		     
 	 	  }
 	   }catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }	     
 	   return vo;
    }
    /**********判断是否可以重新计算*********
     * 
 	 * @param userbase  数据库前缀
 	 * @param collectdate  操作时间
 	 * @param code 部门	
 	 * @param userbase  数据库前缀
 	 * @return 是否可以起草
     *
    * *****/
    public boolean if_Refer(String userbase,String code,String registerdate,String whereIN){
    	  boolean isCorrect=false;
    	  RowSet rowSet=null;
    	     StringBuffer sql=new StringBuffer();          
    	     sql.append("select Q03Z5 from Q05 where ");
    	     sql.append(" UPPER(nbase)='"+userbase.toUpperCase()+"'");
    	     sql.append(" and e0122 like '"+code+"%'"); 		
    	     sql.append(" and Q03Z0='"+registerdate+"'");
             sql.append("  and a0100 in(select a0100 "+whereIN+")");             
          ContentDAO dao = new ContentDAO(this.conn);
          try{
        	  rowSet= dao.search(sql.toString());
            if(rowSet.next())
            {
        	     String Q03Z5= (String)rowSet.getString("Q03Z5");
        	      if("01".equals(Q03Z5)|| "07".equals(Q03Z5))
        	      {
        		   isCorrect=true;
        	      }
             }else{
                isCorrect=true;//第一次汇总	
             }
        }catch(Exception e){
        	 e.printStackTrace();
        }finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
    	return isCorrect;
    }
    /**
	 * 建立时间临时表
	 * **/  
	public String  creat_KqTmp_Table()
	{
		String tablename=getTmpTableName(this.userView.getUserName(),RegisterInitInfoData.getKqPrivCode(userView));
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table(tablename);
		if(dbWizard.isExistTable(tablename,false))
		{
			dropOperationTable(tablename);
		}
		Field temp = new Field("nbase","人员库前缀");
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		Field temp1=new Field("sDate","考勤日期");
		temp1.setDatatype(DataType.STRING);
		temp1.setLength(10);
		temp1.setKeyable(false);			
		temp1.setVisible(false);
		table.addField(temp1);		
		Field temp2=new Field("a0100","人员编号");
		temp2.setDatatype(DataType.STRING);
		temp2.setLength(30);
		temp2.setKeyable(false);			
		temp2.setVisible(false);
		table.addField(temp2);
		Field temp3=new Field("b0110","部门编号");
		temp3.setDatatype(DataType.STRING);
		temp3.setLength(30);
		temp3.setKeyable(false);			
		temp3.setVisible(false);
		table.addField(temp3);
		Field temp4=new Field("e0122","部门编号");
		temp4.setDatatype(DataType.STRING);
		temp4.setLength(30);
		temp4.setKeyable(false);			
		temp4.setVisible(false);
		table.addField(temp4);
		Field temp5=new Field("itemType","业务类型");
		temp5.setDatatype(DataType.STRING);
		temp5.setLength(30);
		temp5.setKeyable(false);			
		temp5.setVisible(false);
		table.addField(temp5);
		Field temp6=new Field("TimeLen","业务类型值");
		temp6.setDatatype(DataType.STRING);
		temp6.setLength(30);
		temp6.setKeyable(false);			
		temp6.setVisible(false);
		table.addField(temp6);		
			try
			{
				dbWizard.createTable(table);
			}catch(Exception e)
			{
				e.printStackTrace();
			}	
			
		
		return tablename;
	}
	public void kqTempOperation(String a0100,String userbase,String b0110,String e0122,String op_date_to,String fielditemid,float unit,String lxtype,String kqTempTable)
	{
		fielditemid=fielditemid.toLowerCase();
		StringBuffer selectSQL = new StringBuffer();
		selectSQL.append("select itemType from "+kqTempTable);
		selectSQL.append(" where UPPER(nbase)='"+userbase.toUpperCase()+"' and a0100='"+a0100+"'");
		selectSQL.append(" and b0110='"+b0110+"' and sDate='"+op_date_to+"' and e0122='"+e0122+"' and itemType='"+fielditemid+"'");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try{
			rowSet=dao.search(selectSQL.toString());
			if(!rowSet.next())
			{
				StringBuffer INSERTSQL=new StringBuffer();
				INSERTSQL.append("INSERT INTO "+kqTempTable+" (nbase,a0100,b0110,e0122,sDate,itemType,TimeLen)");
				INSERTSQL.append(" values (?,?,?,?,?,?,?)");
				ArrayList  list  = new ArrayList();
				list.add(userbase);
				list.add(a0100);
				list.add(b0110);
				list.add(e0122);
				list.add(op_date_to);
				list.add(fielditemid);				
				
				 if("float".equals(lxtype)){
		    	       list.add(new Float(unit));
		    	 }else{
		    	       list.add(new Integer((int)unit));
		    	 }  
				 dao.insert(INSERTSQL.toString(),list);				
			}else
			{
				String unit_vaue="";
				if("float".equals(lxtype)){
					unit_vaue=new Float(unit).toString();
		    	 }else{
		    		unit_vaue= new Integer((int)unit).toString();
		    	 } 
				StringBuffer update = new StringBuffer();
				update.append("UPDATE "+kqTempTable+" SET TimeLen ="+Sql_switcher.isnull("TimeLen","''")+"+"+unit_vaue);
				update.append(" WHERE a0100='"+a0100+"' AND UPPER(nbase)='"+userbase.toUpperCase()+"'");
				update.append(" AND b0110='"+b0110+"' AND sDate='"+op_date_to+"'");
				update.append(" AND itemType='"+fielditemid+"'");				
				dao.update(update.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
		
	}
	public void pickUPTemp(String kqTempTable,ArrayList fielditemlist)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try
		{
			for(int i=0;fielditemlist.size()>i;i++)
			{
				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				String itemid=fielditem.getItemid();	
				
				String sql="select * from "+kqTempTable+" where itemType='"+itemid+"'";
				
			       rs=dao.search(sql);
			       if(!rs.next()) {
                       continue;
                   }
				
				StringBuffer update = new StringBuffer();
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  update.append("UPDATE Q03 SET q03."+itemid+" = "+kqTempTable+".TimeLen");
					  update.append(" FROM "+kqTempTable+" ");
					  update.append(" WHERE Q03.a0100="+kqTempTable+".a0100");
					  update.append(" AND Q03.nbase="+kqTempTable+".nbase");				  
					  update.append(" AND Q03.b0110="+kqTempTable+".b0110");
					  update.append(" AND Q03.q03z0="+kqTempTable+".sDate");
					  update.append(" AND "+kqTempTable+".itemType='"+itemid+"'");
					  update.append(" AND Q03.q03z3 in ('01','02')");
					  update.append(" AND Q03.q03z5 in ('01','07')");	
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  update.append(" UPDATE Q03 SET q03."+itemid+"=");
					  update.append("(SELECT TimeLen FROM "+kqTempTable+" A ");
					  update.append(" WHERE A.A0100=Q03.A0100 and A.nbase=Q03.nbase");
					  update.append(" AND A.B0110=Q03.B0110");
					  update.append(" AND A.itemType='"+itemid+"' and A.sDate=Q03.q03z0)");
					  update.append(" WHERE EXISTS(SELECT * FROM "+kqTempTable+" A");
					  update.append(" where A.B0110=Q03.B0110");
					  update.append(" AND A.itemType='"+itemid+"' and A.sDate=Q03.q03z0)");
					  update.append(" AND Q03.q03z3 in ('01','02')");
					  update.append(" AND Q03.q03z5 in ('01','07')");
					  break;
				  }
				  case Constant.DB2:
				  {
					  update.append(" UPDATE Q03 SET q03."+itemid+"=");
					  update.append("(SELECT TimeLen FROM "+kqTempTable+" A ");
					  update.append(" WHERE A.A0100=Q03.A0100 and A.nbase=Q03.nbase");
					  update.append(" AND A.B0110=Q03.B0110");
					  update.append(" AND A.itemType='"+itemid+"' and A.sDate=Q03.q03z0)");
					  update.append(" WHERE EXISTS(SELECT * FROM "+kqTempTable+" A");
					  update.append(" where A.B0110=Q03.B0110");
					  update.append(" AND A.itemType='"+itemid+"' and A.sDate=Q03.q03z0)");
					  update.append(" AND Q03.q03z3 in ('01','02')");
					  update.append(" AND Q03.q03z5 in ('01','07')");		 
					  break;
				  }
				} 
				dao.update(update.toString());
				
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
	     {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
		
	}
	/**
	 * 删除临时表
	 * */
	public void dropOperationTable(String tablename)
	{
		String deleteSQL="delete from "+tablename;		
		ArrayList deletelist= new ArrayList();			
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT * from "+tablename);
		ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		try
		{
			dao.delete(deleteSQL,deletelist);
			rowSet=dao.search(sql.toString());
			if(!rowSet.next())
			{
				DbWizard dbWizard =new DbWizard(this.conn);
				Table table=new Table(tablename);
				dbWizard.dropTable(table);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 		
	}
	/**
     * 新建临时表的名字
     * **/
    public static String getTmpTableName(String UserId,String PrivCode) 
    {
    	StringBuffer tablename=new StringBuffer();
		tablename.append("kqoperation");
		tablename.append("_");
		tablename.append(PrivCode);
		tablename.append("_");
		tablename.append(UserId);
		return tablename.toString();
    }
    public void saveRecold(String kqTempTable,String a0100,String b0110,String e0122,String userbase,String fielditemid,String op_date_to,float unit,String lxtype,String start_date,String end_date,String whereIN,String rest_date,String kq_type)throws GeneralException
    {
    	 if(!if_EmpRecord(userbase,a0100,b0110,whereIN,op_date_to))
  	     {
  		    restInterRecord(a0100,b0110,userbase,op_date_to,unit,lxtype,start_date,end_date,whereIN,rest_date,kq_type);   
  	     }
	     kqTempOperation(a0100,userbase,b0110,e0122,op_date_to,fielditemid,unit,lxtype,kqTempTable);
    }
}
