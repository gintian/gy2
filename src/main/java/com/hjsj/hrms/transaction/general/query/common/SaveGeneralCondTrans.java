/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SaveGeneralCondTrans</p> 
 *<p>Description:保存修改过的通用条件</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-5-21:下午06:23:40</p> 
 *@author cmq
 *@version 4.0
 */
public class SaveGeneralCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		String id=(String)this.getFormHM().get("curr_id");
		String lexpr=(String)this.getFormHM().get("lexpr");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			lexpr = PubFunc.hireKeyWord_filter_reback(lexpr);
			int index = lexpr.indexOf("|");
			RecordVo vo=new  RecordVo("lexpr");
			vo.setString("id",id );
			String lexpr0=PubFunc.keyWord_reback(lexpr.substring(0, index));
			String factor=PubFunc.keyWord_reback(lexpr.substring(index + 1));
			vo.setString("lexpr",lexpr0);
			vo.setString("factor", factor);
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
