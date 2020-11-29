/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Title:UpdateUserGroupTrans</p>
 * <p>Description:修改用户及用户组信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-9:15:05:52</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UpdateUserGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			DynaBean user_vo=(LazyDynaBean)this.getFormHM().get("user_vo");
			String username=(String)user_vo.get("username");
			//String password=(String)user_vo.get("password");
			//password=SafeCode.decode(password);
			String fullname=(String)user_vo.get("fullname");
			//处理特殊字符  jingq  add  2014.6.3
			fullname = SafeCode.decode(fullname);
			if(fullname!=null&&fullname.length()>0){
				fullname = fullname.replace("nbspa", "#");
				fullname = PubFunc.keyWord_reback(fullname);
				fullname = fullname.replace("quanjiao;hao", "；");
			}
			String state=(String)user_vo.get("state");
			UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
		
			/*if(!userbo.validatePassword(password))
            {
            	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
            }  			
			if(ConstantParamter.isEncPwd(this.getFrameconn()))
			{
				Des des=new Des();
				password=des.EncryPwdStr(password);
			}*/			
			RecordVo vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo.setString("fullname",fullname);
			//vo.setString("password",password);
			vo.setInt("state",Integer.parseInt(state));
			/**email,phone,org_dept在同一版本中增加*/
			if(vo.hasAttribute("email"))
			{
				vo.setString("email", (String)user_vo.get("email"));
				vo.setString("phone", (String)user_vo.get("phone"));
				vo.setString("org_dept", (String)user_vo.get("org_dept"));
			}			
			vo.setDate("modtime",DateStyle.getSystemTime());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.updateValueObject(vo);
			
			//同步更新流程定义t_wf_actor表里的actorname数据
			dao.update("update t_wf_actor set actorname=? where actor_type=4 and actorid=? ",Arrays.asList(fullname,username));
			
			
			
			//String interval=SystemConfig.getPropertyValue("account_logon_interval");
			String	 interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
			String   password_lock_days=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
			if(interval.length()>0||password_lock_days.length()>0){
				vo=dao.findByPrimaryKey(vo);
				String userflag = vo.getString("userflag");
				if("12".equals(userflag))
					this.getFormHM().put("flag","1");
				else
					this.getFormHM().put("flag","0");
				if(!"0".equals(state)){//解锁时清空登录账号错误锁定次数缓存
					if(interval.length()>0){
						com.hrms.hjsj.sys.SecurityLock.clearCounter(username);
					}
					ConstantParamter.setUserAttribute(username, "locked_login", "0");
					ArrayList values = new ArrayList();
			        ArrayList users = new ArrayList();
			        users.add(username);
			        values.add(users);
			        userbo.updatePWDModTime(values);
				}else{
					ConstantParamter.setUserAttribute(username, "locked_login", "1");	
				}
			}
		    this.getFormHM().put("message",ResourceFactory.getProperty("label.save.success"));			
		    this.getFormHM().put("state",state);
		    this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.operuser.update")+username);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
