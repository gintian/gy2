package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveAnswerTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String status=(String)map.get("status");
			String questionOne=(String)this.getFormHM().get("questionOne");
			String questionTwo=(String)this.getFormHM().get("questionTwo");
			String questionThree=(String)this.getFormHM().get("questionThree");
			String questionFour=(String)this.getFormHM().get("questionFour");
			String questionFive=(String)this.getFormHM().get("questionFive");
			String isLeader=(String)this.getFormHM().get("isLeader");
			CommendTableBo ctb= new CommendTableBo(this.getFrameconn(),this.getUserView(),1);
			ctb.saveResult(questionOne, questionTwo, questionThree, questionFour, questionFive, Integer.parseInt(status), isLeader);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
