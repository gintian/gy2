package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanTraceBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;
/**
 * 年计划任务跟踪
 * 
 * @author haosl
 *
 */
public class YearPlanTraceTrans extends IBusiness {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		/**
		 * opt 0=任务列表和责任人信息
		 * 	   1=年度信息
		 */
		if(this.userView.getA0100()==null||userView.getA0100().trim().length()<1){//非自助用户不能使用该功能
			throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
		}
		String opt = (String)this.getFormHM().get("opt");
		YearPlanTraceBo traceBo = new YearPlanTraceBo(this.frameconn, this.userView);
		if("0".equals(opt)){//查询指定年度下的计划列表
			Integer year = (Integer) this.getFormHM().get("year");
			List<Map> planList = traceBo.getPlanList(year);
			this.getFormHM().put("planList", planList);
		}else if("1".equals(opt)){//查询的年度列表
			List<Integer> yearList = traceBo.getYearList();
			this.getFormHM().put("yearList", yearList);
		}else if("2".equals(opt)){//根据季度查询汇报过程信息
			Integer planId = (Integer)this.getFormHM().get("planId");
			Integer quarter =(Integer)this.getFormHM().get("quarter");
			List<String> infos = traceBo.getProcessInfo(planId,quarter);
			this.getFormHM().put("infos", infos);
		}
	}

}
