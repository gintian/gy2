package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:修改检查指标名称</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 14, 2008:2:44:52 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class CheckupIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			String msg = "1";
			String indexname = (String) this.getFormHM().get("indexname");
			String itemid = (String) this.getFormHM().get("itemid");
			String setid = (String) this.getFormHM().get("setid");
			IndexBo subset = new IndexBo(this.getFrameconn());
			if (subset.checkupname(indexname, itemid, setid)) {
				msg = ResourceFactory.getProperty("kjg.error.clew");
			}
			this.getFormHM().put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
