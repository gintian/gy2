package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 校验代码号交易类
 * @author wangb 2017-11-11
 *
 */
public class HasPosBusinessTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String codesetid = (String) this.formHM.get("codesetid");
		String codeitemid = (String) this.formHM.get("codeitemid");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String msg = "0";
		String sql = "select * from codeitem where codesetid=? and codeitemid=?";
		ArrayList list = new ArrayList();
		list.add(codesetid);
		list.add(codeitemid);
		try {
			this.frowset =dao.search(sql,list);
			if(this.frowset.next())
				msg = "1";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.formHM.put("msg", msg);
	}

}
