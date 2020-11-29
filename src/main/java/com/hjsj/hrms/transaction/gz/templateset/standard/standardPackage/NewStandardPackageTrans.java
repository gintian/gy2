package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:新建历史沿革</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 10, 2007:9:16:18 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class NewStandardPackageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			String[] newStandards=(String[])this.getFormHM().get("newStandards");
			String   packName=(String)this.getFormHM().get("packName");
			String   isStart=(String)this.getFormHM().get("isStart");
			String   startDate=(String)this.getFormHM().get("startDate");
			/* 薪资标准：新建薪资标准历史沿革时，清空右侧薪资标准表后，点击【保存】，系统报错 xiaoyun 2014-10-29 start */
			if(newStandards != null && newStandards.length > 0) {
				for (int i = 0; i < newStandards.length; i++) {
					newStandards[i] = PubFunc.keyWord_reback(newStandards[i]);
				}
			}
			this.getFormHM().put("newStandards", newStandards);
			/* 薪资标准：新建薪资标准历史沿革时，清空右侧薪资标准表后，点击【保存】，系统报错 xiaoyun 2014-10-29 end */
			if(newStandards.length==1&& "#".equals(newStandards[0]))
				newStandards=null;
			bo.saveStandardPackage(startDate.trim(),packName,isStart,newStandards);
		//	System.out.println("dddd");
			/* 新建薪资标准历史沿革a，勾选启用后，进行保存；再次新建薪资标准历史沿革b，不勾选启用，进行保存，在列表中看到b被启用了 xiaoyun 2014-10-20 start */
			this.getFormHM().put("isStart", "0");// 设置为默认不启用
			/* 新建薪资标准历史沿革a，勾选启用后，进行保存；再次新建薪资标准历史沿革b，不勾选启用，进行保存，在列表中看到b被启用了 xiaoyun 2014-10-20 end */

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
