package com.hjsj.hrms.transaction.dtgh.party;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DelPartySubInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selectedlist = (ArrayList)this.getFormHM().get("selectedlist");
		if(selectedlist==null){
			return;
		}
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			for(int i=0;i<selectedlist.size();i++){
				RecordVo vo = (RecordVo)selectedlist.get(i);
				dao.deleteValueObject(vo);
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
