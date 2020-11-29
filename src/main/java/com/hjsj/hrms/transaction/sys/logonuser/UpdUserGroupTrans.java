package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.logonuser.UserGroupBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title: UpdUserGroupTrans </p>
 * <p>Description: 修改用户组名称</p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-4-28 下午3:54:57</p>
 * @author jingq
 * @version 1.0
 */
public class UpdUserGroupTrans extends IBusiness{

	public void execute() throws GeneralException {
		String oldname = ((String) this.getFormHM().get("oldname")).trim();
		String newname = ((String) this.getFormHM().get("newname")).trim();
		try {
			UserGroupBo uob = new UserGroupBo(this.getFrameconn());
			uob.upd_group(newname,oldname);
			this.getFormHM().put("newname", newname);
			this.getFormHM().put("oldname", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.usergroup.exist"))) ;
		}
	}

}
