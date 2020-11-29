package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.PassWordEncodeOrDecode;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SysParamTrans extends IBusiness {

	public void execute() throws GeneralException {

		String module = (String)this.getFormHM().get("module");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(hm.containsKey("b_sys_param")){//查询
			hm.remove("b_sys_param");
			Map paramMap = SysParamBo.getSysParamValues(module);
			this.getFormHM().put("paramMap", paramMap);
			this.getFormHM().put("encryPwd", ConstantParamter.isEncPwd()?"1":"0");
			this.getFormHM().put("oldEncryPwd", ConstantParamter.isEncPwd()?"1":"0");
		}else if(hm.containsKey("b_save")){//保存
			hm.remove("b_save");
			Map paramMap = (Map)this.getFormHM().get("paramMap");
			SysParamBo.setSysParamValues(module, paramMap);
			//刷新登录类使用的缓存 已统一到sysparambo中全部刷新框架缓存
			/*String login_first_chang_pwd = (String)paramMap.get("login_first_chang_pwd");
			ConstantParamter.setAttribute("login_first_chang_pwd", login_first_chang_pwd);
			String account_logon_interval = (String)paramMap.get("account_logon_interval");
			ConstantParamter.setAttribute("account_logon_interval", account_logon_interval);
			String account_logon_failedcount = (String)paramMap.get("account_logon_failedcount");
			ConstantParamter.setAttribute("account_logon_failedcount", account_logon_failedcount);
			String only_logon_one = (String)paramMap.get("only_logon_one");
			ConstantParamter.setAttribute("only_logon_one", only_logon_one);
			String validatecode = (String)paramMap.get("validatecode");
			ConstantParamter.setAttribute("validatecode", validatecode);
			String validatecodelen = (String)paramMap.get("validatecodelen");
			ConstantParamter.setAttribute("validatecodelen", validatecodelen);*/
			
			
			
			//口令加密解密处理
			String encryPwd = (String)this.getFormHM().get("encryPwd");
			//String oldEncryPwd = (String)this.getFormHM().get("oldEncryPwd");
			//集群环境下缓存是不通的，不能使用缓存数据判断。从库里查询实时数据判断 guodd 2016-07-09
			RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.frameconn);
			if(encryVo==null){
				encryVo = new RecordVo("constant");
				encryVo.setString("constant","EncryPwd");
				encryVo.setString("str_value","0");
			}
			String oldEncryPwd = encryVo.getString("str_value");
			if(!oldEncryPwd.equals(encryPwd)){
				 String username="username";
			        String password="userpassword";
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		        if(login_vo!=null){
			        String login_name = login_vo.getString("str_value");
			        int idx=login_name.indexOf(",");
			        if(idx>3)
			        {
			        	username=login_name.substring(0,idx);
				        password=login_name.substring(idx+1);
			        }
			    }
		        if(username.length()<5)
		        	username = "username";
		        if(password.length()<5)
		        	password="userpassword";
				PassWordEncodeOrDecode ped = new PassWordEncodeOrDecode(this.getFrameconn(),password,"0".equals(encryPwd)?"2":encryPwd,username);
				String info = ped.exectue();
				if("ok".equals(info)){
				    //RecordVo vo=new RecordVo("constant");
				    encryVo.setString("constant","EncryPwd");        
				    encryVo.setString("str_value",encryPwd);
				    ContentDAO dao=new ContentDAO(this.frameconn);
		            try {
		            	ped.ifNoParameterInsert("EncryPwd");
						dao.updateValueObject(encryVo);
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        	ConstantParamter.putConstantVo(encryVo,"EncryPwd");
				}
			}
		}
	}

}
