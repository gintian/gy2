package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * check只能拖动统计项
 * @author Luckstar
 *
 */
public class CheckDragStaticTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fromid =  (String)this.getFormHM().get("fromid");
		String tablename = (String)this.getFormHM().get("tablename");
		tablename=tablename==null||tablename.length()==0?"sname":tablename;
		String msg = "ok";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("select id from "+tablename+" where id="+fromid);
			this.frowset = dao.search(sql.toString());
			if(!this.frowset.next()){
				msg="error";
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.formHM.put("msg", msg);
		}
	}

}
