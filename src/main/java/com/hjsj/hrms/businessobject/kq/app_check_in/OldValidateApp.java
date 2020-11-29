package com.hjsj.hrms.businessobject.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class OldValidateApp {
	private Connection conn;
	private UserView userView;
	public OldValidateApp()
	{
		
	}
	public OldValidateApp(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	 /**
     * 判断一时间段是否有公休日
	 * @param usr,tab, star,endtime 
	 *        
	 * @return boolean
	 *        返回真 和假
     * @throws SQLException 
     * @throws GeneralException 
	 * */
    private boolean is_Rest(Date da1,Date da2,String code) throws  GeneralException
    {
    	boolean ret=false;
    	
    	 if(code==null||code.length()<=0)
    	 {
    		 ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.conn);
  		     code=managePrivCode.getPrivOrgId();  
    	 }
    		 
    	 String b0110="UN"+code;
    	 ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.conn);
    	 String rest_date=restList.get(0).toString();
 		 String rest_b0110=restList.get(1).toString(); 
  	     int num=RegisterDate.diffDate(da1,da2);
  	     
    	 for(int m=0;m<=num;m++)
     	 {
              String op_date_to=getDateByAfter(da1,m);
              if(IfRestDate.if_Rest(op_date_to,userView,rest_date))
              {
            	  String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date_to,this.conn);
    			  if(turn_date==null||turn_date.length()<=0)			
    			  {
    				  ret= true;
    				  continue;
    			  }else
    			  {
    				  return false;
    				  
    			  }
              }else
              {
            	  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date_to,this.conn);       	    	    	  
    	          if(week_date!=null&&week_date.length()>0)
    	          {
    	        	  ret= true;
    				  continue;
    	          }else
    	          {
    	        	  return false;
    	          }
              } 	
  	     } 
    	return ret;
    }
    /**
     * 判断一时间段是否有节假日
	 * @param usr,tab, star,endtime 
	 *        
	 * @return boolean
	 *        返回真 和假
     * @throws GeneralException 
	 * */
    private boolean is_Feast(Date da1,Date da2,String code) throws GeneralException
    {
    	boolean ret=false;    	
    	try{
    		if(code==null||code.length()<=0)
    		{
    			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.conn);
    		    code=managePrivCode.getPrivOrgId();
    		}       		  
       	    String b0110="UN"+code;
       	    ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.conn);
       	    String rest_date=restList.get(0).toString();
    		String rest_b0110=restList.get(1).toString(); 
 	        int num=RegisterDate.diffDate(da1,da2);
   	        for(int m=0;m<=num;m++)
    	    {
   	        	String op_date_to=getDateByAfter(da1,m);
   	        	String feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
				if(feast_name!=null&&feast_name.length()>0)
 	    	   {
					 String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date_to,this.conn);
					 if((turn_date==null||turn_date.length()<=0))			
					 {
						 ret= true;
	    				 continue;
					 }else
					 {
						 return false;
					 }
 	    	   }else
 	    	   {
 	    		  String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date_to,this.conn);       	    	    	  
    	          if(week_date!=null&&week_date.length()>0)
    	          {
    	        	  ret= true;
    				  continue;
    	          }else
    	          {
    	        	  return false;
    	          }
 	    	   }            
 	       } 
     	 }catch(Exception se){
	       se.printStackTrace();
	 	  throw GeneralExceptionHandler.Handle(se);
	      }	
    	return ret;
    }
    /**
	   * 取
	   * @param dateString，
	   *         某年某月某天
	   * @param  afterNum
	   *         天数  
	   * @return string
	   *          返回相加后得到新的某年某月某天
	   * */
	public static String getDateByAfter(Date date, int afterNum) throws GeneralException {

		Calendar calendar = Calendar.getInstance();

		try {
			 calendar.setTime(date);
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);

		return new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
	}
}
