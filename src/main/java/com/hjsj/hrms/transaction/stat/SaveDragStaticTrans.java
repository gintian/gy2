package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * @author xuj
 *	保存统计项拖动
 */
public class SaveDragStaticTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fromid = (String)this.getFormHM().get("fromid");
		String toid = (String)this.getFormHM().get("toid");
		String tablename = (String)this.getFormHM().get("tablename");
		tablename=tablename==null||tablename.length()==0?"sname":tablename;
		if("root".equals(toid)){
			toid = "";
		}
		String msg = "ok";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("update "+tablename+" set categories='"+toid+"' where id="+fromid);
			if(dao.update(sql.toString())<1){
				msg="error";
			}
		}catch(Exception e){
			e.printStackTrace();
			msg="error";
		}finally{
			this.formHM.put("msg", msg);
		}
		
	}

}
