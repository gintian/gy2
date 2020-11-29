package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.ExportXmlBo;
import com.hjsj.hrms.businessobject.sys.export.ExportSearchSQLStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.JDOMException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ExportSaveTrans extends IBusiness {

    public void execute() throws GeneralException { 	
    	HashMap hm=this.getFormHM();
        String tab_name=(String)hm.get("tab_name");
        
        ExportXmlBo export = new ExportXmlBo(this.getFrameconn(),"SYS_EXPORT");
        ExportSearchSQLStr sqlStr = new ExportSearchSQLStr();
        Connection conn = this.getFrameconn();

        String code = (String)hm.get("code");
        hm.remove("code");
        hm.put("code",code);
        
        String field = (String)hm.get("field");
        hm.remove("field");
        hm.put("field",field);
        
        String transcode = (String)hm.get("transcode");
        hm.remove("transcode");
        hm.put("transcode",transcode);
        
        String strtoutf = (String)hm.get("strtoutf");
        hm.remove("strtoutf");
        hm.put("strtoutf",strtoutf);

        String str="";
        try{
	        if(tab_name==null|| "".equals(tab_name))
	            return;
	        /**
	         * 保存主集
	         */
	        if("dbpriv".equals(tab_name)){
	            saveDbPriv(export,code,field,transcode,strtoutf);
	            str=sqlStr.searchDbNameHtml(conn);
	        }
	        /**
	         * 保存子集
	         */
	        if("tablepriv".equals(tab_name)){
	            saveTablePriv(export);
	            saveRoot(code,field,transcode,strtoutf);
	            str=sqlStr.searchTablePriv(conn);
	        }
	        /**
	         * 保存指标
	         */
	        if("fieldpriv".equals(tab_name)){
	            saveFieldPriv(export);
	            saveRoot(code,field,transcode,strtoutf);
	            str=sqlStr.searchFieldPriv(conn);
	        }  
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        
        hm.put("script_str",str);         
        hm.put("tab_name",tab_name);
        
        hm.remove("path");
        hm.put("path","no");
    }
    /**
     * 保存采集指标
     * 2007-5-21 
     * @throws JDOMException 
     * @throws GeneralException 
     */
	  private void saveFieldPriv(ExportXmlBo export){
	    String field_str=(String)this.getFormHM().get("field_set_str");
	    StringBuffer func_str=new StringBuffer();   	
        StringBuffer strsql=new StringBuffer();  
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		
		
		String[] fieldsetid = null;
		if(field_str!=null&&field_str.length()>0){
			field_str = field_str.substring(0,field_str.lastIndexOf(","));
			fieldsetid = field_str.split(",");
		}
        func_str.append(export.alertSubsetValue(fieldsetid)); 
		try{
			strsql.append("update constant set str_value=? where constant='SYS_EXPORT'");
			List paralist=new ArrayList();
			paralist.add(func_str.toString());
			dao.update(strsql.toString(), paralist);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    /**
     * 保存子集
     * @throws JDOMException 
     * @throws GeneralException 
     */
    private void saveTablePriv(ExportXmlBo export){
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer func_str=new StringBuffer();   	
        StringBuffer strsql=new StringBuffer();  
        String[] func = (String[])this.getFormHM().get("func");
        func_str.append(export.alertSubset(func));
        try{
			strsql.append("update constant set str_value=? where constant='SYS_EXPORT'");
			List paralist=new ArrayList();
			paralist.add(func_str.toString());
			dao.update(strsql.toString(), paralist);
       }catch(SQLException e){
          e.printStackTrace();
       }
    }
    /**
     * 保存主集
     * @throws JDOMException 
     * @throws GeneralException 
     */
    private void saveDbPriv(ExportXmlBo export,String code,String filed,String transcode,String strtoutf){
        StringBuffer func_str=new StringBuffer(); 
        ContentDAO dao=new ContentDAO(this.getFrameconn());

        StringBuffer strsql=new StringBuffer();
        
        String field_set_str = (String)this.getFormHM().get("field_set_str"); 
        if(field_set_str.length()>0){
        	field_set_str = field_set_str.substring(0,field_set_str.lastIndexOf(","));
  
        	String[] func=field_set_str.split(",");
        	String[] flag = new String[func.length];
        	for(int i=0;i<func.length;i++){
        		if("B".equals(func[i])){
        			flag[i]="B";
        		}else if("K".equals(func[i])){
        			flag[i]="K";
        		}else{
        			flag[i]="A";
        		}
        	}
        	func_str.append(export.alertBase(func,flag,code,filed,transcode,strtoutf));
        }else{
        	func_str.append(export.alertBase(null,null,code,filed,transcode,strtoutf));
        }
        try{
			strsql.append("update constant set str_value=? where constant='SYS_EXPORT'");
			List paralist=new ArrayList();
			paralist.add(func_str.toString());
			dao.update(strsql.toString(), paralist);        	
       }catch(SQLException e){
          e.printStackTrace();
       }
    }
    /**
     * 修改是否导出代码体系和子标体系
     * @throws JDOMException 
     * @throws GeneralException 
     */
    private void saveRoot(String code,String filed,String transcode,String strtoutf){
    	ExportXmlBo export = new ExportXmlBo(this.getFrameconn(),"SYS_EXPORT");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer func_str=new StringBuffer();   	
        StringBuffer strsql=new StringBuffer();  
        func_str.append(export.alertRoot(code,filed,transcode,strtoutf));
        try
        {
			strsql.append("update constant set str_value=? where constant='SYS_EXPORT'");
			List paralist=new ArrayList();
			paralist.add(func_str.toString());
			dao.update(strsql.toString(), paralist);
       }catch(SQLException e){
          e.printStackTrace();
       }
    }
}
