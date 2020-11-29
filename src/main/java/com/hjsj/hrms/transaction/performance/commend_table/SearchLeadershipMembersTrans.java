package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchLeadershipMembersTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			CommendTableBo ctb = new CommendTableBo(this.getFrameconn(),this.getUserView(),1);
			ctb.createResultTable();
			ctb.getBeforeResult();
			ArrayList leaderShipList=ctb.getLeadershipMembers();
			this.getFormHM().put("leaderShipList", leaderShipList);
			this.getFormHM().put("isLeader", ctb.getIsLeader());
			this.getFormHM().put("questionOne", ctb.getQuestionOne());
			this.getFormHM().put("questionTwo", ctb.getQuestionTwo());
			this.getFormHM().put("questionFour", ctb.getQuestionFour());
			this.getFormHM().put("questionFive", ctb.getQuestionFive());
			this.getFormHM().put("status", ctb.getStatus());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
