package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class KqClassTreeTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
	  HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
	  String class_id=(String)this.getFormHM().get("class_id");
	  String class_flag=(String)this.getFormHM().get("class_flag");	  
	  if(class_flag==null||class_flag.length()<=0)
	    return;
	  if("add".equals(class_flag))
	  {
		  this.getFormHM().put("class_id","");
		  this.getFormHM().put("class_name","");
	  }else if("up".equals(class_flag))
	  {
		  String class_name=selectKaClass(class_id);
		  this.getFormHM().put("class_id",class_id);
		  this.getFormHM().put("class_name",class_name);
	  }
	  this.getFormHM().put("class_flag",class_flag);
	}
	
    /**
     * 
     * @param class_id
     * @throws GeneralException
     */
    public String selectKaClass(String class_id)throws GeneralException
    {
    	if(class_id==null||class_id.length()<=0)
    		return "";
    	String name="";
    	String sql="select "+this.kq_class_name+" from "+this.kq_class_table+" where "+this.kq_class_id+"=?";
    	ArrayList list=new ArrayList();
    	list.add(class_id);
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		this.frowset=dao.search(sql,list);
    		if(this.frowset.next())
    		{
    			name=this.frowset.getString(this.kq_class_name);
    		}
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();
    	   throw GeneralExceptionHandler.Handle(e);
    	}
    	return name;
    }   
}
