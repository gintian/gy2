package com.hjsj.hrms.transaction.sys.dbinit.gather;

import com.hjsj.hrms.businessobject.sys.gathertable.GatherTableBo;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:指标解释展现</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 27, 2009:2:54:57 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndexExplainTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
		GatherTableBo gather = new GatherTableBo(this.getFrameconn());
		ArrayList indexlist = gather.indexlist(lockclient,this.userView);
		this.getFormHM().put("indexlist", indexlist);
	}

}
