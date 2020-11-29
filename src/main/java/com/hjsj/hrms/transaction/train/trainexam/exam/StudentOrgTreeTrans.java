package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:培训考试人员</p>
 * <p>Description:机构树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-11-26 下午15:43:00</p>
 * @author zxj
 * @version 1.0
 */
public class StudentOrgTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model = (String)hm.get("model");
		String planId = (String)hm.get("planid");
		planId = PubFunc.decrypt(SafeCode.decode(planId));
		model = model!=null&&model.trim().length()>0?model:"1";
		hm.remove("model");
		String viewunit = "1";
		if(this.userView.getStatus()==4||this.userView.isSuper_admin()){
			viewunit="0";
		}
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		if(this.userView.getStatus()==0&&!this.userView.isSuper_admin()){
			String codeall = this.userView.getUnit_id();
			if(codeall==null||codeall.length()<2)
				viewunit="0";
		}
		this.getFormHM().put("model",model);
		this.getFormHM().put("viewunit",viewunit);
		this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(planId)));
	}

}
