package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveRnameTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveRnameTrans  extends IBusiness {

  
    public void execute() throws GeneralException {
        ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");    
        saveRname(code_fields);
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
	    	if(code_fields==null||code_fields.size()<=0)
	    		return "";	
	    	String sql="";
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO (this.getFrameconn());
	    		mess.append("<br>");
	    		int r=1;
	    		StringBuffer inStr=new StringBuffer();
	    		for(int i=0;i<code_fields.size();i++)
		    	{
	    			inStr.append("'"+code_fields.get(i).toString()+"',");
		    	}
	    		if(inStr==null||inStr.length()<=0)
	    			return "";
	    		inStr.setLength(inStr.length()-1);
	    		sql="select name  from rname where tabid in("+inStr.toString()+") order by tabid";
	    		this.frowset=dao.search(sql);
	    		while(this.frowset.next())
	    		{
	    			 mess.append(this.frowset.getString("name"));
	    			 if(r%5==0)
		    			   mess.append("<br>");
		    		   else
		    			 mess.append(",");  
		    	    r++;
	    		}
	    		mess.append("<br>");
	    	}catch(Exception e)
	    	{
	    	  e.printStackTrace();	
	    	}
	    	return mess.toString();
	  }
	  private boolean saveRname(ArrayList code_fields)throws GeneralException
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
		  EngageParamXML engageParamXML=new EngageParamXML(this.getFrameconn()); 	 
		  isCorrect=engageParamXML.setTextValue(EngageParamXML.CARD,buf.toString());
		  engageParamXML.saveParameter();
		  return isCorrect;
	  }
}

