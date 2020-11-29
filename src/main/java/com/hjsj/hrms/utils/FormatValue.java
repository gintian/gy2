package com.hjsj.hrms.utils;

import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.FieldItem;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title:FormatValue</p>
 * <p>Description:主要为了指标系统表数据格式显示</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2005:11:30:52 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class FormatValue {

    /**
     * 
     */
    public FormatValue() {
    }
    
    /**格式化字符串*/
    public String format(FieldItem item,String value)
    {
       if(item==null)
    	   return value;
       String itemtype=item.getItemtype();
       int ndecimal=item.getDecimalwidth();
       int len=item.getItemlength();
       if("A".equals(itemtype))
       {
    	   if(value==null)
    		   return "";
           return value;
       }
       else if("N".equals(itemtype))
       {
            if(value==null|| "".equals(value))
                return "";
  	    	String pattern="";
   	    	/*float*/double fldValue=0.0f;
   	    	if (ndecimal > 0) 
   	    	{
   	    	    pattern = "0.";
   	    	    for (int i = 0;i<ndecimal;i++)
   	    	        pattern += "0";
   	    	} 
   	    	else 
   	    	{
   	    	    pattern = "###"; 
   	    	}
   			if (value != null && value.length() > 0) 
   			{
   				fldValue = /*Float.parseFloat(value)*/Double.parseDouble(value);
   				value=new DecimalFormat(pattern).format(fldValue).trim();
   			}
   			return value;
       }
       else if("D".equals(itemtype))
       {
           SimpleDateFormat myFmt1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
           try
           {
               String datestr="";
               if(value==null|| "null".equalsIgnoreCase(value))
            	   return "";
        	   //Date datevalue=df.parse(value);
               value=value.replaceAll("\\.", "-");
               Date datevalue=myFmt1.parse(value);
               switch(len)
               {
               case 4://YYYY
            	   datestr=DateStyle.dateformat(datevalue,"yyyy");            	   
            	   break;
               case 7://YYYY.MM
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM");            	   
            	   break;
               case 10://YYYY.MM.DD
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd");             	   
            	   break;
               case 16://YYYY.MM.DD
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd HH:mm");             	   
            	   break;  
               case 18://YYYY.MM.DD HH:MM:SS
            	   //SimpleDateFormat myFmt1=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd HH:mm:ss");             	   
            	   break;
               default:
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd");            	   
            	   break;
               }
//               if(len>=10)
//               {
//            	   Date datevalue=df.parse(value);
//            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd");  
//               }
               /**0:00:00,日期型指标,如果数据库存放是此内容的话*/
               if("1899.12.30".equals(datestr))
            	   return "";
               if(len==7 && value.length()>=7)
              	 datestr=value.substring(0,4) + "." + value.substring(5,7);
               if("1899.12".equals(datestr))
               	  return "";
               if(len==4  && value.length()>=4)
            	 datestr=value.substring(0,4); 
               if("1899".equals(datestr))
               	return "";
              return datestr;
           }
           catch(ParseException pe)
           {
               //pe.printStackTrace();
               value=value.replaceAll("-",".");
               return value;               
           }
       }
       else
           return value;
    }
    
    /**格式化字符串*/
    public String formatItemType(String itemtype,int ndecimal,int len,String value)
    {
       if(value==null||value.length()<=0)
    	   return "";
       if("A".equals(itemtype))
       {
    	   if(value==null)
    		   return "";
           return value;
       }
       else if("N".equals(itemtype))
       {
            if(value==null|| "".equals(value))
                return "";
  	    	String pattern="";
   	    	float fldValue=0.0f;
   	    	if (ndecimal > 0) 
   	    	{
   	    	    pattern = "0.";
   	    	    for (int i = 0;i<ndecimal;i++)
   	    	        pattern += "0";
   	    	} 
   	    	else 
   	    	{
   	    	    pattern = "###"; 
   	    	}
   			if (value != null && value.length() > 0) 
   			{
   				fldValue = Float.parseFloat(value);
   				value=new DecimalFormat(pattern).format(fldValue).trim();
   			}
   			return value;
       }
       else if("D".equals(itemtype))
       {
           DateFormat df = DateFormat.getDateInstance();
           try
           {
               String datestr="";
               if(value==null|| "null".equalsIgnoreCase(value)||"undefined".equals(value))
            	   return "";
               if(len>=10&&len<18)
               {
            	   Date datevalue=df.parse(value);
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd");  
               }else if(len==10)
               {
            	   Date datevalue=df.parse(value);
            	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd HH:mm:ss");  
               }
               /**0:00:00,日期型指标,如果数据库存放是此内容的话*/
               if("1899.12.30".equals(datestr))
            	   return "";
               if(len==7 && value.length()>=7)
              	 datestr=value.substring(0,4) + "." + value.substring(5,7);
               if("1899.12".equals(datestr))
               	  return "";
               if(len==4  && value.length()>=4)
            	 datestr=value.substring(0,4); 
               if("1899".equals(datestr))
               	return "";
              return datestr;
           }
           catch(ParseException pe)
           {
               pe.printStackTrace();
               return value;               
           }
       }
       else
           return value;
    }

}
