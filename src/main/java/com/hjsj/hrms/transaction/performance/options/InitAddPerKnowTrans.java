package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/** 添加/编辑了解程度交易类 */
public class InitAddPerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {
//		String bodyId = (String) this.getFormHM().get("bodyId");
//		String knowId = (String) this.getFormHM().get("knowId");
//		String bodyType = (String) this.getFormHM().get("bodyType");
		String info = (String) this.getFormHM().get("info");

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String knowId=(String)hm.get("knowId");
		
		RecordVo vo = new RecordVo("per_know");		  
		ContentDAO dao = new ContentDAO(this.getFrameconn());
//		System.out.println("--"+knowId+"info:"+info);
		try { 

			if ("edit".equals(info)) {			
				StringBuffer strsql = new StringBuffer();
				strsql.append("select know_id,name,status,seq from per_know where know_id=");
				strsql.append(knowId);
				strsql.append(" order by  seq ");
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) {
					vo.setString("know_id", this.frowset.getString("know_id"));
					vo.setString("name", this.frowset.getString("name"));
					vo.setString("seq", this.frowset.getString("seq"));
					vo.setString("status", this.frowset.getString("status"));	
				}
				this.getFormHM().put("info", "editend");
			
//				vo.setString("know_id", "" + knowId);		
//				RecordVo vo2 = dao.findByPrimaryKey(vo);
//				this.getFormHM().put("name", vo2.getString("name"));
//				this.getFormHM().put("status", vo2.getString("status"));
//				this.getFormHM().put("bodyType", bodyType);
			} else {
//				this.getFormHM().put("bodyId", "");
//				this.getFormHM().put("name", "");
//				this.getFormHM().put("status", "0");
				vo.setString("know_id", "");
				vo.setString("name","");
				vo.setString("seq", "");
				vo.setString("status","1");	
			}
//			this.getFormHM().put("knowId", knowId);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally
        {
            this.getFormHM().put("perknowvo",vo);
        }
	}
}
