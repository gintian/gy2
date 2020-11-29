package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SavePackStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			String[] newStandards=(String[])this.getFormHM().get("newStandards");
			/* 安全问题：参数设置/薪资标准：在修改页面，如上图，将右侧的薪资标准表全部移到左侧后，点击【保存】，系统报错 xiaoyun 2014-10-17 start */
			for (int i = 0; i < newStandards.length; i++) {
				newStandards[i] = PubFunc.keyWord_reback(newStandards[i]);
			}
			/* 安全问题：参数设置/薪资标准：在修改页面，如上图，将右侧的薪资标准表全部移到左侧后，点击【保存】，系统报错 xiaoyun 2014-10-17 end */
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			bo.updatePackageStandarList(pkg_id,newStandards);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
