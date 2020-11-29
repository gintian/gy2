package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

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
 * <p>Description>:在团队绩效和我的目标中 导入目标卡</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 14, 2011 10:15:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ImportExcelTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try{
    		String plan_id = (String) this.getFormHM().get("planid");
    		FormFile form_file = (FormFile) getFormHM().get("file");
    		boolean flag = FileTypeUtil.isFileTypeEqual(form_file);
    		if(!flag){
    			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
    		}

    		ArrayList personList = (ArrayList) this.getFormHM().get("personList");
    		ObjectiveDecisionBo bo = new ObjectiveDecisionBo(this.getFrameconn(),this.userView,plan_id);
    		
    		ArrayList conctorList = bo.getKh_objectList(personList); //获取控制范围
    		
    		bo.importData(form_file,conctorList);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    
}
