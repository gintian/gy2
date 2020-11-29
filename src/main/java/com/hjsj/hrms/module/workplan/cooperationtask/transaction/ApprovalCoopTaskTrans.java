package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ApprovalCoopTaskTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		
		try {
			//获取审批类型
			int type = (Integer)this.getFormHM().get("type");// 1: 批准 2：驳回
			//获得选中的协办任务的id
			ArrayList<String> al = (ArrayList)this.getFormHM().get("selectedArr");
			CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
			//审批协作任务
			bo.approveCoopTask(type, al);
			//发送待办、邮件、微信
			bo.sendApprovedCoopTask(type, al);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
