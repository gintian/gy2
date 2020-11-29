package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SelectProcessTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = null;
		RowSet rs = null;
		ArrayList<CommonData> list = new ArrayList<CommonData>();
		try {
			dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer("select codeitemid,codeitemdesc from codeitem where codesetid = '36' and codeitemid=parentid and invalid='1' order by a0000,codeitemid");
			rs = dao.search(sql.toString());
			while(rs.next()){
				if("04".equals(rs.getString("codeitemid"))||"09".equals(rs.getString("codeitemid")))
					continue;
				CommonData com = new CommonData(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
				list.add(com);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.formHM.put("processlist", list);
			PubFunc.closeResource(rs);
		}
	}

}
