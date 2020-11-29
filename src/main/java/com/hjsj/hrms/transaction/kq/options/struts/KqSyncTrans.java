package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * <p>
 * Title:KqSyncTrans
 * </p>
 * <p>
 * Description:查询考勤同步配制
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-23
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class KqSyncTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		DocumentSyncBo bo = new DocumentSyncBo(this.frameconn);
		List list = bo.getConnStrList();
		this.getFormHM().put("connStrList", list);
	}
}
