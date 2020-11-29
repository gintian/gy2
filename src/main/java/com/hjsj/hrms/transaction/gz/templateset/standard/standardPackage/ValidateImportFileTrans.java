package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

public class ValidateImportFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			FormFile form_file = (FormFile) getFormHM().get("file");
			/* 安全问题 文件上传 薪资标准-导入 xiaoyun 2014-9-16 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(form_file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题 文件上传 薪资标准-导入 xiaoyun 2014-9-16 end */
			
			ArrayList gzStandardPackageInfo=DownLoadXml.AnalyseImportStandard(form_file);
			this.getFormHM().put("gzStandardPackageInfo",gzStandardPackageInfo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	
	
	

}
