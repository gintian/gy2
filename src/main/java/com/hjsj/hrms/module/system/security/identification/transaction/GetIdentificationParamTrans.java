package com.hjsj.hrms.module.system.security.identification.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.mortbay.util.ajax.JSON;

import java.util.HashMap;

/**
 * <p>Title: GetIdentificationParamTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-7 上午10:41:02</p>
 * @author jingq
 * @version 1.0
 */
public class GetIdentificationParamTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		String mobile = "",manual = "",sms = "";
		try{
			ConstantXml constant = new ConstantXml(this.getFrameconn(), "SYS_LOGIN_SETTING");
			mobile = constant.getNodeAttributeValue("/params/open_authentication", "mobile");
			manual = constant.getTextValue("/params/authentication_type/manual");
			sms = constant.getTextValue("/params/authentication_type/sms");
			map.put("mobile", "1".equals(mobile)?true:false);
			map.put("manual", "1".equals(manual)?true:false);
			map.put("sms", "1".equals(sms)?true:false);
		} catch (Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("result", JSON.toString(map));
	}
	
}
