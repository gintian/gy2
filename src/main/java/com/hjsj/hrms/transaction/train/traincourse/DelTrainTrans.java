package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:删除培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class DelTrainTrans extends IBusiness {

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
			TrainClassBo bo = new TrainClassBo(this.frameconn);
			
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString("r3127");
				namestr =  vo.getString("r3130");
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
					}
					
				}else{
//					if(sp.equals("01")){
//						exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.submit.drafting.error")+"!");
//						continue;
//					}else 
					if("07".equals(sp)){
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
				}
				
				if(!bo.checkClassPiv(vo.getString("r3101"), this.userView))
				    continue;
				
				vo.removeValue("model");
				vo.removeValue("person");
				valuelist.add(vo);
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
			if(valuelist.size()>0){
				//培训预算
				TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
				if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
					for (int i = 0; i < list.size(); i++) {
						RecordVo vo=(RecordVo)valuelist.get(i);
						if("03".equals(vo.getString("r3127"))||"09".equals(vo.getString("r3127")))
							tbb.updateTrainBudget("1", vo.getString("r3101"), -999999,null);
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
