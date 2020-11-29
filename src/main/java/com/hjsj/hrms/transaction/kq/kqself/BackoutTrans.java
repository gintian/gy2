package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 撤销申请
 * @author Owner
 * wangyao
 */
public class BackoutTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");     
		String table = (String) hm.get("table");
		String id=(String)hm.get("id");
		id = PubFunc.decrypt(id);
		StringBuffer sql=new StringBuffer();
		sql.append("update " + table);
		sql.append(" set " + table + "z5='01'");
		sql.append(" where " + table + "01='"+id+"'");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
