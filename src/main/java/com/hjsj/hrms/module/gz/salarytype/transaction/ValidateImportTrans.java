package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：ValidateImportTrans
 * 类描述：薪资类别导入验证
 * 创建人： lis
 * 创建时间：2015-11-28
 */
public class ValidateImportTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			// 上传组件 vfs改造
            String fileid = (String)this.getFormHM().get("fileid");
            
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.getUserView());
			ArrayList list=bo.getSalaryTemplateList(fileid,"salarytemplate.xml","salaryid","cname");
			this.getFormHM().put("salarySetList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
