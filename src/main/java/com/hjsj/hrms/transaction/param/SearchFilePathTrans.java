package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchFilePathTrans extends IBusiness{

	public void execute() throws GeneralException {
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM");
        String fileRootPath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
        String multimedia_maxsize = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
		String asyn_maxsize = constantXml.getNodeAttributeValue("/filepath/asyn", "maxsize");
        String doc_maxsize = constantXml.getNodeAttributeValue("/filepath/document", "maxsize");
        String videostreams_maxsize = constantXml.getNodeAttributeValue("/filepath/trainvideo", "maxsize");
		this.getFormHM().put("fileRootPath",fileRootPath);
		this.getFormHM().put("multimedia_maxsize",multimedia_maxsize);
		this.getFormHM().put("asyn_maxsize",asyn_maxsize);
		this.getFormHM().put("doc_maxsize",doc_maxsize);
		this.getFormHM().put("videostreams_maxsize",videostreams_maxsize);
	}
}
