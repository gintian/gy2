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
 * <p>create time:Jun 17, 2015 2:05:25 PM</p>
 * @author sunming
 * @version 1.0
 */
public class SearchCodeItemTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String itemid = (String)this.getFormHM().get("itemid");
		String codeitemid=itemid.split(":")[1];
		String codesetid = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
		if(!"B0110,E01A1".contains(codeitemid)){
			String sql = "select codesetid from fielditem where itemid='"+codeitemid+"'";
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				codesetid = this.frowset.getString("codesetid");
			}
		}else{
			if("B0110".equalsIgnoreCase(codeitemid))
				codesetid = "UN";
			else if("E01A1".equalsIgnoreCase(codeitemid))
				codesetid = "@K";
		}
			this.getFormHM().put("codesetid",codesetid);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
