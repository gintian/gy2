package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.deptperson;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.DeptPersonBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class LookDeptPersonsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String fromFlag=(String)this.getFormHM().get("fromFlag");//=1从部门人员进入,=2从部门人员进入处室人员,=3从处室人员进入
			DeptPersonBo bo = new DeptPersonBo(this.userView,this.getFrameconn());
			String e0122="";
			if("2".equals(fromFlag)){
				e0122=(String)this.getFormHM().get("e0122");
			}else{
				e0122=this.userView.getUserDeptId();
			}
			String htmlStr=bo.getDeptPersonsHtmlStr(fromFlag, e0122);
			this.getFormHM().put("htmlStr",htmlStr);
			this.getFormHM().put("fromFlag", fromFlag);
			this.getFormHM().put("e0122", e0122);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
