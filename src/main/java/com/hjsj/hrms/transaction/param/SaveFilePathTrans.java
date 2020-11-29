package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;

public class SaveFilePathTrans extends IBusiness{

	public void execute() throws GeneralException {
		String fileRootPath = (String)this.getFormHM().get("fileRootPath");
		fileRootPath = PubFunc.hireKeyWord_filter_reback(fileRootPath);
		fileRootPath = fileRootPath.replace(" ", "");
		String multimedia_maxsize = (String)this.getFormHM().get("multimedia_maxsize");
		String asyn_maxsize = (String)this.getFormHM().get("asyn_maxsize");
		String doc_maxsize = (String)this.getFormHM().get("doc_maxsize");
		String videostreams_maxsize = (String)this.getFormHM().get("videostreams_maxsize");
		//【7923】系统管理/参数设置/系统参数：文件存放目录，linux环境时应该是/的斜杠，不应该是\斜杠，建议规则跟操作系统统一。  jingq upd 2015.03.10
		fileRootPath = fileRootPath.replace("\\", File.separator);
		// 没带单位时， 后台处理为M
		try{
			Double.parseDouble(multimedia_maxsize);
			multimedia_maxsize = multimedia_maxsize + "M";
		}catch (Exception e) {
		}
		try{
			Double.parseDouble(asyn_maxsize);
			asyn_maxsize = asyn_maxsize + "M";
		}catch (Exception e) {
		}
		try{
			Double.parseDouble(doc_maxsize);
			doc_maxsize = doc_maxsize + "M";
		}catch (Exception e) {
		}
		try{
			Double.parseDouble(videostreams_maxsize);
			videostreams_maxsize = videostreams_maxsize + "M";
		}catch (Exception e) {
		}		
		
		//44101 配置10M或10MB两种比较混乱，程序使用处理时比较麻烦容易出问题。此处规定统一为10M格式 guodd 2019-05-10
		multimedia_maxsize = multimedia_maxsize.replace("MB","M");
		asyn_maxsize = asyn_maxsize.replace("MB","M");
		doc_maxsize = doc_maxsize.replace("MB","M");
		videostreams_maxsize = videostreams_maxsize.replace("MB","M");
		
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM","filepath");
		constantXml.setAttributeValue("/filepath","rootpath",fileRootPath);
		constantXml.setAttributeValue("/filepath/multimedia","maxsize",multimedia_maxsize);
		constantXml.setAttributeValue("/filepath/asyn","maxsize",asyn_maxsize);
		constantXml.setAttributeValue("/filepath/document","maxsize",doc_maxsize);
		constantXml.setAttributeValue("/filepath/trainvideo","maxsize",videostreams_maxsize);
		constantXml.saveStrValue();
		//系统管理参数设置，文件存放目录，修改文件存放根目录保存后，页面显示的目录盘符不对  jingq upd 2014.10.21
		this.getFormHM().put("fileRootPath", fileRootPath);
	}

}
