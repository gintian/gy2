package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ChangeCodeSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList txrecordList = new ArrayList();
		String codeSetId = (String) this.getFormHM().get("setid");
		if (codeSetId == null || "".equals(codeSetId)) {
			CommonData dataobj = new CommonData();
			dataobj = new CommonData("", "");
			txrecordList.add(dataobj);
		} else {
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			String sqlstr = "select codeitemid,codeitemdesc from codeitem where codesetid='"+codeSetId+"'";
			CommonData dataobj = new CommonData();
			dataobj = new CommonData("", "");
			txrecordList.add(dataobj);
			try {
				this.frowset = dao.search(sqlstr);
				while (this.frowset.next()) {
					dataobj = new CommonData(this.frowset.getString("codeitemid"),
							this.frowset.getString("codeitemid")+" "+this.frowset.getString("codeitemdesc"));
					txrecordList.add(dataobj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("txrecordList", txrecordList);
	}
}
