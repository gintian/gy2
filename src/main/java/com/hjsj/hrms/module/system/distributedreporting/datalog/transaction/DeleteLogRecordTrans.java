package com.hjsj.hrms.module.system.distributedreporting.datalog.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @version: 1.0
 * @Description: 删除日志记录表的记录
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:58:29
 */
public class DeleteLogRecordTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String ids = (String)this.getFormHM().get("ids");
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			if (null!=ids&&!"".equals(ids)){
				bo.deleteLogRecord(ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
