package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:SaveRangTargetTrans.java</p>
 * <p>Description>:保存排名指标</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 11, 2011 10:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SaveRangTargetTrans extends IBusiness
{
	public void execute() throws GeneralException
	{		
		String grpMenu1=(String)this.getFormHM().get("grpMenu1");
		String grpMenu2=(String)this.getFormHM().get("grpMenu2");
		grpMenu1 = PubFunc.keyWord_reback(grpMenu1);
		grpMenu2 = PubFunc.keyWord_reback(grpMenu2);		
		
		String enabled="false";
		if(((grpMenu1==null || grpMenu1.length()<=0) && (grpMenu2==null || grpMenu2.length()<=0)) ||(("[对象类别]".equalsIgnoreCase(grpMenu1)) && (grpMenu2==null || grpMenu2.length()<=0)))
			enabled="false";
		else
			enabled="true";
		
		String planid = (String) this.getFormHM().get("planid");
		LoadXml loadXml = new LoadXml(this.getFrameconn(),planid);
		
		
		ArrayList list = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("Enabled", enabled);
		bean.set("GrpMenu1", grpMenu1);
		bean.set("GrpMenu2", grpMenu2);
		list.add(bean);
		ArrayList idlist = new ArrayList();
		idlist.add("Enabled");
		idlist.add("GrpMenu1");
		idlist.add("GrpMenu2");
		loadXml.saveRelatePlanValue("CustomOrderGrp", idlist, list);
				
	}
}
