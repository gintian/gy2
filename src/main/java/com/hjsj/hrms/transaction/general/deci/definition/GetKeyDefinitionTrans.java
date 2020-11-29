package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetKeyDefinitionTrans extends IBusiness {

	public void execute() throws GeneralException {
  
        String factorid=(String)this.getFormHM().get("factorid");     
        ContentDAO dao=new ContentDAO(this.getFrameconn());       
        RecordVo vo=new RecordVo("ds_key_factor");
        try{
  	      	vo.setString("factorid",factorid);
  	      	vo=dao.findByPrimaryKey(vo);
        }catch(Exception sqle){
 	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
        	//this.getFormHM().put("fieldname",vo.getString("field_name"));
        	try
        	{
        		
        		this.frowset=dao.search("select fieldsetid from fielditem where itemid='"+vo.getString("field_name")+"'");
        		if(this.frowset.next())
        		{
        				this.getFormHM().put("fieldsetid",this.frowset.getString("fieldsetid"));
        		}
        		
        		this.frowset=dao.search("select itemid from fielditem where itemid='"+vo.getString("field_name")+"'");
        		if(this.frowset.next())
        		{
        				this.getFormHM().put("fieldname",this.frowset.getString("itemid").toLowerCase());
        		}
        		
        		
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	
        	this.getFormHM().put("codeitemvalue",vo.getString("codeitem_value"));
        	this.getFormHM().put("staticmethod",String.valueOf(vo.getInt("static_method"))); //统计方法
        	this.getFormHM().put("formula",vo.getString("formula"));

            
        }

	}

}
