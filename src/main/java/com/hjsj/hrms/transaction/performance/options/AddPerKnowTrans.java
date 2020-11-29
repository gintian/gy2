package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddPerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {
		RecordVo votemp = (RecordVo) this.getFormHM().get("perknowvo");
		String name = votemp.getString("name");
		String knowId = votemp.getString("know_id");
		String status = votemp.getString("status"); 
		String info = (String) this.getFormHM().get("info");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {       

			RecordVo vo = new RecordVo("per_know");
			if ("save".equals(info)) { 
				 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
				 knowId=idg.getId("per_know.know_id");           
				vo.setString("know_id", knowId);
				vo.setString("name", name);
				vo.setString("status", status);
				vo.setString("seq", "" + this.getSeq());
				dao.addValueObject(vo);     
				this.getFormHM().put("info", "addend");
			} else {
				vo.setString("know_id", knowId);
				vo.setString("name", name);
				vo.setString("status", status);
				dao.updateValueObject(vo);
				this.getFormHM().put("info", "updateend");
				this.getFormHM().put("knowId", "");
			}

		} catch (Exception exx) {
			exx.printStackTrace();
			throw GeneralExceptionHandler.Handle(exx);
		} finally {
			// this.getFormHM().put("type",null);
			// this.getFormHM().put("typeid","");
		}

	}

	public synchronized int getKnowId() throws GeneralException {
		int num = 0; // 序号默认为0
		String sql = "select max(know_id) as num  from per_know";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				num = this.frowset.getInt("num");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return num + 1;
	}

	public synchronized int getSeq() throws GeneralException {
		int num = 0; // 序号默认为0
		String sql = "select max(seq) as num  from per_know";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				num = this.frowset.getInt("num");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return num + 1;
	}
}
