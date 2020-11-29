package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelHisPlanTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");	
		TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		try{
			String namestr="";
			StringBuffer exper = new StringBuffer("");
			ArrayList valuelist = new ArrayList();
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				if(!bo.checkPlanPiv(vo.getString("r2501"), this.userView))
				    continue;
				String sp = vo.getString("r2509");
				namestr =  vo.getString("r2502");
				namestr=namestr.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				String model =  vo.getString("model");
				
					if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
						if("03".equals(sp)|| "09".equals(sp)){
							if(tbb.isChildPlan(vo.getString("r2501"),vo.getString("r2503"),vo.getString("b0110"),vo.getString("e0122"))){
								exper.append("\n\n["+namestr+"]存在该年度的下级单位计划，请先删除下级计划!");
								continue;
							}
						}
					}
				vo.removeValue("model");
				valuelist.add(vo);
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
			if(valuelist.size()>0){
				//培训预算
				if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
					for (int i = 0; i < list.size(); i++) {
						RecordVo vo=(RecordVo)valuelist.get(i);
						if("03".equals(vo.getString("r2509"))||"09".equals(vo.getString("r2509")))
							tbb.updateTrainPlanBudget("1", vo.getString("r2501"), 0, false);
					}
			    }
				ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.deleteValueObject(valuelist);
			}
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
