package com.hjsj.hrms.transaction.train.b_plan;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:培训计划</p>
 * <p>Description:培训计划组织机构树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class OrgTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model = (String)hm.get("model");
		model = model!=null&&model.trim().length()>0?model:"1";
		hm.remove("model");
		String viewunit = "1";
		if(this.userView.getStatus()==4||this.userView.isSuper_admin()){
			viewunit="0";
		}
		
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		if(this.userView.getStatus()==0&&!this.userView.isSuper_admin()){
			String codeall = this.userView.getUnit_id();
			if(codeall==null||codeall.length()<3)
				viewunit="0";
		}
		this.getFormHM().put("model",model);
		this.getFormHM().put("viewunit",viewunit);
	}

}
