package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训计划</p>
 * <p>Description:删除培训计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class DelPlanTrainTrans extends IBusiness {

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
			String ids="";
			StringBuffer exper = new StringBuffer("");
			ArrayList valuelist = new ArrayList();
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString("r2509");
				namestr =  vo.getString("r2502");
				namestr=namestr.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				String model =  vo.getString("model");
				if("1".equals(model)){
					if("02".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.submit.approval")+"!");
						continue;
					}else if("03".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.approved")+"!");
						continue;
					}else if("04".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.published")+"!");
						continue;
					}else if("05".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.perform")+"!");
						continue;
					}else if("06".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.end")+"!");
						continue;
					}else if("09".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.suspended")+"!");
						continue;
					}
					
				}else{
					if("01".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.submit.drafting.error")+"!");
						continue;
					}else if("07".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.dismissed.error")+"!");
						continue;
					}else if("04".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.published.error")+"!");
						continue;
					}else if("05".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.perform.error")+"!");
						continue;
					}else if("06".equals(sp)){
						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.end.error")+"!");
						continue;
					}
//					else if(sp.equals("09")){
//						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.suspended.error")+"!");
//						continue;
//					}
					if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
						if("03".equals(sp)|| "09".equals(sp)){
							if(tbb.isChildPlan(vo.getString("r2501"),vo.getString("r2503"),vo.getString("b0110"),vo.getString("e0122"))){
								exper.append("\n\n["+namestr+"]存在该年度的下级单位计划，请先删除下级计划!");
								continue;
							}
						}
					}
				}
				
				if(!bo.checkPlanPiv(vo.getString("r2501"), this.userView))
				    continue;
				vo.removeValue("model");
				ids=ids+"'"+vo.getString("r2501")+"',";
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
				if(ids.length()>0&&ids!=null){
					ids=ids.substring(0, ids.length()-1);
					String sql="update r31 set r3125='' where r3125 in ("+ids+")";
					dao.update(sql);
				}
			}
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
