package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取评委会信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetCommitteeTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类

			String b0110 = this.userView.getUnitIdByBusi("9");//取得所属单位
			
			ArrayList<HashMap<String, String>> committeeList = new ArrayList<HashMap<String, String>>();
			committeeList = committeeBo.getCommittee(b0110);//获取评委会
			
			this.getFormHM().put("committeeList", committeeList);
			
			this.getFormHM().put("addVersion", this.userView.hasTheFunction("380020202"));//新增聘委会权限
			this.getFormHM().put("editVersion", this.userView.hasTheFunction("380020209"));//编辑聘委会权限
			this.getFormHM().put("deleteVersion", this.userView.hasTheFunction("380020210"));//删除聘委会权限
			this.getFormHM().put("showHistoryVersion", this.userView.hasTheFunction("380020207"));//显示历史权限
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
