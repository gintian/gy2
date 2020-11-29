package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.PickUpOperationData;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

public class GetKqRestTime {
	
	private  Connection conn;
	private UserView userView;
	private  String A0100;
	private RowSet frowset;
	public GetKqRestTime(Connection conn,UserView userView,String A0100,RowSet frowset)
	{
		this.conn=conn;
		this.userView=userView;		
		this.A0100=A0100;
		this.frowset=frowset;
	}
	

	public  float getTime_My(ArrayList timelist,String startd,String endd,String rest_date,String rest_b0110,String a0101) throws GeneralException
	{
		   
		    ContentDAO cdao =new ContentDAO(this.conn);
            String column_z1="q17z1";
            String column_z3="q17z3";		    
		    String has_rest=null;
		    String has_feast=null;
		    float re=0;
		    float dd=0;
		    try{ 
		    	StringBuffer sb = new StringBuffer();
		        sb.append("select q1707 from Q17 where a0100='"+this.A0100+"'");       	
		        sb.append(" and "+column_z1+"<="+Sql_switcher.dateValue(startd)+"");	
		        sb.append(" and "+column_z3+">="+Sql_switcher.dateValue(endd));		
		        frowset = cdao.search(sb.toString());
		        if(this.frowset.next())
		        {
		            	 dd= frowset.getFloat("q1707");
		        }else
		        {
		        	throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+",没有年假","",""));
		        }
		        if(dd<=0)
		           return 0;
	            sb.delete(0,sb.length());
		        String kq_item_sql="select has_rest,has_feast from kq_item where item_id='06'";
		        this.frowset=cdao.search(kq_item_sql);
		        if(this.frowset.next())
		        {
		        	   		has_rest=this.frowset.getString("has_rest");
		        	   		has_feast=this.frowset.getString("has_feast");
		        }	
		        /*********对日起进行操作**********/
		        String[] sw_times=KQRestOper.getTimeSlice (timelist.get(0).toString()); 
    	    	String sb_time=sw_times[0];
    	    	String[] ed_times=KQRestOper.getTimeSlice (timelist.get(timelist.size()-1).toString());
    	    	String ed_time=ed_times[1];
    	    	
