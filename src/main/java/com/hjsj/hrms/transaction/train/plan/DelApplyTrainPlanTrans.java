package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 计划审批
 * @author Owner
 *
 */
public class DelApplyTrainPlanTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("plan_set_table");
		ArrayList list=(ArrayList)hm.get("plan_set_record");		
		ContentDAO dao=null;
		try
		{
			if(!(list==null||list.size()==0))
			{
			    String where = "";
                if (!this.userView.isSuper_admin()) {
                    where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
                }

                String cid = "";
                for (int i = 0; i < list.size(); i++) {
                    RecordVo vo = (RecordVo) list.get(i);
                    String r3101 = vo.getString("r3101");
                    cid += r3101;
                }
                
                StringBuffer exper = new StringBuffer("");
                if (!this.userView.isSuper_admin())
                    exper.append(TrainClassBo.checkclass(cid, this.frameconn, where));
                
                if (exper.length() > 1) {
                    String mes = exper.toString() + ResourceFactory.getProperty("train.job.class.nopiv");
                    throw GeneralExceptionHandler.Handle(new Exception(mes));
                }
                
				StringBuffer sql_whl=new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					String r3130 = vo.getString("r3130");
					r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("%2526lt;","<").replaceAll("%2526gt;",">");
					if("02".equals(vo.getString("r3127"))){
						sql_whl.append(",'"+vo.getString("r3101")+"'");
					}else if("07".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.dismissed.error")+"!");
					}else if("03".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.approved.error")+"!");
					}else if("04".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.published.error")+"!");
					}else if("01".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.submit.drafting.error")+"!");
					}else if("06".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.end.error")+"!");
					}else if("08".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.altrial.error")+"!");
					}else if("09".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.delete.stop.error")+"!");
					}
				}	
				if(exper.length()>1)
					throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
				dao=new ContentDAO(this.getFrameconn());
				
				if(sql_whl.length()>1)
					dao.delete("delete from "+name+" where  r3101 in ("+sql_whl.toString().trim().substring(1)+") " + where,new ArrayList());
			}
			
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
		

	}

	
	
}