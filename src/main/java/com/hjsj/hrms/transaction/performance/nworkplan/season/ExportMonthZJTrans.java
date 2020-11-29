package com.hjsj.hrms.transaction.performance.nworkplan.season;
/*导出月度总结  团队不再导出了*/

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportMonthZJTrans extends IBusiness{

	public void execute() throws GeneralException {
		String outName = "";
		String year = (String)this.getFormHM().get("year");
		String season = (String)this.getFormHM().get("season");
		String startMonth = (String)this.getFormHM().get("startMonth");
		String endMonth  = (String)this.getFormHM().get("endMonth");
		String isdept = (String)this.getFormHM().get("isdept");
		String type = (String)this.getFormHM().get("type");
		if("2".equals(type)){//如果是年报
			startMonth = "1";
			endMonth = "12";
		}
		NewWorkPlanBo nwb = new NewWorkPlanBo(this.frameconn , this.userView);
		try {
			String a0100 = this.userView.getA0100();
			String nbase = this.userView.getDbname();
			String departid = this.userView.getUserDeptId();//部门
			NewWorkPlanBo nwp = new NewWorkPlanBo(this.getFrameconn(),this.userView);
			String parentid = nwp.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname());//父部门
			outName = nwb.creatExcel(year, season, startMonth ,endMonth ,isdept,a0100,nbase,departid,parentid,type);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
		
	}
	
}