		        Date  d_start=DateUtils.getDate(startd,"yyyy-MM-dd HH:mm");   	    	
    	    	String op_date_start=DateUtils.format(d_start,"yyyy.MM.dd");
    	    	String op_date=DateUtils.format(d_start,"yyyy-MM-dd");	
    	    	Date  d_end=DateUtils.getDate(endd,"yyyy-MM-dd HH:mm");     	    	
    	    	String op_date_end=DateUtils.format(d_end,"yyyy.MM.dd");
    	    	String end_op_date=DateUtils.format(d_end,"yyyy-MM-dd");    	    	
    	    	String kq_sta_hhmm=DateUtils.format(d_start,"HH:mm");
    	    	String kq_end_hhmm=DateUtils.format(d_end,"HH:mm");
    	    	Date op_start_date=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
    	    	/********判断请假开始时间是否小于上班时间*******/    	    	
    	    	float f_sb=KQRestOper.toHourFormMinute(d_start,op_start_date);
    	    	if(kq_sta_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		d_start=op_start_date;
    	    	}else if(f_sb>0)
    	    	{
    	    		d_start=op_start_date;
    	    	}
    	    	if(kq_end_hhmm.indexOf("00:00")!=-1)
    	    	{
    	    		d_end=DateUtils.getDate(end_op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    	}
    	    	int diff=RegisterDate.diffDate(d_start,d_end); 
    	    	float work_time=KQRestOper.getWork_Time(timelist);
    	    	dd=dd*work_time;
    	    	float unit=0;
    	    	float diffhour=KQRestOper.toHourFormMinute(d_start,d_end);
    	    	
    	    	for(int i=0;i<=diff;i++)
    	    	{
    	    		if(i>0)
     	    	    {
    	    		   d_start=DateUtils.addDays(d_start,1);	
  	    	           op_date=DateUtils.format(d_start,"yyyy-MM-dd");
  	    	           op_date_start=DateUtils.format(d_start,"yyyy.MM.dd"); 	    	      
     	    	    }
    	    		String feast_name=IfRestDate.if_Feast(op_date_start,this.conn);
					if(feast_name!=null&&feast_name.length()>0)
					{
						String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
						if(turn_date==null||turn_date.length()<=0)			
						{
						   if(!"1".equals(has_feast))
						   {
								continue;
     	    	    	   } 
						}	    		         
	    			}else if(IfRestDate.if_Rest(op_date_start,userView,rest_date))
	    	    	{
	    				 String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
	    				 if(turn_date==null||turn_date.length()<=0)			
 						 {
	    					 if(!"1".equals(has_rest))
    	    	    		  {
	    						 continue;
    	    	    		  }
 						}	    	    		 
	    	    	}else
	    	    	{
	    	    		  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date,this.conn);
	 	    	    	  if(week_date!=null&&week_date.length()>0)
			    		  { 
	 	    	    		 if(!"1".equals(has_rest))
   	    	    		     {
	    						 continue;
   	    	    		     }
			    		  }
	    	    	}	    	      
    	    		if(diff>0)
    	    		{
    	    			if(i==0)
        	    		{
        	    			  Date dd_end=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    				  float rest_diff_time=KQRestOper.getTime_StartDiffQ15(op_date,d_start,dd_end,timelist);  
    	    				  unit=unit+rest_diff_time;
        	    		}else if(i==diff)
        	    		{
        	    			d_start=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
       	    			    Date op_end_date=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
       	    			    float f_xb=KQRestOper.toHourFormMinute(op_end_date,d_end);
       	    			    if(f_xb>0)
       	    			    {
       	    			    	d_end=op_end_date;
       	    			    }   	    			    
       	    			    float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,d_start,d_end,timelist);   
       	    			    unit=unit+rest_diff_time;
       	    			}else
        	    		{
       	    				unit=unit+work_time;
        	    		}
    	    		}else
    	    		{
    	    			 if(diffhour<work_time)
      	    		     {
      	    			   unit=KQRestOper.getTime_StartDiffQ15( op_date,d_start,d_end,timelist);  
      	    			    
      	    		     }else
      	    		     {
      	    			   unit=work_time; 
      	    		     }
    	    		}
    	    		
    	    	}
    	    	if(unit<0)
    	    	{
    	    		unit=unit*-1;
    	    	}
    	    	if(unit>dd)
    		    {
    	    		re=0;
    		    }else
    		    {
    		    	re=unit;
    		    }
		    }catch(Exception se){
		    	  se.printStackTrace();
		 	      throw GeneralExceptionHandler.Handle(se);
		    }
		    
		    return re;
	}
	public  float getTime(ArrayList timelist,String nbase,String b0110,String t_id) throws GeneralException
	{
		     
		    StringBuffer sb = new StringBuffer();
		    ContentDAO cdao =new ContentDAO(this.conn);
            String[] weeks=null;
		    String has_rest=null;
		    String startd="";
		    String endd="";
		    String item_unit="01";
		    String has_feast=null;
		    float dd=0;
		    String[] sw_times=KQRestOper.getTimeSlice (timelist.get(0).toString()); 
	    	String sb_time=sw_times[0];
	    	String[] ed_times=KQRestOper.getTimeSlice (timelist.get(timelist.size()-1).toString());
	    	String ed_time=ed_times[1];
		    try{ 
		        	 sb.append("select * from Q17 where a0100='");
		    	     sb.append(A0100);
		    	     sb.append("'");
		    	     sb.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
		               frowset = cdao.search(sb.toString());
		               if(this.frowset.next())
		               {
		            	 dd= frowset.getFloat("q1707");
		               }
		               if(dd<=0)
		            	   return 0;
		     
		                sb.delete(0,sb.length());
		                sb.append("select * from Q15 where a0100='");
		    	        sb.append(A0100);
		    	        sb.append("'");
		    	        sb.append(" and nbase='"+nbase+"'");
		    	        sb.append(" and q1501='"+t_id+"'");
		                this.frowset = cdao.search(sb.toString());
		                if(this.frowset.next())
		                {
		            	   Date start_d=this.frowset.getDate("q15z1");
		         		   Date start_t=this.frowset.getTime("q15z1");
		         		   String start_str_d=DateUtils.format(start_d,"yyyy-MM-dd");
		         		   String start_str_t=DateUtils.format(start_t,"HH:mm");
		         		   startd=start_str_d+" "+start_str_t;		         		  
		      			   Date end_d=this.frowset.getDate("q15z3");
		      			   Date end_t=this.frowset.getTime("q15z3");
		      			   String end_str_d=DateUtils.format(end_d,"yyyy-MM-dd");
		      			   String end_str_t=DateUtils.format(end_t,"HH:mm");
		      			   endd=end_str_d+" "+end_str_t; 
		      			      
		                }
                        String kq_item_sql="select has_rest,has_feast,item_unit from kq_item where item_id='06'";
		        	    this.frowset=cdao.search(kq_item_sql);
		        	   	if(this.frowset.next())
		        	   	{
		        	   		has_rest=this.frowset.getString("has_rest");
		        	   		has_feast=this.frowset.getString("has_feast");
		        	   		
		        	   	}
		        		
		        }catch(Exception se){
		    	  se.printStackTrace();
		 	      throw GeneralExceptionHandler.Handle(se);
		        }
		        if(startd==null|| "".equals(startd)||endd==null|| "".equals(endd))
		        	return 0 ;
		        ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.conn);
		        String rest_date=restList.get(0).toString();
		        String rest_b0110=restList.get(1).toString();
		        PickUpOperationData pickUpOperationData=new PickUpOperationData();		        
		        Date kq_start=DateUtils.getDate(startd,"yyyy-MM-dd HH:mm");
		        Date kq_end=DateUtils.getDate(endd,"yyyy-MM-dd HH:mm");
		        String op_date=DateUtils.format(kq_start,"yyyy-MM-dd"); 
		        String op_date_to=DateUtils.format(kq_start,"yyyy.MM.dd");   
		        float work_time=KQRestOper.getWork_Time(timelist);
		        float diffhour=KQRestOper.toHourFormMinute(kq_start,kq_end);
		        int diff=RegisterDate.diffDate(kq_start,kq_end);  
		        float resu=0;
		        for(int i=0;i<=diff;i++)
       	    	{  
    	    	  float unit=0; 
    	    	  if(i>0)
       	    	  {
    	    		  kq_start=DateUtils.addDays(kq_start,1);    	    		  
    	    	      op_date=DateUtils.format(kq_start,"yyyy-MM-dd");
    	    	      op_date_to=DateUtils.format(kq_start,"yyyy.MM.dd");   	    	      
       	    	  }
    	    	  if(diff>0)
    	    	  {    	    		  
    	    		 if(diff==i)
    	    		 {
    	    			 kq_start=DateUtils.getDate(op_date+" "+sb_time,"yyyy-MM-dd HH:mm");
    	    			 Date op_end_date=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    			 float f_xb=KQRestOper.toHourFormMinute(op_end_date,kq_end);
    	    			 if(f_xb>0)
    	    			 {
    	    				 kq_end=op_end_date;
    	    			 }
    	    			 float rest_diff_time=KQRestOper.getTime_StartDiffQ15(op_date,kq_start,kq_end,timelist);
    	    		     float l_unit= diffhour-i*24;
    	    			 if(l_unit>work_time)
    	    			 {
    	    			     unit=KQRestOper.con_Unit(item_unit,work_time,work_time);    	    			     
    	    			 }else
    	    			 {
    	    				 unit=pickUpOperationData.count_Unit(item_unit,work_time,rest_diff_time);     	    				
    	    			 }
    	    		  }else{
    	    			  if(i==0)
    	    			  {
    	    				     	    			 
    	    				  Date d_end=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
    	    				  float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,kq_start,d_end,timelist);  
    	    				  unit=pickUpOperationData.count_Unit(item_unit,work_time,rest_diff_time); 
    	    			  }else{
    	    				 unit=KQRestOper.con_Unit(item_unit,work_time,work_time);   
    	    			  }
    	    		  }    	    		  
    	    	  }else
    	    	  {
//    	    		  Date op_end_date=DateUtils.getDate(op_date+" "+ed_time,"yyyy-MM-dd HH:mm");
// 	    			  float f_xb=KQRestOper.toHourFormMinute(op_end_date,kq_d_end);
// 	    			  if(f_xb>0)
// 	    			  {
// 	    				 kq_d_end=op_end_date;
// 	    			  }
    	    		  if(diffhour<work_time)
    	    		  {
    	    			  
    	    			  float rest_diff_time=KQRestOper.getTime_StartDiffQ15( op_date,kq_start,kq_end,timelist);  
    	    			  unit=pickUpOperationData.count_Unit(item_unit,work_time,rest_diff_time);  
    	    		  }else
    	    		  {
    	    			  unit=KQRestOper.con_Unit(item_unit,work_time,work_time); 
    	    		  }
    	    	   }
    	    	   String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
       			   if(feast_name!=null&&feast_name.length()>0)
       			   {
       				 String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
       				 if(turn_date==null||turn_date.length()<=0)			
       				 {
       							if("1".equals(has_feast))
            	    	        {
       								resu=resu+unit;
           	    		    	} 
       				 }else
       				{
       							resu=resu+unit;       							 
       				}
       	    		         
       	    	  }else if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
       	    	  {
       	    		String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date,this.conn);
       	    		if(turn_date==null||turn_date.length()<=0)			
        			{
       	    		    if("1".equals(has_rest))
           	    	    {
           	    	    	resu=resu+unit;
           	    	    }
        			}else
        			{
        				   resu=resu+unit;
        		    }       	    	    		 
       	    	  }else
       	          {
       	    		  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date,this.conn);
	    	          if(week_date==null||week_date.length()<=0)
	    	          {
	    	        	  
	    	        	  resu=resu+unit;
	    	          }else
	    	          {
	    	        	  if("1".equals(has_rest))
	    	        	  {
	    	        		  resu=resu+unit; 
	    	        	  }
	    	          }
       	    	  }
       	    } 	   //for结束
    	    	
		        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	      		String aaa = sdf.format(kq_start);
	      		SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
	      		String ccc = sdff.format(kq_end);
	      		   
	      		SimpleDateFormat sdfs = new SimpleDateFormat("yyyy.MM.dd");
	      		String bbb = sdfs.format(kq_start);
	      		
	      		
	      		
	      		
	      		int num=RegisterDate.diffDate(kq_start,kq_end);
	      		num+=1;
	      		float resu=0;
	      		String feast="";
	      		for(int da=0;da<num;da++)
	      		{
	      			if(num==1)
	      			  resu=KQRestOper.getTime_StartDiffQ15(aaa,kq_start,kq_end,timelist);
	      		
	      			if(da==0&&num!=1){
  	      	        	
	      				Date kq_ends=DateUtils.getDate(aaa+" 23:59","yyyy-MM-dd HH:mm");
  	    	        	resu=KQRestOper.getTime_StartDiffQ15( aaa,kq_start,kq_ends,timelist);
  	    	        	kq_start=DateUtils.addDays(kq_start,1);
	      			 } 
	      			if(da==1&&num-1==1)
		      		{
		      		    	kq_start=DateUtils.addDays(kq_start,1);
		      		    	SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
		    	      		aaa = sdfd.format(kq_start);
		    	      		Date kq_starts=DateUtils.getDate(aaa+" 00:00","yyyy-MM-dd HH:mm");
		      		    	Date kq_ends=DateUtils.getDate(aaa+" 23:59","yyyy-MM-dd HH:mm");
	  	    	        	resu=resu+KQRestOper.getTime_StartDiffQ15( aaa,kq_starts,kq_ends,timelist);
	  	    	  
		      		 }else if(num-1>da&&da>0)
	      			 { 	  
	      				
	      				kq_start=DateUtils.addDays(kq_start,1);
      	    	        bbb=DateUtils.format(kq_start,"yyyy.MM.dd");   
      	    	 	    weeks=weeksstr.split(",");
      	    	 	    boolean wes=false;
	  	      	         for(int n=0;n<weeks.length;n++)
	  	      	         {
	  	      	            String wek=weeks[n];
	  	      	        	wes=KQRestOper.if_RestWeek(wek,bbb);
	  	      	         }
	  	      	          if(wes==true)
	      	    	      {
	      	    	    	if(has_rest.indexOf("1")!=-1)
	      	    		       resu=resu+work_time;
	      	    	    	else
	      	    		       continue;

	      	    	      }
	  	      	          feast=IfRestDate.if_Feast(bbb,this.conn);
	  	      	          if(feast==null||feast.equals(""))
	  	      	          {
	  	      	        	 resu=resu+work_time;
	  	      	          }else{
	  	  	      	        if(has_feast.indexOf("1")!=-1)
	  	      	        	   resu=resu+work_time;
	  	      	           }
      	    	      
	      			}else if(num-1==da&&num-1>1)
	      			{  
	      				kq_start=DateUtils.getDate(ccc+" 00:00","yyyy-MM-dd HH:mm");
	      				
	      				String endr=DateUtils.format(kq_end,"HH:mm");
	      				
	      				if(endr.indexOf("00:00")!=-1)
	      					kq_end=DateUtils.getDate(ccc+" 23:59","yyyy-MM-dd HH:mm");
	      				
  	    	        	resu=resu+KQRestOper.getTime_StartDiffQ15(ccc,kq_start,kq_end,timelist);
	      			}
	      		}*/
	      		float re=0;
	      		re=resu/work_time;
	      		re=KQRestOper.round(String.valueOf(re),2);
	      		    
	      		    
		     return re;
	}

}
