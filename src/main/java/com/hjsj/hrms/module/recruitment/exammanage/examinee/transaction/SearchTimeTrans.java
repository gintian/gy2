package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.mortbay.util.ajax.JSON;

import java.util.HashMap;


/**
 * <p>
 * Description:查询时间
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-12-16 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class SearchTimeTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
    		
    		HashMap hs = bo.generateHourMins();
			this.getFormHM().put("times", JSON.toString(hs));
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
