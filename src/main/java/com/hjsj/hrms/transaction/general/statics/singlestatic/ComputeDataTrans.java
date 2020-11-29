/*
 * Created on 2006-1-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.statics.singlestatic;


import com.hjsj.hrms.businessobject.general.statics.singlestatic.SingleStaticBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ComputeDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//得到前台数据
		HashMap hm=this.getFormHM();
		//Sql_switcher.m
		String dbpre=(String)hm.get("dbpre");
		String userbases=(String)hm.get("userbases");
		String setname=(String)hm.get("setname");
		String select=(String)hm.get("select");
		String fieldname=(String)hm.get("fieldname");
		String time=(String)hm.get("time");
		String flag=(String)hm.get("flag");	
		String find=(String)hm.get("find");
		String datavalue ="";
		boolean bfind=false;
	    if(find==null|| "".equals(find)|| "0".equals(find))
	    {
	    	bfind=false;
	    }else{
	    	bfind=true;
	    }
	    try{
	   
		   String codesetid= userView.getManagePrivCode();
           String codevalue= userView.getManagePrivCodeValue();
		   //通过字典得到字段类型和有关信息
		    FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
		   //String itemtype=fielditem.getItemtype();
		   String fieldsetid = fielditem.getFieldsetid();
		   String fieldset = fieldsetid.substring(0,1);
		   String query ="";
		   String querywhere="";
		   //求得权限和相关类型
		   if(time==null|| "".equals(time))
		   {
			  SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd"); 
		      Date date = new Date(); 
		      time = formatter.format(date);
		   }
		

			 StringBuffer  strexpr=new StringBuffer();
	         StringBuffer  strfactor=new StringBuffer();
	         if("UN".equals(codesetid))
	         {
	             strfactor.append("B0110=");
	             strfactor.append(codevalue);
	             strfactor.append("*`");
	             strexpr.append("1");
	         }
	         else if("UM".equals(codesetid))
	         {
	             strfactor.append("E0122=");
	             strfactor.append(codevalue);
	             strfactor.append("*`");
	             strexpr.append("1");            
	        }
	        else if("@K".equals(codesetid))
	        {
	            strfactor.append("E01A1=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            strexpr.append("1");            
	        }
	        else
	        {
	           strfactor.append("B0110=*`");
	           strexpr.append("1");  
	        }
	        ArrayList fieldlist=new ArrayList();
	        if(!userView.isSuper_admin())
	        {
	        	if("A".equals(fieldset))
	        	{
	            //   query=" and "+userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist).substring(18);   	
	              query=" and "+dbpre+"a01.a0100 in (select "+dbpre+"A01.a0100 "+userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist)+")";
	               
	               querywhere=" "+userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist).substring(13);
	        	  // System.out.println(query);
	        	}
	        	else if("B".equals(fieldset))
	            {
	        		//dbpre="B";
	        		strfactor.delete(0,strfactor.length());
	        		strexpr.delete(0,strexpr.length());
	        		strfactor.append("B0110=");
		            strfactor.append(codevalue);
		            strfactor.append("*`");
		            strexpr.append("1");
		            String querysql=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),"B",true,fieldlist);
	            	query=" and "+querysql.substring(querysql.indexOf("WHERE") + 5);
	                querywhere=" "+querysql.substring(querysql.indexOf("WHERE"));
		            //System.out.println("B" + query);
		            //query=query.replaceAll("A01","B01");
		            //querywhere=querywhere.replaceAll("A01","B01");
	            }
	        }

		   SingleStaticBo singlestaticbo=new SingleStaticBo(this.getFrameconn(),this.userView);
		   String tem=userView.getUserName();
		   if(querywhere==null || querywhere!=null && querywhere.toLowerCase().indexOf("where")==-1)
			   querywhere=" where 1=1 ";
		   if(userbases==null||userbases.length()==0){
			   datavalue = singlestaticbo.getvalueandcount(dbpre.toUpperCase(),setname,select,fieldname,time,flag,query,querywhere,bfind,tem);
		   }else
			   datavalue = singlestaticbo.getvalueandcount(dbpre.toUpperCase(),setname,select,fieldname,time,flag,query,querywhere,bfind,tem,userbases);
		   
		  String value = datavalue.substring(0,datavalue.indexOf(","));
		  String date = PubFunc.DoFormatDecimal(value,2);
		  String count = datavalue.substring(datavalue.indexOf(",")+1);

		
		  this.getFormHM().put("count",count);
		  this.getFormHM().put("datavalue",date);
		  this.getFormHM().put("realdata",value);
		
	    } catch(Exception sqle)
        {
		         sqle.printStackTrace();
		         throw GeneralExceptionHandler.Handle(sqle);            
	       }
	}
}
