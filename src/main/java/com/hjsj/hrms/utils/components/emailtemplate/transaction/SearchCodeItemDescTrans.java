package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;




/**
 * <p>Title:SearchCodeItemDescTrans</p>
 * <p>Description:查询所属机构</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 11, 2015 2:05:25 PM</p>
 * @author sunming
 * @version 1.0
 */
public class SearchCodeItemDescTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		String codeitemdesc = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				codeitemdesc = this.frowset.getString("codeitemdesc");
			}
			this.getFormHM().put("codeitemdesc",codeitemdesc);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
