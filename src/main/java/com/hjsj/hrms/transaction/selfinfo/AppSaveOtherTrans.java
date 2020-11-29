/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AppSaveOtherTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		MyselfDataApprove self = new MyselfDataApprove(this.frameconn,
				this.userView);
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String save = (String) map.get("b_save");
		String setname = (String) map.get("setname");
		String userbase = (String) this.getFormHM().get("userbase");
		String a0100 = (String) this.getFormHM().get("a0100");
		List infofieldlist = (List)this.getFormHM().get("infofieldlist");
		String keyvalue = (String) map.get("keyvalue");
		String type = (String) map.get("type");
		String sequence = (String) map.get("sequence");
		String isDraft = (String) map.get("isDraft");
		
		String chg_id = self.getChgid(userbase, a0100, "01");
		if (chg_id == null || chg_id.length() <= 0) {
			chg_id = self.getChgid(userbase, a0100, "07");
		}
		if("save".equals(save))
			self.eidtMyselfDataOther(chg_id, setname, keyvalue, type, sequence, "", userbase, infofieldlist);
		if("approve".equals(save))
			self.eidtMyselfDataOther(chg_id, setname, keyvalue, type, sequence, "02", userbase, infofieldlist);
		this.getFormHM().put("infofieldlist", infofieldlist);
		
		
		
    }
}
