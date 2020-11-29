package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveDecision;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveDecisionBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

/**
 * <p>Title:ImportExcelTrans.java</p>
 * <p>Description>:目标卡制定 导入目标</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 14, 2011 10:15:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ImportExcelTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try
		{
			String plan_id = (String) this.getFormHM().get("plan_id");
			FormFile form_file = (FormFile) getFormHM().get("file");
			ArrayList personList = (ArrayList) this.getFormHM().get("personList");
			ObjectiveDecisionBo bo = new ObjectiveDecisionBo(this.getFrameconn(),this.userView,plan_id);
			
			ArrayList conctorList = bo.getKh_objectList(personList); //获取控制范围
			if(!FileTypeUtil.isFileTypeEqual(form_file)) 
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			} 
			bo.importData(form_file,conctorList);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    
}
