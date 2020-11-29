package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SubmitGradeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn);
			String z0127=(String)this.getFormHM().get("z0127");
			String object_id=(String)this.getFormHM().get("object_id");
			String templateId=(String)this.getFormHM().get("templateId");			
			String userValue=(String)this.getFormHM().get("userValue");
			String mainBodyId=(String)this.getFormHM().get("mainBodyID");		
			String status=(String)this.getFormHM().get("status");
			String scoreFlag=(String)this.getFormHM().get("scoreFlag");	// 1:标度打分  2：混合打分
			String hireState=(String) this.getFormHM().get("hireState");//测评阶段 初试复试
			
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		    parameterSetBo.createEvaluatingTableAbsolutely(templateId);//zzk 重新生成招聘测评结果表 
			String info=" ";			
			String z0301="";
			this.frowset=dao.search("select zp.zp_pos_id from zp_pos_tache zp where  resume_flag='12' and zp.a0100='"+object_id+"'");
			if(this.frowset.next())
				z0301=this.frowset.getString("zp_pos_id");
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
			if(hireState!=null&&hireState.trim().length()>0){
				batchGradeBo.setHireState(hireState);
			}
			info=batchGradeBo.insertGradeResult(object_id,templateId,userValue,mainBodyId,status,z0301,scoreFlag);
			String operate="";
		
			operate=ResourceFactory.getProperty("lable.welcomeinv.sumbit");
			if("1".equals(info))
			{
				ParameterSetBo pb=new ParameterSetBo(this.getFrameconn());
				pb.setHire_obj_code(z0127);
				pb.setHireState(hireState);
				pb.CalculateGradeResult(templateId,status,object_id,z0301);
				info=operate+ResourceFactory.getProperty("lable.performance.success")+"！";
			}
			else
				info=operate+ResourceFactory.getProperty("lable.performance.lost")+"！";
				
			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	

}
