package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExecuteCurrentStandardExcelTrans extends IBusiness {

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
			
			GzExcelBo excelBo=new GzExcelBo(this.getFrameconn());
			String filename = excelBo.getSingleGzStandardExcel(vo,gzStandardName,1,this.userView.getUserName());
			/* 安全问题 文件下载 薪资标准-薪资标准表-编辑 xiaoyun 2014-9-16 start */
			filename = SafeCode.encode(PubFunc.encrypt(filename));
			/* 安全问题 文件下载 薪资标准-薪资标准表-编辑 xiaoyun 2014-9-16 end */
			this.getFormHM().put("filename", filename);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}

}
