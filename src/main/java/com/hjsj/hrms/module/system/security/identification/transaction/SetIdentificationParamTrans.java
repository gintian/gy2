package com.hjsj.hrms.module.system.security.identification.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: SetIdentificationParamTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-7 下午4:03:39</p>
 * @author jingq
 * @version 1.0
 */
public class SetIdentificationParamTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String result = "ok";
		try{
			boolean mobile = (Boolean) this.getFormHM().get("mobile");
			boolean manual = (Boolean) this.getFormHM().get("manual");
			boolean sms = (Boolean) this.getFormHM().get("sms");
			
			ConstantXml constant = new ConstantXml(this.getFrameconn(),"SYS_LOGIN_SETTING");
			if("params".equals(constant.getRootNode().getName())){
				constant.setAttributeValue("/params/open_authentication", "mobile", mobile == true ? "1" : "0");
				constant.setTextValue("/params/authentication_type/manual", manual == true ? "1" : "0");
				constant.setTextValue("/params/authentication_type/sms", sms == true ? "1" : "0");
				constant.saveStrValue();
			} else {
				ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
				
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name", "open_authentication");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("mobile", mobile == true ? "1" : "0");
				bean.set("attributes", map);
				list.add(bean);
				constant.addElement2("/params/open_authentication", list);
				
				bean = new LazyDynaBean();
				list.clear();
				bean.set("name", "manual");
				bean.set("content", manual == true ? "1" : "0");
				list.add(bean);
				constant.addElement2("/params/authentication_type/manual", list);
				
				bean = new LazyDynaBean();
				list.clear();
				bean.set("name", "sms");
				bean.set("content", sms == true ? "1" : "0");
				list.add(bean);
				constant.addElement2("/params/authentication_type/sms", list);
				
				constant.saveValue("SYS_LOGIN_SETTING", "");
				constant.saveStrValue();
			}
		} catch (Exception e){
			result = e.getMessage();
			e.printStackTrace();
		}
		this.getFormHM().put("result", result);
	}

}
