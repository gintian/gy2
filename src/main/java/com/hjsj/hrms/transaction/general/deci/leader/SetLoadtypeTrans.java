package com.hjsj.hrms.transaction.general.deci.leader;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SetLoadtypeTrans extends IBusiness {
	 public void execute() throws GeneralException {
		 //System.out.println(this.getFormHM().get("field_falg"));
		 ArrayList dblist=new ArrayList();
		 LazyDynaBean bean = new LazyDynaBean();
		 bean.set("name","默认结构");
		 bean.set("id","0");
		 dblist.add(bean);
		 bean = new LazyDynaBean();
		 bean.set("name","显示到部门");
		 bean.set("id","1");
		 dblist.add(bean);
		 bean = new LazyDynaBean();
		 bean.set("name","显示到集团");
		 bean.set("id","2");
		 dblist.add(bean);
		 String loadtype_sel = "0";
		 this.getFormHM().put("dbprelist",dblist);
		 this.getFormHM().put("loadtype_sel",loadtype_sel);
		 
	}
}
