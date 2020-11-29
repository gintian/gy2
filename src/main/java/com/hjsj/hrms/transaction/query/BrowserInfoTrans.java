/**
 * 
 */
package com.hjsj.hrms.transaction.query;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:BrowserInfoTrans</p>
 * <p>Description:取得对应信息的主键序号</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-3-1:17:43:44</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class BrowserInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String keyid=(String)hm.get("keyid");
		this.getFormHM().put("keyid",keyid);
	}

}
