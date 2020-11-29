package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;


/**
 * <p>
 * Description:分派考场
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
public class DispatchHallTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		//增加座位分配规则---zhiyh20200409
    		String distrubuteRule = (String)this.getFormHM().get("distrubuteRule"); 
    		String hallIds = (String)this.getFormHM().get("hallIds");   
			String z0321s = (String)this.getFormHM().get("z0321s");   
			String z0357s = (String)this.getFormHM().get("z0357s");   
			String subjects = (String)this.getFormHM().get("subjects");
			String batchId = (String)this.getFormHM().get("batchId");
			
			if(StringUtils.isNotEmpty(hallIds) && hallIds.startsWith(","))
				hallIds = hallIds.substring(1);
			
			ExamHallBo hallBo = new ExamHallBo(this.frameconn,this.userView);
			hallBo.assignExamHall(hallIds, z0321s, z0357s, subjects, batchId,distrubuteRule);
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
