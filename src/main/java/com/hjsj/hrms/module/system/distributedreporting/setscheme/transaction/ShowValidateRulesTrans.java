package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.drbean.DRValidateBean;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * @Description:展示校验规则主界面交易类
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:31:10 
 * @version: 1.0
 */
public class ShowValidateRulesTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			ArrayList list=new ArrayList();
			String sql = "select * from t_sys_asyn_validaterules where belong = 0 order by checkId desc";
			ContentDAO dao = new ContentDAO(frameconn);
			this.frowset = dao.search(sql);
			while(this.frowset.next()) {
				int checkId = this.frowset.getInt("checkId");
				String checkname = this.frowset.getString("checkname");
				String checkField = this.frowset.getString("checkField");
				String condition = this.frowset.getString("condition");
				int forcestateInt = this.frowset.getInt("forcestate");
				int validInt = this.frowset.getInt("valid");
				boolean forcestate = false;
				if (1==forcestateInt) {
					forcestate = true;
				}
				boolean valid = false;
				if (1==validInt) {
					valid = true;
				}
				list.add(new DRValidateBean(checkId, checkname, checkField, condition, forcestate, valid));
			}
			this.getFormHM().put("list",list);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
