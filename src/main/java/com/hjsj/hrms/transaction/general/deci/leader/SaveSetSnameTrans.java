package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 常用统计分析
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 8, 2007:4:40:36 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveSetSnameTrans extends IBusiness {

  
    public void execute() throws GeneralException {
        ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");   
        saveSname(code_fields);
		this.getFormHM().put("mess",getSnameMesslist(code_fields));
	}
    /**
	  * 通过表编号的到表信息
	  * @param cardno
	  * @return String
	  */
	 public String getSnameMesslist(ArrayList code_fields)
	 {
	    	StringBuffer mess=new StringBuffer();
	    	HashMap codeMaps = new HashMap();
	    	if(code_fields==null||code_fields.size()<=0)
	    		return "";	
	    	String sql="";
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO (this.getFrameconn());
	    		//mess.append("<br>");
	    		int r=1;
	    		StringBuffer inStr=new StringBuffer();
	    		for(int i=0;i<code_fields.size();i++)
		    	{
	    			inStr.append("'"+code_fields.get(i).toString()+"',");
		    	}
	    		if(inStr==null||inStr.length()<=0)
	    			return "";
	    		inStr.setLength(inStr.length()-1);
	    		sql="select name ,id from sname where id in("+inStr.toString()+") ";
	    		this.frowset=dao.search(sql);
	    		while(this.frowset.next())
	    		{
	    			 String id=this.frowset.getString("id");
	    			 codeMaps.put(id, this.frowset.getString("name"));
	    		}
	    		
	    		for(int i=0;i<code_fields.size();i++)
		    	{
	    			mess.append(codeMaps.get(code_fields.get(i))+",");
	    			if((i+1)%5==0)
		    			   mess.append("<br>");
		    	}
	    		mess.append("<br>");
	    	}catch(Exception e)
	    	{
	    	  e.printStackTrace();	
	    	}
	    	return mess.toString();
	  }
	 private boolean saveSname(ArrayList code_fields)throws GeneralException
	  {
		  boolean isCorrect=false;
		  StringBuffer buf=new StringBuffer();
		  if(code_fields==null||code_fields.size()<=0)
			  buf.append("");
		  else
		  {
			  for(int i=0;i<code_fields.size();i++)
			  {
				  buf.append(""+code_fields.get(i).toString()+",");
			  }
			  buf.setLength(buf.length()-1);
		  }
		  LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn()); 	 
		  isCorrect=leadarParamXML.setTextValue(leadarParamXML.GCOND,buf.toString());
		  leadarParamXML.saveParameter();
		  return isCorrect;
	  }
}

