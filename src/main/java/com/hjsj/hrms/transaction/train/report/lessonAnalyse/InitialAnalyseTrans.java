package com.hjsj.hrms.transaction.train.report.lessonAnalyse;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitialAnalyseTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);
		this.getFormHM().put("r5000", "");
		this.getFormHM().put("name", "");
		this.getFormHM().put("lprogress_t", "0");
		this.getFormHM().put("lprogress_d", "100");
		this.getFormHM().put("trainnbases", this.getTrainNbases());
    }
    
    private String getTrainNbases()
    {
        String nbases = "";
        
        TrainCourseBo trainCourseBo = new TrainCourseBo(this.userView, this.frameconn);
        ArrayList nbaseList = trainCourseBo.getTrainNbases("1");
        nbases = trainCourseBo.getNbasesFromList(nbaseList);
        
        return nbases;
        
    }
}
