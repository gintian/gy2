package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ModifyFileName extends IBusiness {

	
	public void execute() throws GeneralException {
		try {
			String filename = (String) this.getFormHM().get("filename");
			String fileid = (String) this.getFormHM().get("fileid");
			fileid = PubFunc.decrypt(SafeCode.decode(fileid));
			this.getFormHM().put("filename", "");
			String sql = "update tr_res_file set name='" + filename
					+ "' where fileid='" + fileid + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			dao.update(sql);
			
			this.getFormHM().put("isOk", "1");
			this.getFormHM().put("newName", filename);

			
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
