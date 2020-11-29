package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveChangeOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	String [] order=(String [])this.getFormHM().get("order");
	if (order == null) {
		
		return;
	}
	
		for(int i=0;i<order.length;i++)
	    {  
	 		RecordVo vo=new RecordVo("kq_item");
	 		try
			{
	 		    
				ContentDAO dao = new ContentDAO(this.getFrameconn());
		 		vo.setString("item_id",order[i]); 
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
