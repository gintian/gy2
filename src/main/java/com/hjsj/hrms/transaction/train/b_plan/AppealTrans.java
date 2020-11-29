package com.hjsj.hrms.transaction.train.b_plan;

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
 * <p>Description:报批培训计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class AppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		try{
			String namestr="";
			StringBuffer exper = new StringBuffer("");
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString("r2509");
				String r2501 =  vo.getString("r2501");
				String r2502 = vo.getString("r2502");
				r2502=r2502.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				if("02".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.submit.approval")+"!");
					continue;
				}else if("03".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.approved")+"!");
					continue;
				}else if("04".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.published")+"!");
					continue;
				}else if("05".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.perform")+"!");
					continue;
				}else if("06".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.end")+"!");
					continue;
				}else if("09".equals(sp)){
					exper.append("\n\n["+r2502+"]"+ResourceFactory.getProperty("train.b_plan.app.suspended")+"!");
					continue;
				}
				
				if(!bo.checkPlanPiv(r2501, this.userView))
                    continue;
				namestr+="'"+r2501+"',";
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
			if(namestr.trim().length()>0){
				StringBuffer sqlstr = new StringBuffer("");
				sqlstr.append("update r25 set r2509='02' where R2501 in(");
				sqlstr.append(namestr.substring(0,namestr.length()-1));
				sqlstr.append(")");
				ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.update(sqlstr.toString());
			}
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
