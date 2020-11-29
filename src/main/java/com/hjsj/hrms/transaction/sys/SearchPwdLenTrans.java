package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchPwdLenTrans extends IBusiness {

	public void execute() throws GeneralException {
        /**登录参数表,登录用户指定不是username or userpassword*/
        String password=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        /**default值*/
        this.getFormHM().put("pwdlen","8");        
        if(!(login_vo==null))
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");
            if(!(idx==-1))
            {
                password=login_name.substring(idx+1);  
                if("#".equals(password)|| "".equals(password))
                	password="userpassword";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(password);
                	this.getFormHM().put("pwdlen",Integer.toString(item.getItemlength()));                	
                }
            }
        }
	}

}
