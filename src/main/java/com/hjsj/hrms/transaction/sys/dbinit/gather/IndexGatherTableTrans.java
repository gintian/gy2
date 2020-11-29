package com.hjsj.hrms.transaction.sys.dbinit.gather;

import com.hjsj.hrms.businessobject.sys.gathertable.GatherTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:采集表，展现所有</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 27, 2009:1:52:10 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndexGatherTableTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tablename = (String)this.getFormHM().get("tablename");
		GatherTableBo gather = new GatherTableBo(this.getFrameconn());
		ArrayList userlist = gather.userlist(tablename);
		this.getFormHM().put("indexlist",userlist);
	}
}
