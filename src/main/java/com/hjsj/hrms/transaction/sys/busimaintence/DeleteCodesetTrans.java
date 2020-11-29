package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.DeleteSubsysBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:删除子系统信息(只是把is_available改为0不可见,信息不删除)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 10, 2008:11:41:40 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleteCodesetTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String objid = (String)hm.get("obj"); 
		String userType = (String) this.getFormHM().get("userType");
		DeleteSubsysBo sub  = new DeleteSubsysBo(this.getFrameconn());
		ArrayList subsyslist = sub.getsubsys(objid);
		this.getFormHM().put("subsyslist", subsyslist);
		ArrayList busitablelist = sub.getbusitable(objid, userType);
		this.getFormHM().put("setname", "");
		this.getFormHM().put("busitablelist", busitablelist);
		this.getFormHM().put("id", objid);
	}
}
