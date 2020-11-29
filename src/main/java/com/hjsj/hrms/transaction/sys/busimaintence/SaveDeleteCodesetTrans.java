package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.DeleteSubsysBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:业务字典删除子集</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2008:8:57:29 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveDeleteCodesetTrans extends IBusiness{

	public void execute() throws GeneralException {
		String set=(String)this.getFormHM().get("set");
		String userType = (String)this.getFormHM().get("userType");
		String obj=(String)this.getFormHM().get("id");
		DeleteSubsysBo sub  = new DeleteSubsysBo(this.getFrameconn());
		if(set!=null||!"".equals(set)){
			sub.deletbusitable(set, userType,obj);
		}
		
	}

}
