package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * <p>
 * Description:批量修改考试时间
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-11-3 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class UpdateExamTimeTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
    		
			String batch_id = (String)this.getFormHM().get("batch_id");   
			String subId = (String)this.getFormHM().get("subId");   
			String examTime = (String)this.getFormHM().get("examTime");   
			String updateTextId = (String)this.getFormHM().get("updateTextId");  //替换成 
			String codeItemId = (String)this.getFormHM().get("codeItemId");   //代码项
			String a0100s = (String)this.getFormHM().get("a0100s");   //选中的人员a0100集合
			String nbases = (String)this.getFormHM().get("nbases");   //选中的人员库集合
			String z0301s = (String)this.getFormHM().get("z0301s");   //选中的人员职位编号集合
			
			bo.updateExamTime(batch_id,subId,examTime,updateTextId,codeItemId,a0100s,nbases,z0301s);
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
