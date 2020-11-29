package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		String id = (String)this.getFormHM().get("userCode");
		this.getFormHM().put("details", this.getDetailById(id));
	}
	
	//得到报批详细情况
	public String getDetailById(String id){
		String details = "";
		String sql = "select approcess from q35 where id = '"+id+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				details = this.frowset.getString("approcess") + " ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return details;
	}
}
