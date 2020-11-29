package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>
 * Title:DelTrainResourceTrans.java
 * </p>
 * <p>
 * Description:删除培训体系交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelTrainResourceTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String delStr = (String) hm.get("deletestr");	
	delStr = PubFunc.keyWord_reback(delStr);
	String type = (String) hm.get("type");
	
	TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);
	
	delStr = delStr.substring(0, delStr.length() - 1);
	String[] ids = delStr.split("/");
	
	bo.delete(ids);
    }

}
