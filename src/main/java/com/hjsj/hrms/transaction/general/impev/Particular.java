/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:Particular
 * </p>
 * <p>
 * Description:浏览重要信息报告内容
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class Particular extends IBusiness {

	/**
	 * 
	 */
	public Particular() {
		// TODO Auto-generated constructor stub
	}


	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String p0600 = (String) hm.get("p0600");
		p0600 = p0600 != null && p0600.trim().length() > 0 ? p0600 : "";
		String flag = (String) hm.get("flag");
		hm.remove("flag");
		if(!"isDecode".equals(flag)){
			p0600 = PubFunc.decrypt(p0600);
		}	
		hm.remove("p0600");
		String content = null;
		String sql = "select p0607 from p06 where p0600='" + p0600 + "'";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				content = this.frowset.getString("p0607");
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("content", content);
		}
	}
}
