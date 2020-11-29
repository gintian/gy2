package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ExecuteMultipleStandardExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			ArrayList standardids=(ArrayList)this.getFormHM().get("standardids");
			
			SalaryStandardBo bo=new SalaryStandardBo(this.getFrameconn());
			bo.setUserView(userView);
			ArrayList standardlist=bo.getSalaryStandardList(pkg_id);
			LazyDynaBean bean=null;
			String ids=",";
			for(int i=0;i<standardlist.size();i++)
			{
				bean=(LazyDynaBean)standardlist.get(i);
				ids+=(String)bean.get("id")+",";
			}
			 
			GzExcelBo excelBo=new GzExcelBo(this.getFrameconn());
			String fileName = excelBo.getMultipleGzStandardExcel(pkg_id,standardids,this.userView.getUserName(),ids);//加个用户名  zhaoxg add 2013-10-11
			/* 安全问题 文件下载 薪资标准-薪资标准表-导出excel xiaoyun 2014-9-16 start */
			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			/* 安全问题 文件下载 薪资标准-薪资标准表-导出excel xiaoyun 2014-9-16 end */
			this.getFormHM().put("fileName", fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
