package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.PassWordEncodeOrDecode;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * 口令解密
 * @author Owner
 *
 */
public class PassWordDecodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String item = (String)this.getFormHM().get("pass");
		String name = (String)this.getFormHM().get("name");
		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		String oldpwd = "";
		if(login_vo==null)
			oldpwd="UserPassword";
        String login_name = login_vo.getString("str_value");
        int idx=login_name.indexOf(",");
        if(idx==-1){
        	oldpwd="UserPassword";
        }
        oldpwd=login_name.substring(idx+1);
        if(oldpwd.length()==0||"#".equals(oldpwd))
        	oldpwd="UserPassword";
        if(!oldpwd.equalsIgnoreCase(item)){
        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.decodepwd.msg"))); 
        }
		PassWordEncodeOrDecode ped = new PassWordEncodeOrDecode(this.getFrameconn(),item,"2",name);
		String info = ped.exectue();
		ped.saveUserNamePassword();
		if("ok".equals(info)){
		    RecordVo vo=new RecordVo("constant");
		    vo.setString("constant","EncryPwd");        
		    vo.setString("str_value","0");
		    ContentDAO dao=new ContentDAO(this.frameconn);
            try {
				dao.updateValueObject(vo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	ConstantParamter.putConstantVo(vo,"EncryPwd");
		}
		this.getFormHM().put("info",info);
	}

}
