package com.hjsj.hrms.transaction.train.request;


import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SelectPreTrans extends IBusiness {

	public void execute() throws GeneralException {
		ConstantXml constantbo = new ConstantXml(this.frameconn,"TR_PARAM");
		 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		 if(tmpnbase == null || tmpnbase.length()<1)
			 throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！<br><br>请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
	}

}
