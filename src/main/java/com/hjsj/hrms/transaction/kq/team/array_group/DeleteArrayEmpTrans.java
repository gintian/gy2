package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteArrayEmpTrans extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");	
		if(selectedinfolist==null)
			return;
		String group_id=(String)this.getFormHM().get("group_id");
		ArrayList deletelist=new ArrayList();
		StringBuffer delStr=new StringBuffer();
		delStr.append("delete from "+kq_group_emp_table);
		delStr.append(" where group_id=? and nbase=? and a0100=?");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	for(int i=0;i<selectedinfolist.size();i++)
	        {
				
	    	    LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);   
	       	    String nbase=rec.get("nbase").toString();
	          	String a0100=rec.get("a0100").toString();
	       	    ArrayList list=new ArrayList();
	       	    list.add(group_id);
	       	    list.add(nbase);	
	       	    list.add(a0100);
	       	    deletelist.add(list );  	    
	       	          	              	
	        }
	    	dao.batchUpdate(delStr.toString(),deletelist);	 
	    }catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("delete.error"),"",""));
	    }
	}
}
