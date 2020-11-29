package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:TemplateSaveAsTrans.java</p>
 * <p>Description>:TemplateSaveAsTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-26 下午03:03:27</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class TemplateSaveAsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String oldid = (String)this.getFormHM().get("templatesetid");
			String templatename=SafeCode.decode((String)this.getFormHM().get("templatename"));
			String topscore=(String)this.getFormHM().get("topscore");
			String status = (String)this.getFormHM().get("status");
			String newid=(String)this.getFormHM().get("templateid");	
			String parentsetid=(String)this.getFormHM().get("parentsetid");	
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			int seq = bo.getMaxId("per_template", "seq");
			bo.templateSaveAs(oldid, newid, seq, templatename, topscore, status,parentsetid);
			if(!(this.userView.isSuper_admin())&&!"1".equals(this.userView.getGroupId()))
			{
				UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
				user_bo.saveResource(newid,this.userView,IResourceConstant.KH_MODULE);
			}
			this.getFormHM().put("id",newid);
			this.getFormHM().put("name",SafeCode.encode(templatename));
			this.getFormHM().put("type",type);
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("parentsetid",parentsetid);
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
