package com.hjsj.hrms.businessobject.sys;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


public class AddDbSecurity extends IBusiness{


	@Override
    public void execute() throws GeneralException {
		try {
			ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"DB_SECURITY","root");
			String rsydid = constantXml.getTextValue("/root/rsydid");//人事异动
			String gjhmcid = constantXml.getTextValue("/root/gjhmcid");//高级花名册id
		    String salaryid = constantXml.getTextValue("/root/salaryid"); //薪资id
		    String cyhmcid = constantXml.getTextValue("/root/cyhmcid");//常用花名册id
		    String reportid = constantXml.getTextValue("/root/reportid");//报表id
		    String tablename = constantXml.getTextValue("/root/tablename");//表名
		    
		    
		    this.getFormHM().put("rsydid",rsydid);
		    this.getFormHM().put("gjhmcid",gjhmcid);
		    this.getFormHM().put("salaryid",salaryid);
		    this.getFormHM().put("cyhmcid",cyhmcid);
		    this.getFormHM().put("reportid",reportid);
		    this.getFormHM().put("tablename",tablename);
		} catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
			}
  
	}
}
