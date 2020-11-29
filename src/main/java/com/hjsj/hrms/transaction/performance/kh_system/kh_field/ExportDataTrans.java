package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ExportDataTrans.java</p>
 * <p>Description>:导出考核指标文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-8-5 上午11:11:15</p>
 * <p>@version: 4.0</p>
 * <p>@author: JinChunhai
 */

public class ExportDataTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			String outName="";
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String export_type=(String)this.getFormHM().get("export_type");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn(),this.userView);
			if("all".equalsIgnoreCase(export_type))
			{
		    	outName = bo.exportData(subsys_id,export_type,"");
			}
			else
			{
				String pointsetid=(String)this.getFormHM().get("pointsetid");
				outName = bo.exportData(subsys_id,export_type,pointsetid);
			}
			
			// 没有可以到处的指标，应给出提示 lium
			if ("1".equals(outName)) {
				formHM.put("error", "true");
				formHM.put("errorInfo", "该分类下没有指标");
			} else {
				outName=SafeCode.encode(PubFunc.encrypt(outName));
				
				this.getFormHM().put("outName",outName);
				formHM.put("error", "");
				formHM.put("errorInfo", "");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
