package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * 项目名称 ：ehr7.x
 * 类名称：SaveAsSalaryTypeTrans
 * 类描述：薪资类别另存为
 * 创建人： lis
 * 创建时间：2015-11-17
 */
public class SaveAsSalaryTypeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String salarySetName=SafeCode.decode((String)this.getFormHM().get("name"));
			String gz_module=(String)this.getFormHM().get("gz_module");
			if(StringUtils.isBlank(gz_module))
				gz_module="0";
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
			bo.reSaveSalaryTemplate(salaryid,salarySetName,gz_module);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
