package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.options.OrganizationPopedomAnalyse;
import com.hjsj.hrms.businessobject.sys.options.UserPopedom;
import com.hjsj.hrms.businessobject.sys.options.UserPopedomAnalyse;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class UserPopedomTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String operatorFlag = (String)hm.get("operatorflag");
		UserPopedom up = null;
		//获取请求中btnBack参数  是否显示返回按钮   wangb  20171204 
		String btnBack = (String) hm.get("btnBack");
		if(btnBack !=null && "1".equals(btnBack)){
			this.formHM.put("btnBack", "1");
			hm.remove("btnBack");//移除btnBack 参数
		}else{
			this.formHM.put("btnBack", "0");
		}	
		EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
		
		if("1".equals(operatorFlag)){//个人
			String modeFlag = (String)hm.get("modeflag"); //用户标识(自助/业务)1,2
			String dbPre = (String)hm.get("dbpre");
			String userName=(String)this.getFormHM().get("name");
			//xuj add 2014-3-31  当数据库中登录用户名字段有等于空字符串的记录查看未分配账号的用户时权限明细串问题
			userName = userName==null||userName.length()==0?"	":userName;
			
			UserPopedomAnalyse upa = new UserPopedomAnalyse(this.getFrameconn(),modeFlag, dbPre,userName);
			//zxj changed 20140211 一些外挂模块权限需要加锁控制
			upa.setLock(lockclient);
			up = upa.execute();
			this.getFormHM().put("flag","show");//显示角色类
			
		}else if("2".equals(operatorFlag)){//组织
			//role_id   组织ID
			//role_flag 类别( 1 角色 0 用户组 2 单位,部门,职位)   
			//用户组时传递的是组的描述
			String role_flag = (String)hm.get("role_flag");
			String role_id=(String)this.getFormHM().get("role_id");
			
			OrganizationPopedomAnalyse op = new OrganizationPopedomAnalyse(this.getFrameconn(), role_id ,  role_flag);
			//zxj changed 20140211 一些外挂模块权限需要加锁控制
			op.setLock(lockclient);
			up = op.execute();
			this.getFormHM().put("flag","hidden"); //隐藏角色类
		}
		this.getFormHM().put("up",up);
	}

}
