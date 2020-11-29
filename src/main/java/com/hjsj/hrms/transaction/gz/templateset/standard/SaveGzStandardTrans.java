package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveGzStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			GzStandardItemVo vo=(GzStandardItemVo)this.getFormHM().get("gzStandardItemVo");
			String gzStandardName=(String)this.getFormHM().get("gzStandardName");
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			String opt=(String)this.getFormHM().get("opt");
			String standardID=(String)this.getFormHM().get("standardID");
			GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),this.userView);
			bo.saveSalaryStandard(vo,pkg_id,gzStandardName,opt,standardID);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
