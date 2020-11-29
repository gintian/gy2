package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 保存指标
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 10, 2007:4:24:41 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveFieldsTrans extends IBusiness {

  
    public void execute() throws GeneralException {
        ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");
        if(code_fields==null){
        	code_fields = new ArrayList();
        }
        String field_falg=(String)this.getFormHM().get("field_falg");
        if(!"photo_other_view".equalsIgnoreCase(field_falg)){
        	saveFields(code_fields,field_falg);
        }
        String mess ="";
        String code = "";
        for(int i=0;i<code_fields.size();i++)
    	{
        	if(code_fields.get(i).toString()!=null){
        		code+=code_fields.get(i).toString()+",";
        	}
    	}
        if(!"unitfile".equalsIgnoreCase(field_falg)){
        	if("photo_other_view".equalsIgnoreCase(field_falg)){
        		mess=getUsedMesslist(code_fields);
        		this.getFormHM().put("code",code);
        	}else{
        		mess=getCardMesslist(code_fields);
        	}
        }else
        	mess=getUnitMesslist(code_fields);
		this.getFormHM().put("types","ok");
		this.getFormHM().put("mess",mess);
	}
    private String getUsedMesslist(ArrayList code_fields) {
    	StringBuffer mess=new StringBuffer();
    	if(code_fields==null||code_fields.size()<=0)
    		return "";	
    	String sql="";
    	try
    	{
    		ContentDAO dao=new ContentDAO (this.getFrameconn());
//    		mess.append("<br>");
    		for(int i=0;i<code_fields.size();i++)
	    	{
	    		if(code_fields.get(i).toString()!=null&& "b0110".equals(code_fields.get(i).toString()))
	    			mess.append("单位名称,");
	    		if(code_fields.get(i).toString()!=null&& "e01a1".equals(code_fields.get(i).toString()))
	    			mess.append("岗位名称,");		
	    		sql="select itemid,itemdesc from fielditem where Upper(itemid)='"+code_fields.get(i).toString().toUpperCase()+"'";
	    		RowSet rs=dao.search(sql);
	    		if(rs.next())
		    	{
		    		   mess.append(rs.getString("itemdesc")+",");
		    	}
	    	}
    	}catch(Exception e)
    	{
    	  e.printStackTrace();	
    	}
    	return mess.toString();
	}
	/**
	  * 通过表编号的到表信息
	  * @param cardno
	  * @return String
	  */
	 public String getCardMesslist(ArrayList code_fields)
	 {
	    	StringBuffer mess=new StringBuffer();
	    	if(code_fields==null||code_fields.size()<=0)
	    		return "";	
	    	String sql="";
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO (this.getFrameconn());
//	    		mess.append("<br>");
	    		int r=1;
	    		for(int i=0;i<code_fields.size();i++)
		    	{
		    		if(code_fields.get(i).toString()!=null&& "b0110".equals(code_fields.get(i).toString()))
		    			mess.append("单位名称,");
		    		if(code_fields.get(i).toString()!=null&& "e01a1".equals(code_fields.get(i).toString()))
		    			mess.append("岗位名称,");		
		    		sql="select itemid,itemdesc from fielditem where Upper(itemid)='"+code_fields.get(i).toString().toUpperCase()+"'";
		    		RowSet rs=dao.search(sql);
		    		if(rs.next())
			    	{
			    		   mess.append(rs.getString("itemdesc")+",");
			    	}
		    		if(r%5==0)
		    			   mess.append("<br>");
		    		 //  else
		    		//	 mess.append(",");  
		    	    r++;
		    	}
	    		 mess.append("<br>");
	    	}catch(Exception e)
	    	{
	    	  e.printStackTrace();	
	    	}
	    	return mess.toString();
	  }
	 public String getUnitMesslist(ArrayList code_fields)
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
	    		for(int i=0;i<code_fields.size();i++)
		    	{    		
		    		sql="select fieldsetid,fieldsetdesc from fieldset where fieldsetid='"+code_fields.get(i).toString()+"'";
		    		RowSet rs=dao.search(sql);
		    		if(rs.next())
			    	{
			    		   mess.append(rs.getString("fieldsetdesc"));
			    	}
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
	  private boolean saveFields(ArrayList code_fields,String field_falg)throws GeneralException 
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
		  int flag=0;
		  LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn()); 
		  if("output".equals(field_falg))
		  {
			  flag=LeadarParamXML.OUTPUT;
		  }else if("display".equals(field_falg))
		  {
			  flag=LeadarParamXML.DISPLAY;
		  }else if("condi_display".equals(field_falg))
		  {
			  flag=LeadarParamXML.CONDI_DISPLAY;
		  }else if("unitfile".equals(field_falg))
		  {
			  flag=LeadarParamXML.UNIT_ZJ;
		  }
		  isCorrect=leadarParamXML.setTextValue(flag,buf.toString());
		  leadarParamXML.saveParameter();
		  return isCorrect;
	  }
	 
}
