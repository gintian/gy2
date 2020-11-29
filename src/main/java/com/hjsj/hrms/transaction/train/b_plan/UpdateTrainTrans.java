package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
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
public class UpdateTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");	

		try{
			String namestr="";
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
						continue;
					}else if("03".equals(sp)){
						continue;
					}else if("04".equals(sp)){
						continue;
					}else if("05".equals(sp)){
						continue;
					}else if("06".equals(sp)){
						continue;
					}else if("09".equals(sp)){
						continue;
					}
					
				}else{
					if("01".equals(sp)){
						continue;
					}else if("07".equals(sp)){
						continue;
					}else if("04".equals(sp)){
						continue;
					}else if("05".equals(sp)){
						continue;
					}else if("06".equals(sp)){
						continue;
					}
//					else if(sp.equals("09")){
//						continue;
//					}
				}
				vo.removeValue("model");
				valuelist.add(vo);
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
			if(valuelist.size()>0){
				//培训预算
				TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
				if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
					for (int i = 0; i < list.size(); i++) {
						RecordVo vo=(RecordVo)list.get(i);
						if("03".equals(vo.getString("r2509"))||"09".equals(vo.getString("r2509")))
							tbb.updateTrainPlanBudget("1", vo.getString("r2501"), vo.getDouble("r2506"), false);
					}
			    }
				ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.updateValueObject(valuelist);
			}
			
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
