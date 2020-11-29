package com.hjsj.hrms.module.template.templatetoolbar.batch.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:TemplateBatchCalcTrans.java</p>
 * <p>Description>:人事异动-批量计算</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-12-29 下午04:27:18</p>
 * <p>@version: 7.0</p>
 */
public class TemplateBatchCalcTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try
		{	
			TemplateFrontProperty frontProperty =new TemplateFrontProperty(this.getFormHM()); 			
			String tabId = frontProperty.getTabId();
			TemplateUtilBo utilBo=new TemplateUtilBo(this.getFrameconn(),this.userView);
			TemplateBo templateBo=new TemplateBo(this.getFrameconn(),
					this.userView,Integer.parseInt(tabId));
			templateBo.setModuleId(frontProperty.getModuleId());
			templateBo.setTaskId(frontProperty.getTaskId());
			String selfapply=(String)this.getFormHM().get("selfapply");			
			String[] taskids = frontProperty.getTaskId().split(","); 
            String ins_ids="";
            for(int i=0;i<taskids.length;i++){
               String ins_id =utilBo.getInsId(taskids[i]); 
               ins_ids=ins_ids+","+ins_id;
            }
            templateBo.setInsid(ins_ids.substring(1));
            templateBo.batchCompute(ins_ids.substring(1));

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
