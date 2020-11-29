/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.logonuser.UserGroupBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * <p>Title:AddUserGroupTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-7:15:02:07</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class AddUserGroupTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		String groupname=(String)this.getFormHM().get("groupname");
		groupname = com.hjsj.hrms.utils.PubFunc.keyWord_reback(groupname);
		String currname=(String)this.getFormHM().get("currname");
		try
		{
			if(groupname==null|| "".equals(groupname))
				throw new GeneralException(ResourceFactory.getProperty("error.usergroup.null"));
			/**根据当前用户选中树节点，定位新建的用户节点*/
			UserObjectBo bo=new UserObjectBo(this.getFrameconn());
			/**求当前所在组号*/
			int groupid=1;
			if(!currname.equalsIgnoreCase(ResourceFactory.getProperty("label.user.group")))
				groupid=bo.getCurrentGroupId(currname);
			if(StringUtils.isNumeric((groupname.substring(0,1))))
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.user.number")));
			
			RecordVo vo=new RecordVo("usergroup");
			vo.setString("groupname",groupname);
			UserGroupBo groupbo=new UserGroupBo(this.getFrameconn());
			groupbo.add_UserGroup(vo,groupid);
			this.getFormHM().put("groupname",groupname);
			this.getFormHM().put("groupid",vo.getString("groupid"));			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
