package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.HashMap;

/**
 * <p>Title:ParamFileTrans.java</p>
 * <p>Description:上传参数指标说明文件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ParamFileTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String planId = (String) hm.get("plan_id");
	
			FormFile form_file = (FormFile) getFormHM().get("file");
			boolean flag = FileTypeUtil.isFileTypeEqual(form_file);
			if(!flag){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
	
			this.getFormHM().put("file", form_file);
			
			ExamPlanBo bo = new ExamPlanBo(this.frameconn);
			bo.saveThefile(planId, form_file,this.userView);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	}
	
}
