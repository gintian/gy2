package com.hjsj.hrms.module.hire.transaction;

import com.hjsj.hrms.module.hire.businessobject.SendResetPasswordMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/*没办法获取验证码改用servlet*/
@Deprecated
public class SendResetPasswordMailTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
		    String code = (String) this.formHM.get("codeValue");
		    String data = (String) this.formHM.get("data");
		    data = SafeCode.decode(PubFunc.decrypt(data));
		    String msg = (String) this.formHM.get("flag");
		    if(!"1".equalsIgnoreCase(msg) 
		    		&& (StringUtils.isEmpty(code)||StringUtils.isEmpty(data)||!code.trim().toUpperCase().equalsIgnoreCase(data))) {
		        this.formHM.put("flag", "false");
		        this.formHM.put("msg", "验证码错误！");
		        return;
		    }
		    
		    String emailName = (String) this.formHM.get("emailName");
		    if("1".equalsIgnoreCase(msg))
		        emailName = PubFunc.decrypt(emailName);
		    
			String requesturl = (String) this.formHM.get("url");
			SendResetPasswordMailBo bo = new SendResetPasswordMailBo(this.frameconn);
			String[] split = emailName.split("@");
			String add = split[1];
			String address = "";
			if((add.toLowerCase()).contains("qq.com")){
				address="https://mail.qq.com/cgi-bin/loginpage";
			}else if((add.toLowerCase()).contains("hjsoft.com")){
				address="http://exmail.qq.com/login";
			}else if((add.toLowerCase()).contains("163.com")){
				address="http://mail.163.com/";
			}else if((add.toLowerCase()).contains("sina.com")){
				address="http://mail.sina.com.cn/";
			}else if((add.toLowerCase()).contains("126.com")){
				address="http://www.126.com/";
			}
			String flag = bo.sendEmail(emailName,requesturl, "");
			this.formHM.put("flag", flag);
			this.formHM.put("address", address);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
