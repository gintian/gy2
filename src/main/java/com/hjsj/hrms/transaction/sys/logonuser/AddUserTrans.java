/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Title:AddUserTrans</p>
 * <p>Description:增加用户交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-7:17:05:35</p>
 * @author chenmengqing
 * @version 1.0
 */
public class AddUserTrans extends IBusiness {


	public void execute() throws GeneralException {
		String currname=(String)this.getFormHM().get("currname");
		String flag=(String)this.getFormHM().get("flag");
		if(flag==null|| "".equals(flag))
			flag="1";
		
		try
		{
			DynaBean user_vo=(LazyDynaBean)this.getFormHM().get("user_vo");
			String username=(String)user_vo.get("username");
			String password=SafeCode.decode((String)user_vo.get("password"));

			String pwdok=SafeCode.decode((String)user_vo.get("pwdok"));
			String fullname=(String)user_vo.get("fullname");
			//处理特殊字符  jingq  add  2014.6.3
			fullname = SafeCode.decode(fullname);
			if(fullname!=null&&fullname.length()>0){
				fullname = fullname.replace("nbspa", "#");
				fullname = PubFunc.keyWord_reback(fullname);
				fullname = fullname.replace("quanjiao;hao", "；");
			}
			String state=(String)user_vo.get("state");
			if(StringUtils.isNumeric((username.substring(0,1))))
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.user.number")));
			if("-".equalsIgnoreCase(username.substring(0,1)))
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.user.number")));
			
			if(!pwdok.equalsIgnoreCase(password))
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("errors.sys.newpassword")));
			if(username.indexOf(" ")!=-1)
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("errors.sys.notspaces")));
			UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
            /*if(!userbo.validatePassword(password))
            {
            	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
            }*/ 
			//现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
			userbo.validatePasswordNew(password);
			
			if(ConstantParamter.isEncPwd(this.getFrameconn()))
			{
				Des des=new Des();
				password=des.EncryPwdStr(password);
			}			

			RecordVo vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo.setInt("roleid",0);
			vo.setString("fullname",fullname);
			vo.setString("password",password);
			/**email,phone,org_dept在同一版本中增加*/
			if(vo.hasAttribute("email"))
			{
				vo.setString("email", (String)user_vo.get("email"));
				vo.setString("phone", (String)user_vo.get("phone"));
				vo.setString("org_dept", (String)user_vo.get("org_dept"));
			}
			vo.setDate("modtime",DateStyle.getSystemTime());
			//将全角参数转成半角  hej update   2015/10/28
			currname = SafeCode.keyWord_reback(currname);
			int groupid=userbo.getCurrentGroupId(currname);
			if(groupid==1)
			{
				vo.setInt("userflag",10);
				flag="0";
			}
			else
			{
				if("1".equals(flag))//一般用户
				{
					vo.setInt("userflag",12);
					vo.setInt("photoid",2);
				}
				else//管理员
				{
					vo.setInt("userflag",11);
					vo.setInt("photoid",1);
				}
			}
			/**超级用户*/
			if(groupid==1)
				vo.setInt("photoid",0);
			vo.setInt("groupid",groupid);
			vo.setInt("state",Integer.parseInt(state));
			userbo.add_User(vo,true);
			if(!"0".equals(state)){//刷新登录类缓存
				ConstantParamter.setUserAttribute(username, "locked_login", "0");
			}else{
				ConstantParamter.setUserAttribute(username, "locked_login", "1");	
			}

			this.getFormHM().put("groupname",username);
			this.getFormHM().put("groupid",vo.getString("groupid"));	
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("state", state);
			this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.operuser.add")+username);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
