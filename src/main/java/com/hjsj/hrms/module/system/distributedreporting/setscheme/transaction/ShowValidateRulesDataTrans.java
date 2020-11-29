package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @version: 1.0
 * @Description: 用于定义校验规则回显数据
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:48:56
 */
public class ShowValidateRulesDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			int  checkId = (Integer) this.getFormHM().get("checkId");
			String checkname = "";
			String checkField = "";
			String condition = "";
			int forcestate = 0;
			int valid =0;
			String sql = "select * from t_sys_asyn_validaterules where checkId = "+checkId;
			ContentDAO dao = new ContentDAO(frameconn);
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				checkname = this.frowset.getString("checkname");
				checkField = this.frowset.getString("checkField");
				condition = this.frowset.getString("condition");
				forcestate =this.frowset.getInt("forcestate");
				valid = this.frowset.getInt("valid");
			}
			this.getFormHM().put("checkId", checkId);
			this.getFormHM().put("checkname", checkname);
			this.getFormHM().put("checkField", checkField);
			this.getFormHM().put("condition", condition);
			this.getFormHM().put("forcestate", forcestate);
			this.getFormHM().put("valid", valid);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
