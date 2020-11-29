package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.deptperson;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.DeptPersonBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchDeptPersonDetailTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
			String a0100  =(String)this.getFormHM().get("a0100");
			String nbase  =(String)this.getFormHM().get("nbase");
			ArrayList list = new ArrayList();
			String fields = SystemConfig.getPropertyValue("personvisiblefield");
			if(a0100!=null&&!"".equals(a0100.trim())&&nbase!=null&&!"".equals(nbase.trim())&&fields!=null&&!"".equals(fields.trim())){
				DeptPersonBo bo = new DeptPersonBo(this.getFrameconn(),this.userView,nbase,a0100);
				list = bo.queryPersonDetails(fields);
			}
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("nbase", nbase);
			this.getFormHM().put("list", list);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
