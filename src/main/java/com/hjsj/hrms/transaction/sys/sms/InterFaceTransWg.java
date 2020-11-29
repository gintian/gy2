package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.Sms_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

public class InterFaceTransWg extends IBusiness {


	public void execute() throws GeneralException 
	{
		String service = (String)this.getFormHM().get("service");
		
		service = service == null? "" : service;
						
		try
		{
			if(service==null || service.trim().length()<=0)
			{						
				RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
			    if (sms_vo == null) {
			    	service="SERVICE1";
			    } else {
				    String content = sms_vo.getString("str_value");		    			
					if (content == null || content.trim().length()<=0)
			    	{
						service="SERVICE1";
			    	} else
			    	{
			    		Document doc = PubFunc.generateDom(content);
			    		String xpath = "//gateway";
			    		XPath xpath_ = XPath.newInstance(xpath);
			    		Element ele = (Element) xpath_.selectSingleNode(doc);
			    		if (ele != null)
							service = ele.getAttributeValue("service");	    		
			    	}
			    }
			}
			
		    Sms_Parameter sparam=new Sms_Parameter(this.getFrameconn());			
		    LazyDynaBean lvo=sparam.searchGateway(service); 
		    if(lvo!=null)
		    {	
		    	String str = (String) lvo.get("service");
		    	this.getFormHM().put("service",lvo.get("service") == null ? "" : lvo.get("service"));
		    	this.getFormHM().put("userName",lvo.get("username"));
		    	this.getFormHM().put("password", lvo.get("password"));
		    	this.getFormHM().put("upUrl", lvo.get("up_url"));
		    	this.getFormHM().put("downUrl", lvo.get("down_url"));
		    	this.getFormHM().put("channelId", lvo.get("channelid"));
		    	this.getFormHM().put("qy", lvo.get("qy")); 
		    	this.getFormHM().put("spname", lvo.get("spname"));

		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	

	}

}
