package com.hjsj.hrms.businessobject.kq.options.kqcrad;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;


public class OrderCardEmp {
	public   class   PYComparator   implements   Comparator
	{   
		  @Override
          public   int   compare(Object   o1, Object   o2)
		  {   
			  try   {   
				  byte[]   buf1   =   ((String)o1).getBytes("GB2312");   
				  byte[]   buf2   =   ((String)o2).getBytes("GB2312");				  
				  int   size   =   Math.min(buf1.length,buf2.length);   
				  for(int   i   =   0;   i   <   size;   i++)   {   
				  if(buf1[i]   <   buf2[i]) {
					  return   -1;
				  } else   if(buf1[i]   >   buf2[i]) {
					  return   1;
				  }
				  }   
				  return   buf1.length   -   buf2.length;   
				  }   
				  catch   (UnsupportedEncodingException   ex)   {   
				  return   0;   
				  }
		  }   
   }   
   public String[] orderByStr(String[] str)
   {
	      Arrays.sort(str,new   PYComparator());   
		  /*for(int   i   =   0   ;   i   <   str.length;   i++)   
		  System.out.println(str[i]);   */
		  return str;
   }
}
