package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class AddKqRuleDataTrans  extends IBusiness {

	public void execute() throws GeneralException 
	{
		// 获取要修改的id
		HashMap hp = (HashMap) this.getFormHM().get("requestPamaHM");
		String op = (String) hp.get("op");

		if ("add".equalsIgnoreCase(op)) {
			String tran_flag = (String) this.getFormHM().get("tran_flag");
			if (tran_flag == null || tran_flag.length() <= 0) {
				tran_flag = "1";
			}
			if ("1".equals(tran_flag)) {
				this.getFormHM().put("rule_name", "");
			}
		}
		if ("edit".equalsIgnoreCase(op)) {

			String id = (String) hp.get("rule_id");
			this.getFormHM().put("rule_name", this.getNamebyId(id));
		}
	}
	
	/**
	 * 根據id 查詢 name
	 * 
	 * @param id
	 * @return
	 */
	private String getNamebyId(String id){

		StringBuffer str = new StringBuffer();
		str.append("select rule_name from kq_data_rule where rule_id=");
		str.append(id);

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(str.toString());
			if (this.frowset.next()) {
				return this.frowset.getString("rule_name");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
