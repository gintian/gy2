package com.hjsj.hrms.transaction.train.request;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:QueryTrainHourTrans.java</p>
 * <p>Description:标准培训学时</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-08-25 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class QueryTrainHourTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	TransDataBo bo = new TransDataBo(this.getFrameconn());	
	this.getFormHM().put("studyHour", bo.getStudyHour());
    }
}
