package com.hjsj.hrms.transaction.general.sys.validate;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SendMessageTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			/**得到登录用户的信息**/
			String a0101 = this.userView.getUserName();
			
    		/**得到该用户的移动电话*/
    		String phoneNumber = SafeCode.decode((String)this.getFormHM().get("phoneNumber"));
    		
    		String content = PubFunc.getStrg().toLowerCase();//生成6位不同的随机数。如果出错，其也用来存储错误信息
    		content = content.substring(content.length()-6);//只截取后6位
    		String content_desc ="您好，您正在使用ehr平台，您的验证码是："+content+"，请及时输入验证，谢谢!";
    		String error = "0";
	        
			/**向该用户发送手机短信*/
    		String sys_name = "系统";//发送人
    		if(SystemConfig.getPropertyValue("sys_name")!=null&&SystemConfig.getPropertyValue("sys_name").length()>0)
    			sys_name=SystemConfig.getPropertyValue("sys_name");
    		String clientName = "";
    		if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").length()>0)
    			clientName=SystemConfig.getPropertyValue("clientName");
    		
    		if(clientName!=null && "specialClient".equalsIgnoreCase(clientName)){//specialClient要替换掉     特定的短信平台发短信
    			Dtsms sms=(Dtsms)Class.forName("com.hjsj.hrms.transaction.general.sys.validate.SMSUtil").newInstance();
    			//Dtsms sms=new SMSUtil();
    			int response = sms.sendValidateMessage(content_desc, phoneNumber,this.userView,this.getFrameconn());
    			if(response!=0){
    				content = sms.getErrorReason();
    	        	this.getFormHM().put("content", content);
    	        	error = "1";
    	        	this.getFormHM().put("error", error);
    	        	return;
    			}
    			
    		}else{//用系统内部集成的短信接口发送短信
    			RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
    	        if(sms_vo==null){//没有配置短信接口参数
    	        	content = ResourceFactory.getProperty("sys.smsparam.nodifine");
    	        	this.getFormHM().put("content", content);
    	        	error = "1";
    	        	this.getFormHM().put("error", error);
    	        	return;
    	        }
    			
    			ArrayList destlist = new ArrayList();
        		LazyDynaBean dyvo=new LazyDynaBean();   
    			dyvo.set("sender",sys_name);
    			dyvo.set("receiver",a0101);
    			dyvo.set("phone_num",phoneNumber);
    			dyvo.set("msg",content_desc);
    			destlist.add(dyvo);
    			SmsBo smsbo=new SmsBo(this.getFrameconn());
    			if(!(destlist==null||destlist.size()==0)){
    				smsbo.batchSendMessage(destlist);
    			}
    		}
    		this.getFormHM().put("content", content);//把验证码发到前台
    		this.getFormHM().put("error", error);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
