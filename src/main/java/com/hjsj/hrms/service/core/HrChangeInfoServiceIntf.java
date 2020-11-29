package com.hjsj.hrms.service.core;

public interface HrChangeInfoServiceIntf {

	public String getChangeUsers (String changeFlag,String hjusername,String password);
	public String getWhereChangeUsers (String whereStr,String hjusername,String password);
	public String returnSynchroXml (String xml,String hjusername,String password);
	public String returnSynchroArray (String[] arrayString,String hjusername,String password);
}
