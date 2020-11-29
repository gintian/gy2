/**
 * 
 */
package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SearchChannelDetailTrans</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-15:上午09:42:07</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchChannelDetailTrans extends IBusiness {


	public void execute() throws GeneralException {
        String content_id=(String)this.getFormHM().get("content_id");
        String url_display = "";
        String content_display = "";
        int type= 0;
		if(content_id==null|| "".equals(content_id))
			return;
		try
		{   
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("t_cms_content");
			vo.setInt("content_id", Integer.parseInt(content_id));
			vo=dao.findByPrimaryKey(vo);
			type = vo.getInt("content_type");
			this.getFormHM().put("contentvo", vo);
			if(type == 0){
				url_display = "display:block";
				content_display = "display:none";
			}
			if(type==1){
				url_display = "display:none";
				content_display = "display:bolck";
			}
			this.getFormHM().put("display",url_display);
			this.getFormHM().put("content_display",content_display);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
