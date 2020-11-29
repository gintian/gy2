package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:OutPutStandardTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 6, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class OutPutStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String standardIDs=(String)this.getFormHM().get("standard");
			String salaryid=(String)this.getFormHM().get("salaryid");
			//System.out.println(standardIDs);
			
			String outName="";
			if(standardIDs!=null&&standardIDs.length()>0)
				outName=DownLoadXml.outPutXmlInfo(this.getFrameconn(),standardIDs);
			if(salaryid!=null&&salaryid.length()>0)
			{
				ArrayList salaryIdList=new ArrayList();
				String[] temps=salaryid.split("#");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]!=null&&temps[i].length()>0)
						salaryIdList.add(temps[i]);
				}
				SalaryPkgBo bo=new SalaryPkgBo(this.getFrameconn(),this.getUserView(),0);
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
