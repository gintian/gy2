package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:调换班组先判断是否设置主集班组，设置同时更新</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 6, 2010:5:00:30 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class JudgeClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		String msg = "0";
		String shiftGroupItem = KqParam.getInstance().getShiftGroupItem();
		if(!"".equals(shiftGroupItem))
			msg="1";
		
		this.getFormHM().put("msg",msg);
	}
}
