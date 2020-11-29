/*
 * Created on 2005-10-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveFieldItemTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		String[] fielditemvalue=(String[])this.getFormHM().get("fielditemvalue");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list=new ArrayList();
		RecordVo vo= ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
		try{
		   if(vo == null){
			  String sql = "insert into constant(constant,type,str_value,Describe) values('ZP_FIELD_LIST','','','采集指标')";
			  dao.update(sql,list);
		   }
		}catch(SQLException sqle){
			sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);
		}
		StringBuffer fielditemstr = new StringBuffer();   
	    try
		{
	    	if(fielditemvalue != null){
	    	   for(int i=0;i<fielditemvalue.length;i++)
		 	   {
	    		   fielditemstr.append(fielditemvalue[i].toUpperCase());       //List中的对象转换成字符串
	    		   fielditemstr.append(",");
		 	   }
	    	}
	    	 String fieldlist=vo.getString("str_value");
	    	 int fieldindex = fieldlist.indexOf(fieldsetid);
	    	 if(fieldindex == -1 && fielditemstr.toString() != null && !"".equals(fielditemstr.toString())){
	    	 	String field = fieldlist + fieldsetid + "{" + fielditemstr.toString() + "},";
	    	 	String ssql = "update constant set str_value = '"+field+"' where constant = 'ZP_FIELD_LIST'";
	    	 	dao.update(ssql,list);
	    	 }else if(fieldindex != -1 && (fielditemstr.toString() == null || "".equals(fielditemstr.toString()))){
	    	 	String substr = fieldlist.substring(fieldindex,fieldlist.length());
	    	 	int subindex = substr.indexOf("},");
	    	 	String firststr = fieldlist.substring(0,fieldindex);
	    	 	String laststr = fieldlist.substring(fieldindex+subindex+2,fieldlist.length());
	    	 	String sumstr = firststr+laststr;
	    	 	String sqlsql = "update constant set str_value = '"+sumstr+"' where constant = 'ZP_FIELD_LIST'";
	    	 	dao.update(sqlsql,list);
	    	 }else if(fieldindex != -1 && fielditemstr.toString() != null && !"".equals(fielditemstr.toString())){
	    	 	String substr = fieldlist.substring(fieldindex,fieldlist.length());
	    	 	int subindex = substr.indexOf("},");
	    	 	String firststr = fieldlist.substring(0,fieldindex);
	    	 	String laststr = fieldlist.substring(fieldindex+subindex,fieldlist.length());
	    	 	String midstr = fieldsetid + "{" +fielditemstr;
	    	 	String sumstr = firststr+midstr+laststr;
	    	 	String sqlsql = "update constant set str_value = '"+sumstr+"' where constant = 'ZP_FIELD_LIST'";
	    	 	dao.update(sqlsql,list);
	    	 }	
		}catch(SQLException e)
		{
			e.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("fieldsetid",fieldsetid);
		}
	}

}
