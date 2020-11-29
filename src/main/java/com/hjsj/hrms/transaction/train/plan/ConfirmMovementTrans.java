package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ConfirmMovementTrans extends IBusiness {

	public void execute() throws GeneralException {
		String msg = "true";
		String[] cid = null;
		String ids = (String)this.getFormHM().get("ids");
		if(ids!=null&ids.length()>0)
			cid = ids.split(",");	
		ContentDAO dao=null;
		try
		{
			dao=new ContentDAO(this.getFrameconn());
			if(!(cid==null||cid.length==0))
			{		
				StringBuffer exper = new StringBuffer("");
				StringBuffer sql_whl=new StringBuffer("");
				for(int i=0;i<cid.length;i++)
				{
					RecordVo vo = new RecordVo("r31");
					vo.setString("r3101", cid[i]);
					vo = dao.findByPrimaryKey(vo);
					String r3130 = vo.getString("r3130");
					r3130=r3130!=null?r3130:"";
					r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("%2526lt;","<").replaceAll("%2526gt;",">");
					if("02".equals(vo.getString("r3127"))){
						sql_whl.append(",'"+vo.getString("r3101")+"'");
					}else if("07".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.dismissed")+"!");
					}else if("03".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.approved")+"!");
					}else if("04".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.published")+"!");
					}else if("01".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.drafting")+"!");
					}else if("06".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.end")+"!");
					}else if("08".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.altrial")+"!");
					}else if("09".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.altrial.stop")+"!");
					}
				}
				if(sql_whl.length()>1){
					dao=new ContentDAO(this.getFrameconn());		
					dao.update("update r31 set r3127='03' where  r3127='02'  and r3101 in ("+sql_whl.toString().trim().substring(1)+")");
					//培训预算
					TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
					if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
						String pri[] = sql_whl.toString().split(",");
						for (int i = 0; i < pri.length; i++) {
							if(pri[i]!=null||pri[i].length()>0)
								if(pri[i].length() >= 1){									
									tbb.updateTrainBudget("0", pri[i].substring(1,pri[i].length()-1), -999999,null);
								}
						}
					}
				}
   			    if(exper.length()>1)
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.job.fail")+"\n"+exper.toString()));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		this.getFormHM().put("msg", msg);
	}

}
