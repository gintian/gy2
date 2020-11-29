package com.hjsj.hrms.transaction.performance.solarterms;

import com.hjsj.hrms.businessobject.performance.solarterms.SolarTermsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.HashMap;

public class ViewTask extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String clientName = SystemConfig.getPropertyValue("clientName");
			String departDutySet = "BA5";// 部门职责子集
			if(clientName==null || !"gw".equalsIgnoreCase(clientName)){//如果不是国网
				ContentDAO dao = new ContentDAO(this.frameconn);
			    this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			    if ( this.frowset.next())
			    {
			    	String str_value = this.frowset.getString("str_value");
			    	if (str_value == null || (str_value != null && "".equals(str_value))){
				    }else{
						Document doc = PubFunc.generateDom(str_value);
					    String xpath = "//Per_Parameters";
					    XPath xpath_ = XPath.newInstance(xpath);
					    Element ele = (Element) xpath_.selectSingleNode(doc);
					    Element child;
					    if (ele != null)
					    {
							child = ele.getChild("TargetDeptDuty");
							if (child != null){
								departDutySet = child.getAttributeValue("SubSet");
							}
					    }
				    }
			    }
			    if("".equals(departDutySet)){
			    	throw GeneralExceptionHandler.Handle(new Exception("请维护部门职责子集!"));
			    }
			}
			
			String showType = (String)hm.get("showType");//0:展示任务维度中的具体内容  1：展示时间维度中某月份的内容
			String depart = (String)hm.get("depart");
			String times = (String)hm.get("times");
			String year = (String)hm.get("year");
			String month = (String)hm.get("month");
			String widthofscreen=(String)hm.get("widthofscreen");
			hm.remove("showType");
			hm.remove("depart");
			hm.remove("times");
			hm.remove("year");
			hm.remove("month");
			hm.remove("widthofscreen");
			int screenwidth = Integer.parseInt(widthofscreen);
			int eachtdwidth = screenwidth/7;
			String taskHtml = "";
			SolarTermsBo bo = new SolarTermsBo(this.userView,this.getFrameconn());
			if("0".equals(showType))
				taskHtml = bo.getTaskHtmlByTask(depart,times,year,departDutySet,eachtdwidth);
			else if("1".equals(showType))
				taskHtml = bo.getTaskHtmlByTime(depart,year,month,departDutySet);
			
			this.getFormHM().put("taskHtml", taskHtml);
			this.getFormHM().put("showType", showType);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
