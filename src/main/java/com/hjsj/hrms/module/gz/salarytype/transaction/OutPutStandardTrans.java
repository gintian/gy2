package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 项目名称 ：ehr7.x
 * 类名称：OutPutStandardTrans
 * 类描述：导出薪资类别
 * 创建人： lis
 * 创建时间：2015-11-27
 */
public class OutPutStandardTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryids=(String)this.getFormHM().get("salaryids");
			
			String outName="";
			if(StringUtils.isNotBlank(salaryids))
			{
				ArrayList salaryIdList=new ArrayList();
				String[] temps=salaryids.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(StringUtils.isNotBlank(temps[i]))
						salaryIdList.add(PubFunc.decrypt(SafeCode.decode(temps[i])));
				}
				SalaryTypeBo bo=new SalaryTypeBo(getFrameconn(), userView);
				outName=bo.exportPkg(salaryIdList);
				
			}
			/* 安全问题 文件导出 参数设置-薪资类别-导出 xiaoyun 2014-9-15 start */
			outName = SafeCode.encode(PubFunc.encrypt(outName));
			/* 安全问题 文件导出 参数设置-薪资类别-导出 xiaoyun 2014-9-15 end */
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
