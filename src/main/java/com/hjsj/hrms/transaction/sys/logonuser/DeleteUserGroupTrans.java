/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.infor.CleanPersonSetting;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserGroupBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteUserGroupTrans</p>
 * <p>Description:删除用户或用户组交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-7:17:08:57</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteUserGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		String currname=(String)this.getFormHM().get("currname");
		currname = com.hjsj.hrms.utils.PubFunc.keyWord_reback(currname);
		RecordVo vo=new RecordVo("operuser");
		vo.setString("username",currname);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		int nroleid=0;
		try
		{
			if(!this.userView.hasTheFunction("08020105") && !this.userView.hasTheFunction("3003105"))// 自助或业务用户 是否有删除权限校验    wangb 20171218
				throw new Exception("操作超出权限！");
			
			if(!this.userView.isSuper_admin()){//删除权限校验    wangb 20171218
    			String sql = "select '1' c from operuser where username=? and groupid=? ";
    			ArrayList values = new ArrayList();
    			values.add(currname);
    			values.add(this.userView.getGroupId());
    			this.frowset = dao.search(sql, values);
    			if(!this.frowset.next())
    				throw new Exception("操作超出权限！");
			}
			vo=dao.findByPrimaryKey(vo);
			nroleid=vo.getInt("roleid");
			UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
			int groupid=userbo.getCurrentGroupId(currname);
			if(nroleid==0)
			{
				userbo.remove_User(vo,true);
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.operuser.del")+currname);
				String login_history_pwd = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD);
		        if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))||SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS).length()>0||!(login_history_pwd==null||login_history_pwd.length()==0||"0".equals(login_history_pwd))){
	            	userbo.delUserName(currname);
	            }
			}
			else
			{
				RecordVo gvo=new RecordVo("usergroup");
				gvo.setInt("groupid",groupid);
				gvo.setString("groupname",currname);
				UserGroupBo groupbo=new UserGroupBo(this.getFrameconn());
				groupbo.removeUserGroup(gvo);
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.usergroup.del")+currname);
			}
			//add by xiegh on 20170915 删除业务用户同步删除用户面板信息
			if(null!=currname)
				if(!CleanPersonSetting.cleanByUsername(currname))
					throw GeneralExceptionHandler.Handle(new Exception("同步人员面板信息错误！"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
