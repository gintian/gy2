package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class InitStandardPackageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
			LazyDynaBean abean=(LazyDynaBean)list.get(0);
			String pkg_id=(String)abean.get("pkg_id");
		//	System.out.println(pkg_id);
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			ArrayList standardList=bo.getStandardList(pkg_id,2);
			ArrayList currentStandardList=bo.getStandardList(pkg_id,1);
			
			this.getFormHM().put("pkg_id",pkg_id);
			this.getFormHM().put("standardList",standardList);
			this.getFormHM().put("currentStandardList",currentStandardList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
