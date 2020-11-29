package com.hjsj.hrms.transaction.train.request;


import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:PermisViewTrans.java
 * </p>
 * <p>
 * Description:查询审核意见
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-09-03 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class PermisViewTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String type = (String) hm.get("type");
	String stuids = (String) hm.get("stuids");
	String classid = (String) hm.get("classid");
	String[] stuArray = stuids.split("@");

	TrainClassBo bo = new TrainClassBo(this.frameconn);
    if(!bo.checkClassPiv(classid, this.userView))
        throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
	
	this.getFormHM().put("classid", classid);
	this.getFormHM().put("type", type);
	this.getFormHM().put("info", ResourceFactory.getProperty("jx.khplan.review")+" "+stuArray.length+" "
				+ResourceFactory.getProperty("gz.tax.totalrecord")+"!");
	this.getFormHM().put("permisView", "");
    }
}
