package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * <p>
 * Description:验证批次中是否存在已生成准考证的考生
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
public class ClearHallRecordsTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
    		
    		String a0100s = (String)this.getFormHM().get("a0100s");   
			String nbases = (String)this.getFormHM().get("nbases");   
			String z0301s = (String)this.getFormHM().get("z0301s"); 
			
			bo.clearHallRecord(a0100s.split(","), nbases.split(","), z0301s.split(","));
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
