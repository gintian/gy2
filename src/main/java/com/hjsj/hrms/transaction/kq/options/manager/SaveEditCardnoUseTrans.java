package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**编辑
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 21, 2008</p> 
 *@author sunxin
 *@version 4.0
 */

public class SaveEditCardnoUseTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String nbase=(String)this.getFormHM().get("nbase");
    	String a0100=(String)this.getFormHM().get("a0100");
    	String i9999=(String)this.getFormHM().get("i9999");
    	if(i9999==null||i9999.length()<=0)
    		i9999="1";
    	String magcard_setid=(String)this.getFormHM().get("magcard_setid");
    	ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
		ArrayList value_arr = (ArrayList)this.getFormHM().get("itemvalue_arr");
		
		if (itemid_arr == null) {
			itemid_arr = new ArrayList();
		}
		
		if (value_arr == null) {
			value_arr = new ArrayList();
		}
		
		String strTableName=nbase+magcard_setid;
    	RecordVo vo=new RecordVo(strTableName);    
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	ArrayList newfieldlist=kqCrads.checkDate(itemid_arr,value_arr);
    	if(newfieldlist==null||newfieldlist.size()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","没有记录或保存操作出错！","",""));
    	for(int i=0;i<newfieldlist.size();i++)
    	{
    		FieldItem field=(FieldItem)newfieldlist.get(i);       		
    		 if("N".equals(field.getItemtype()))
             {
          	   if(field.getValue()!=null&&field.getValue().length()>0)
          	   {
          		   vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
          	   }
             }else  if("D".equals(field.getItemtype()))
 	   		 {
            	 if(field.getValue()!=null&&field.getValue().length()>0)
            	 {
            		 java.util.Date dd=DateUtils.getDate(field.getValue(),"yyyy-MM-dd");
               		 vo.setDate(field.getItemid().toLowerCase(),dd);
            	 }
 	   		 }else{
 	   			 vo.setString(field.getItemid().toLowerCase(),field.getValue());
        	 }
    	}
    	vo.setString("a0100", a0100);
    	vo.setString("i9999", i9999);    	
    	String flag="0";
    	
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		String sql="select 1 from "+strTableName+" where a0100='"+a0100+"' and i9999='"+i9999+"'";
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
    		  dao.updateValueObject(vo);    
    		else
    		  dao.addValueObject(vo);   
    	}catch(Exception e)
    	{
    		flag="1";
    		e.printStackTrace();
    	}
    	this.getFormHM().put("flag",flag);
    }
}
