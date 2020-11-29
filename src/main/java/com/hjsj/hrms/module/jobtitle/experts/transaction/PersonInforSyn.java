package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * <p>Title: PersonInforSyn </p>
 * <p>Description:人员信息同步 </p>
 * <p>Company: hjsj</p>
 * <p>create time  Jul 19, 2016 9:48:06 AM</p>
 * @author changxy
 * @version 1.0
 */
public class PersonInforSyn extends IBusiness{
	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ExpertsBo bo=new ExpertsBo(this.frameconn,this.userView);
		try {
			this.getFormHM().put("status", bo.getInformation());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
