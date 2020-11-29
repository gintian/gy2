package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckDragCodesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		String msg = "0";
		try{
			String fromid = (String)this.getFormHM().get("fromid");
			RecordVo vo = new RecordVo("codeset");
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setString("codesetid", fromid);
			vo = dao.findByPrimaryKey(vo);
			String status = vo.getString("status");
			msg = status==null||status.length()==0?"0":status;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.formHM.put("msg", msg);
		}
	}

}
