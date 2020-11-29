package com.hjsj.hrms.transaction.general.query.complex;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 删除
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class DeleteComplexInterfaceTrans  extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{		String id=(String)this.getFormHM().get("complex_id");
		
		RecordVo vo=new RecordVo("gwhere");
		vo.setInt("id", Integer.parseInt(id));
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			
			dao.deleteValueObject(vo);
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
	}

}
