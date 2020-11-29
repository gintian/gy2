package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

/**
 * 
 *<p>Title:ValidateImportTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ValidateImportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			FormFile form_file = (FormFile) getFormHM().get("file");
			/* 文件上传 安全问题 薪资类别-导入 xiaoyun 2014-9-15 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(form_file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 文件上传 安全问题 薪资类别-导入 xiaoyun 2014-9-15 end */
			SalaryPkgBo bo=new SalaryPkgBo(this.getFrameconn(),this.getUserView(),0);
			ArrayList list=bo.getSalaryTemplateList(form_file,"salarytemplate.xml","salaryid","cname");
			this.getFormHM().put("salarySetList",list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
