
package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveAdjustOderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Dec 12, 2008:3:18:48 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveAdjustOderTrans extends IBusiness {
	public void execute() throws GeneralException {
//		String [] kqlist=(String [])this.getFormHM().get("kqlist");
		ArrayList kqlist = (ArrayList)this.getFormHM().get("kqlist");
		if(kqlist==null||kqlist.size()<=0)
		{
		  return;	
		}
		for(int i=0;i<kqlist.size();i++)
	    {  
	 		RecordVo vo=new RecordVo("kq_class");
	 		try
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
		 		vo.setString("class_id",(String)kqlist.get(i)); 
				vo=dao.findByPrimaryKey(vo);
		 		vo.setInt("displayorder",i+1);
		 		dao.updateValueObject(vo);
		 		
				}
		        catch(Exception ex)
				{
		        	ex.printStackTrace();
				}
	    }
	    
	}

}
