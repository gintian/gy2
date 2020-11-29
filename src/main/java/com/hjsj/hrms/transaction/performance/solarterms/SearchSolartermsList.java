package com.hjsj.hrms.transaction.performance.solarterms;

import com.hjsj.hrms.businessobject.performance.solarterms.SolarTermsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class SearchSolartermsList extends IBusiness{

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
			
			String showType = (String)hm.get("showType");//展现方式  0：按任务  1：按月份
			hm.remove("showType");
			String frompage = (String)hm.get("frompage");//判断是从哪里进入的。0：从菜单。1：页面切换
			hm.remove("frompage");
			String year = "";//得到年份
			String depart = "";//得到用户部门
			String departdesc = "";//得到用户部门描述
			String currentdepart = this.userView.getUserDeptId();//因为下拉列表永远都是"当前登录用户"的父部门
			if("0".equals(frompage)){
				GregorianCalendar ca = new GregorianCalendar();
				year = ca.get(Calendar.YEAR)+"";//当前年份
				depart = this.userView.getUserDeptId();//得到当前用户的部门
				departdesc = AdminCode.getCodeName("UM",depart);
			}else if("1".equals(frompage)){
				year = (String)this.getFormHM().get("year");
				depart = (String)this.getFormHM().get("depart");
				departdesc = AdminCode.getCodeName("UM",depart);
			}
			
			
			ArrayList yearlist = new ArrayList();//年份列表
			ArrayList departoptionslist = new ArrayList();//部门下拉列表
			
			SolarTermsBo bo = new SolarTermsBo(this.userView,this.getFrameconn());
			ArrayList departlist = bo.getDepartList(currentdepart);//部门列表  做辅助用
			departoptionslist = bo.getOptionsList(departlist);//部门列表
			yearlist = bo.getYearList(year,departlist,departDutySet);//年列表
			
			String indexHtml = "";//主页
			if("0".equals(showType)){//如果按任务展示
				indexHtml = bo.getIndexHtmlByTask(year,depart,departDutySet);
			}else if("1".equals(showType)){//如果按时间维度展示
				indexHtml = bo.getIndexHtmlByTime(year,depart,departDutySet);
			}
			
			
			this.getFormHM().put("year", year);
			this.getFormHM().put("depart", depart);
			this.getFormHM().put("departdesc", departdesc);
			this.getFormHM().put("yearlist", yearlist);
			this.getFormHM().put("departoptionslist", departoptionslist);
			this.getFormHM().put("indexHtml", indexHtml);
			this.getFormHM().put("showType",showType);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
