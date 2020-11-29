package com.hjsj.hrms.transaction.report.report_state;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowInfoBoardTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList titleList=new ArrayList();
		LazyDynaBean bean=null;
		bean=new LazyDynaBean();
		bean.set("value","");
		bean.set("name", "");
		titleList.add(bean);
		bean=new LazyDynaBean();
		bean.set("value","(~报表负责人~)");
		bean.set("name", "(~报表负责人~)");
		titleList.add(bean);
		bean=new LazyDynaBean();
		bean.set("value","(~系统时间~)");
		bean.set("name", "(~系统时间~)");
		titleList.add(bean);
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		this.getFormHM().put("titleList", titleList);
		this.getFormHM().put("content", "");
		this.getFormHM().put("info", "");
	}

}
