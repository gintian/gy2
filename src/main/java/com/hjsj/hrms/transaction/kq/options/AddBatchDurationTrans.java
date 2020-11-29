package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddBatchDurationTrans extends IBusiness {
	/**
	   * 
	   * 增加 考勤按自然月方法
	   * 
	   * */
	private void setCode(RecordVo rvo,String yer) throws GeneralException
	{
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
	       try
	       {  
	    	    for(int i=1;i<=12;i++)
	    	     {
	    	      
	    	    	rvo.setString("kq_year",yer);//rv.getString("kq_year")
	    	    	rvo.setString("kq_duration",redate(i));
	    	    	rvo.setString("gz_duration",redate(i));
	    	    	switch(i)
	    	    	{
	    	    	  case 1:
	    	    	  case 2:  
	    	    	  case 3:
	    	    	  case 4:
	    	    	  case 5:
	    	    	  case 6:
	    	    	  case 7:
	    	    	  case 8:
	    	    	  case 9:
	    	    	  case 10:
	    	    	  case 11:
	    	    	  case 12:
	    	    		  rvo.setDate("kq_start",(yer+"-"+redate(i)+"-01").toString());
	    	    		  rvo.setDate("kq_end",getDateByAfteri(yer+"-"+redate(i)));
	    	    	 break;
	    	    	 default:
	    	    	     rvo.setDate("kq_start",(rvo.getString("kq_year")+"-"+i+"-01").toString());
	 	    	         rvo.setDate("kq_end",(rvo.getString("kq_year")+"-"+i+"-31").toString());
	    	    		 
	    	    	}
	    	    	rvo.setString("gz_year",rvo.getString("kq_year"));
	                rvo.setString("finished","0");  
		            dao.addValueObject(rvo); 

	    	      }	
	            
	       }catch(Exception exx)
	       {
	  	       exx.printStackTrace();
	  	       throw GeneralExceptionHandler.Handle(exx);
	  	    }
	}
	private String redate(int str)
		{
			String ret="";
			if(String.valueOf(str).length()==1)
				ret="0"+str;
			else
				ret=String.valueOf(str);
			
			return ret;
		}
     public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
	  
	   RecordVo rv = (RecordVo)this.getFormHM().get("duration");
	   Integer count = Integer.valueOf((String)this.getFormHM().get("count"));//考勤期间数目
	   String kyear =(String)this.getFormHM().get("kyear");//考勤年度
	   String ro =(String)this.getFormHM().get("radio");//高级选项是哪一个
	   String box =(String)this.getFormHM().get("box");//起始月份从上年起
	   String md=(String)this.getFormHM().get("month");//起始月份为哪一月
	   String das=(String)this.getFormHM().get("dat");//起始日期为哪一天
	   String one_len=(String)this.getFormHM().get("one_len");//每期间月数
	   boolean ret=true;
	   if(box==null|| "".equals(box))
	   {
		   ret=false;
	   }else{
		   ret =true;
	   }
       if(rv==null)
           return; 
       

     
       StringBuffer stsql=new StringBuffer();
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
	   String kqst="";
		try
		{
            stsql.append("select kq_year from kq_duration ");
            this.frowset = dao.search(stsql.toString());
            if(this.frowset.first())
            	 kqst=this.frowset.getString("kq_year");
		}
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	  if("1".equals(ro)&&(kqst==null|| "".equals(kqst)))
		  ro="2";
     
     if(ro==null|| "".equals(ro))
     {
    	 ro="2";
     }
       if("1".equals(ro))
    	   this.sameNext(kyear);
       else if("3".equals(ro))
    	   getDursByStartDate(rv,kyear,md,das,one_len,ret);    	   
       else if("4".equals(ro)&&count!=null)
    	    this.setDifine(rv,kyear,count.intValue());
       else if("2".equals(ro))
       	    this.setCode(rv,kyear);
       
    		 this.getFormHM().put("flag","3"); //做为刷新页面
    		 this.getFormHM().put("box","");
    		 this.getFormHM().put("radio","");
    		 this.getFormHM().put("kq_year",kyear);
	}
	/**
	   * 
	   * 增加 考勤同上一年度方法
	   * 
	   * */
	private void sameNext(String year) throws GeneralException
	{
		int nian=Integer.parseInt(year)-1;
		StringBuffer stsql=new StringBuffer();
		RecordVo vo=new RecordVo("kq_duration");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			stsql.append("select * from kq_duration where kq_year='");
			stsql.append(String.valueOf(nian));
			stsql.append("'");
			this.frowset = dao.search(stsql.toString());
	        while(this.frowset.next())
	        {
	        	vo.setString("kq_year",year);
	        	vo.setString("gz_year",year);
	        	vo.setString("kq_duration",this.frowset.getString("kq_duration"));
	        	vo.setString("gz_duration",this.frowset.getString("gz_duration"));
	        	String kqst=DateUtils.format(this.frowset.getDate("kq_start"),"yyyy-MM-dd");
	        	int kqs=Integer.parseInt(kqst.substring(0,4));
	        	vo.setDate("kq_start",kqst.replaceAll(String.valueOf(kqs),String.valueOf(kqs+1)));
	        	String kqed=DateUtils.format(this.frowset.getDate("kq_end"),"yyyy-MM-dd");
	        	int kqe=Integer.parseInt(kqed.substring(0,4));
	        	
	        	int too=Integer.parseInt(kqed.substring(5,7));
	        	int to1=Integer.parseInt(kqed.substring(8,10));
	        	if((too==2&&to1==28)||(too==2&&to1==29))
	        		vo.setDate("kq_end",getDateByAfteri((kqed.replaceAll(String.valueOf(kqe),String.valueOf(kqe+1))).substring(0,7)));
	        	else
	        	   vo.setDate("kq_end",kqed.replaceAll(String.valueOf(kqe),String.valueOf(kqe+1)));
	        	vo.setString("finished","0");  
	   		    dao.addValueObject(vo);
	       }
		}
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
		
	}
	/**
	   * 
	   * 增加 考勤自定义方法
	   * 
	   * */
	
	private void setDifine(RecordVo rvo,String yer,int cou) throws GeneralException
	{
		Date kq_start=DateUtils.getDate(yer+"-01-01","yyyy-MM-dd");
        Date kq_end=DateUtils.getDate(String.valueOf(Integer.parseInt(yer)+1)+"-01-01","yyyy-MM-dd");
		int num=RegisterDate.diffDate(kq_start,kq_end);
		
		int aa=num/cou;
		int bb=num-aa*cou;
		 
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
	    try
	     {  
	    	for(int i=1;i<=cou;i++)
   	     {
   	    	rvo.setString("kq_year",yer);
   	    	rvo.setString("kq_duration",redate(i));
   	    	if(i==1)
   	    	{
   	    		 rvo.setDate("kq_start",String.valueOf(yer+"-01-01"));
   	    		 if(bb>=1)
   	    		 {
   	    			 rvo.setDate("kq_end",getDateByAfter(String.valueOf(yer+"-01-01"), aa));
   	    			 rvo.setString("gz_duration",getDateByAfter(String.valueOf(yer+"-01-01"), aa).substring(5,7));
   	    		    
   	    		 }
   	    		 else
   	    		 {
   	    		     rvo.setDate("kq_end",getDateByAfter(String.valueOf(yer+"-01-01"), aa-1));
   	    		     rvo.setString("gz_duration",getDateByAfter(String.valueOf(yer+"-01-01"), aa-1).substring(5,7));
   	    		 }
   	    	}else{
   	    		
   	    		if(i<=bb){
   	    			 rvo.setDate("kq_start",getDateByAfter(String.valueOf(yer+"-01-01"), aa*(i-1)+i-1));
	    	    	     rvo.setDate("kq_end",getDateByAfter(String.valueOf(yer+"-01-01"), aa*i+i-1));
	    	    	     rvo.setString("gz_duration",getDateByAfter(String.valueOf(yer+"-01-01"), aa*i+i-1).substring(5,7));
   	    			
   	    		}
   	    		 else{
   	    			rvo.setDate("kq_start",getDateByAfter(String.valueOf(yer+"-01-01"), aa*(i-1)+bb));
		    	        rvo.setDate("kq_end",getDateByAfter(String.valueOf(yer+"-01-01"), aa*i+bb-1));
		    	        rvo.setString("gz_duration",getDateByAfter(String.valueOf(yer+"-01-01"), aa*i+bb-1).substring(5,7));
   	    			
   	    		 }
   	    	}
   	    	
   	    	rvo.setString("gz_year",rvo.getString("kq_year"));
               rvo.setString("finished","0");  
	            dao.addValueObject(rvo); 

   	      }
	    	    
	            
	       }catch(Exception exx)
	       {
	  	       exx.printStackTrace();
	  	       throw GeneralExceptionHandler.Handle(exx);
	  	   }
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
	public static String getDateByAfter(String dateString, int afterNum) throws GeneralException {

		Calendar calendar = Calendar.getInstance();

		try {
			 Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			 calendar.setTime(date);
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}
	 /**
	    * 取某年某月的最后一天
	    * @param str 
	    *         某年某月
	    * @return string
	    *          返回某年某月的最后一天
	    * */
	public  String getDateByAfteri(String str) throws GeneralException
	{
		
		Calendar now = Calendar.getInstance();
		int maxDay =0;
		
		try {
		    	Date date = new SimpleDateFormat("yyyy-MM").parse(str);
		    	now.setTime(date);
		        maxDay = now.getActualMaximum(Calendar.DATE);
		        now.add(GregorianCalendar.DAY_OF_MONTH,maxDay-1);
		        
		 }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		 return new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
	}	
	/**
	 * 指定期间
	 * @param vo
	 * @param kyear 考勤年度
	 * @param sMonth 起始月份为哪一月
	 * @param sDasy 起始日期为哪一天
	 * @param one_len 每期间月数
	 * @param ret 起始月份从上年起
	 * @throws GeneralException
	 */
	private void getDursByStartDate(RecordVo vo,String kyear,String sMonth,String sDasy,String one_len,boolean ret)throws GeneralException
	{
		if(sMonth==null||sMonth.length()<=0)
			sMonth="1";
		if(sDasy==null||sDasy.length()<=0)
			sDasy="1";
		if(one_len==null||one_len.length()<=0)
			one_len="1";
		int s_month=Integer.parseInt(sMonth);
		int lOffSet=s_month-1;
		int day=Integer.parseInt(sDasy);
		int s_kyear=Integer.parseInt(kyear);
		int yOffSet=0;
		int len=Integer.parseInt(one_len);
		if(s_month>12)
			s_month=12;
		if(day>31)
			day=31;
		if(ret)
			yOffSet=-1;
		int year=0;
		year=s_kyear+yOffSet;
		int month=0;
		Date sDate=null;
		Date eDate=null;
		try
		{
			for(int i=1;i<=12/len;i++)
			{
				vo.setString("kq_year",kyear);
		    	vo.setString("kq_duration",redate(i));
		    	vo.setString("gz_year",kyear);
				if(len==1)
					month= i + lOffSet;
				else
					month=1+ lOffSet + (i-1)*len;
				if(month>12)
				{
					month = month-12;
					year=s_kyear+yOffSet+1;
				}
				sDate=DateUtils.getDate(year,month,day);	
				if(day==1)
				  month = month + len-1;
				else
				  month = month + len;
				if(month<12)
				{
					if(day==1)
						eDate=DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd");
					else
						eDate=DateUtils.getDate(year,month,day-1);
				}else
				{
					if(day==1)
					{
						if(month==12)
							eDate=DateUtils.getDate(getDateByAfteri(year+"-"+month),"yyyy-MM-dd");
						else
							eDate=DateUtils.getDate(getDateByAfteri((year+1)+"-"+(month-12)),"yyyy-MM-dd");
					}else
					{
						if(month==12)
							eDate=DateUtils.getDate(year,month,day-1);
						else
							eDate=DateUtils.getDate((year+1),(month-12),day-1);
					}
				}	
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if(month>12)
					vo.setString("gz_duration",redate(month-12));
				else	
					vo.setString("gz_duration",redate(month));
				
				vo.setDate("kq_start",sDate);
				vo.setDate("kq_end",eDate);
				vo.setString("finished","0");  
				dao.addValueObject(vo);				
			}
		}catch(Exception exx)
		{
			exx.printStackTrace();
	  	    throw GeneralExceptionHandler.Handle(exx);
		}
		
	}
}
