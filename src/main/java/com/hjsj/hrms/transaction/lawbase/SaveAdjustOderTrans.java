/*
 * Created on 2006-3-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @author wxh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveAdjustOderTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	
	
		String [] lawbase=(String [])this.getFormHM().get("lawbase");
		if(lawbase==null||lawbase.length<=0)
		{
		  return;	
		}
		
		for(int i=0;i<lawbase.length;i++)
	    {  
	 		RecordVo vo=new RecordVo("law_base_struct");
	 		try
			{
	 		    
				ContentDAO dao = new ContentDAO(this.getFrameconn());
		 		vo.setString("base_id",lawbase[i]); 
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
