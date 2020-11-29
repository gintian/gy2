package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteSeasonTrans extends IBusiness{

	public void execute() throws GeneralException {
		String p0100 = (String)this.getFormHM().get("p0100");
		String fileId = (String)this.getFormHM().get("file_id");
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn,this.userView);
		String isok = "0";
		//先删除per_diary_file表
		if(bo.deleteFileInfo(p0100,fileId)){
			isok = "1";
		}else{
			isok = "0";
		}
		if("1".equals(isok)){
			//再判断per_diary_file表中是否还有p0100=p0100的数据。如果没有，才能删除p01中的数据
			if(!bo.isHaveP0100(p0100)){
				//再删除p01表
				if(bo.deleteP01Info(p0100)){
					isok = "1";
				}else{
					isok = "0";
				}
			}
		}
		this.getFormHM().put("isok", isok);
	}
	
}
