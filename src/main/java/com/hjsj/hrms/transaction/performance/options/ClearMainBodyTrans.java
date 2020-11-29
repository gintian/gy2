package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>Title:ClearMainBodyTrans.java</p>
 * <p>Description:清除考核主体筛选条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-15 11:11:11</p>
 * @author JinChunhai
 * @version 6.0
 */

public class ClearMainBodyTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{	
		
		String bodyType = (String)this.getFormHM().get("bodyType");
		String deletestr = (String)this.getFormHM().get("deletestr");
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		try 
		{
			if(deletestr!=null && deletestr.trim().length()>0)
			{
				String typeids = deletestr.substring(0,deletestr.length()-1);
				String [] temp = typeids.replaceAll("／", "/").split("/");
				for(int i=0;i<temp.length;i++)
				{				
					RecordVo vo = new RecordVo("per_mainbodyset");
					vo.setString("body_id", temp[i].trim());
					vo = dao.findByPrimaryKey(vo);
					vo.setString("cond", "");
					vo.setString("cexpr", "");
					dao.updateValueObject(vo);				
				}
				
				this.getFormHM().put("info","true");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}