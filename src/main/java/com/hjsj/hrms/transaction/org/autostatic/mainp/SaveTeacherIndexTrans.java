package com.hjsj.hrms.transaction.org.autostatic.mainp;


import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveTeacherIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		String app = this.getFormHM().get("app").toString();
		String src = this.getFormHM().get("src").toString();
		String subset = this.getFormHM().get("subset").toString();
		
		String mess = "";
		ConstantXml c = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		if(!"#".equals(src)){			
			c.setAttributeValue("/param/teacher_items", "src", src);
		}
		if(!"#".equals(subset)){			
			c.setAttributeValue("/param/teacher_items", "subset", subset);
		}
		c.setAttributeValue("/param/teacher_items", "dest", app);
		c.saveStrValue();
		mess = "ok";
		this.getFormHM().put("mess", mess);
	}
	
}
