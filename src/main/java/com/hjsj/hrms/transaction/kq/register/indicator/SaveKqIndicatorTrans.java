package com.hjsj.hrms.transaction.kq.register.indicator;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveKqIndicatorTrans extends IBusiness{
    public void execute()throws GeneralException
    {
       ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");
       String state[]=(String[])this.getFormHM().get("state");
       StringBuffer updateSQL=new StringBuffer();
       updateSQL.append("update t_hr_busifield set ");
       updateSQL.append("state=? where UPPER(FieldSetId)=? and UPPER(ItemId)=?");
       ArrayList list=new ArrayList();
       for(int i=0;i<fieldlist.size();i++)
       {
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			ArrayList onelist=new ArrayList();
			onelist.add(state[i]);
			onelist.add(fielditem.getFieldsetid().toUpperCase());
			onelist.add(fielditem.getItemid().toUpperCase());
            list.add(onelist);			
            
       }
       ContentDAO dao=new ContentDAO(this.getFrameconn());
       try
       {
    	   dao.batchUpdate(updateSQL.toString(),list);
    	   DataDictionary.refresh();
       }catch(Exception e)
       {
    	   throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
       }
    }
}
