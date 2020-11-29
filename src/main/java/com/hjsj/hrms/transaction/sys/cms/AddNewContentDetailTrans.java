/**
 * 
 */
package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 *<p>Title:AddNewContentDetailTrans</p> 
 *<p>Description:清空对象中的内容</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-14:下午05:50:01</p> 
 *@author cmq
 *@version 4.0
 */
public class AddNewContentDetailTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=this.getFormHM();
			RecordVo vo=(RecordVo)hm.get("contentvo");
			vo.clearValues();
			this.getFormHM().put("contentvo", vo);
			this.getFormHM().put("display","display:block");
			this.getFormHM().put("content_display","display:block");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

}
