package com.hjsj.hrms.transaction.general.sys.validate;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SecondValidateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			
			/**得到登录用户的信息**/
			String a0100 = this.userView.getA0100();
			String pre = this.userView.getDbname();
			if(pre==null||a0100==null||"".equalsIgnoreCase(pre)||"".equals(a0100)){
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("selfservice.module.pri")));
			}
			/**得到移动电话字段*/
			AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
    		String mobile_field =autoSendEMailBo.getMobileField();
    		if(mobile_field==null || "".equals(mobile_field)){//没有电话指标
    			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.sms.notdefine")));
    		}
    		
    		/**得到该用户的移动电话*/
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String phoneNumber = "";
    		StringBuffer sb = new StringBuffer();
    		sb.append("select "+mobile_field+" from "+pre+"a01 where a0100='"+a0100+"'");
    		this.frowset = dao.search(sb.toString());
    		if(frowset.next()){
    			phoneNumber = this.frowset.getString(mobile_field)==null?"":this.frowset.getString(mobile_field);
    		}
    		StringBuffer passPhone=new StringBuffer();
    		if(!("".equals(phoneNumber))){
    		    for(int i=0;i<phoneNumber.length();i++){
                    passPhone.append(phoneNumber.charAt(i));
    		    } 
    		}
    		
    		
    		this.getFormHM().put("phoneNumber", phoneNumber);
    		this.getFormHM().put("passPhone", passPhone.toString());

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
