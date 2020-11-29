package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.ExportXmlBo;
import com.hjsj.hrms.businessobject.sys.export.ExportSearchSQLStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${user}
 *@version 4.0
  */
public class ExportSearchTrans extends IBusiness {
	    public void execute() throws GeneralException {
	    	HashMap hm=this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
	        String	tab_name=(String)reqhm.get("a_tab");
	        
	        reqhm.remove("a_tab");
	        
	        ExportXmlBo export = new ExportXmlBo(this.getFrameconn(),"SYS_EXPORT");
		    ArrayList code = (ArrayList)export.elementName("/root","code");
		    ArrayList field = (ArrayList)export.elementName("/root","field");
		    ArrayList transcode = (ArrayList)export.elementName("/root","transcode");
		    ArrayList strtoutf = (ArrayList)export.elementName("/root","strtoutf");
		    if(code.size()>0){
		    	hm.put("code",code.get(0)); 
		    }else{
		    	hm.put("code","false"); 
		    }
		    if(field.size()>0){
		    	hm.put("field",field.get(0)); 
		    }else{
		    	hm.put("field","false"); 
		    }
		    if(transcode.size()>0){
		    	hm.put("transcode",transcode.get(0)); 
		    }else{
		    	hm.put("transcode","false"); 
		    }
		    if(strtoutf.size()>0){
		    	hm.put("strtoutf",strtoutf.get(0)); 
		    }else{
		    	hm.put("strtoutf","false"); 
		    }
		    
	        ExportSearchSQLStr sqlStr = new ExportSearchSQLStr();
	        Connection conn = this.getFrameconn();
	        
	        if(tab_name==null|| "".equals(tab_name))
	            return;
	        String str="";
	        try{
		        /**
		         * 人员库授权
		         */
		        if("dbpriv".equals(tab_name)){
		            str=sqlStr.searchDbNameHtml(conn);
		        }
		       
		        /**
		         * 子集授权
		         */
		        if("tablepriv".equals(tab_name)){
		            str=sqlStr.searchTablePriv(conn);
		        }
		        /**
		         * 指标授权
		         */
		        if("fieldpriv".equals(tab_name)){
		           str=sqlStr.searchFieldPriv(conn);
		        }
	        }
	        catch(Exception ee){
	        	ee.printStackTrace();
	        	throw GeneralExceptionHandler.Handle(ee);
	        }
	        /**
	         * save the role_id.
	         */
	        hm.put("script_str",str);         
	        hm.put("tab_name",tab_name);
	        hm.remove("path");
	        hm.put("path","no");
	    }

}
