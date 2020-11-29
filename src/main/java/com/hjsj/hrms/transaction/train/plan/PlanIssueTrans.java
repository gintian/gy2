package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class PlanIssueTrans extends IBusiness {

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
					String content = vo.getString("r3125");//是否关联培训计划
					content=content!=null&&content.trim().length()>0?content:"";
					if("03".equals(vo.getString("r3127"))){
						sql_whl.append(",'"+vo.getString("r3101")+"'");
					}
					String r3130 = vo.getString("r3130");
					r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("%2526lt;","<").replaceAll("%2526gt;",">");
					if("07".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.dismissed.error")+"!");
					}else if("02".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.approvalr.error")+"!");
					}else if("04".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.published.error")+"!");
					}else if("01".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.drafting.error")+"!");
					}else if("06".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.end.error")+"!");
					}else if("08".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.altrial.error")+"!");
					}else if("09".equals(vo.getString("r3127"))){
						exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.suspend.error")+"!");
					}
				}
				if(sql_whl.length()>1){
					dao.update("update r31 set r3127='04' where  r3127='03'  and r3101 in ("+sql_whl.toString().trim().substring(1)+")");
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
