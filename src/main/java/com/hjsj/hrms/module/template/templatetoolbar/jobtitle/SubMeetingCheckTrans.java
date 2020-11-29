package  com.hjsj.hrms.module.template.templatetoolbar.jobtitle;

import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyPrepareBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SubMeetingCheckTrans.java</p>
 * <p>Description>:判断是否选中记录</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-15 上午10:22:58</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class SubMeetingCheckTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
	    try {
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String sysType = frontProperty.getSysType();
            String moduleId = frontProperty.getModuleId();
            String returnFlag = frontProperty.getReturnFlag();
            String tabId = frontProperty.getTabId();
            String taskId = frontProperty.getTaskId();            
            String inforType = frontProperty.getInforType();
            boolean bBatchApprove = frontProperty.isBatchApprove();
            TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
            TemplateParam paramBo = templateBo.getParamBo();
            String info ="";            
            TemplateApplyPrepareBo prepareBo=new TemplateApplyPrepareBo(this.frameconn,this.userView,paramBo,frontProperty); 
            //校验是否选中
            String validateFlag="1";           
            info =prepareBo.validateIsSelect("10");//校验是否选中
            this.getFormHM().put("info", info);
            this.getFormHM().put("validateFlag", validateFlag);
  
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
		
	}
	
	       
}
