package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SetAppFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SetAppFileTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		ArrayList applist =new ArrayList();
		ArrayList list =new ArrayList();
		applist = DataDictionary.getFieldList("Z07",Constant.ALL_FIELD_SET);
		for(int i=0;i<applist.size();i++){
			FieldItem fs = (FieldItem)applist.get(i);
			if("Z0700".equalsIgnoreCase(fs.getItemid()))
				continue;
			CommonData appdate =new CommonData();
			if("z0701".equalsIgnoreCase(fs.getItemid()))
				appdate.setDataName("岗位名称");
			else
				appdate.setDataName(fs.getItemdesc());
			appdate.setDataValue(fs.getItemid());
			list.add(appdate);
		}
		this.getFormHM().put("rnamelist",list);
		EngageParamXML engageParamXML=new EngageParamXML(this.getFrameconn());	
		String app_view=engageParamXML.getTextValue(EngageParamXML.APP_VIEW);	
		if(app_view==null||app_view.length()<=0)
			app_view="";
		EngageParam ep = new EngageParam(this.frameconn);
		this.getFormHM().put("selectrname",ep.getAppList(app_view));
	}
   
}
