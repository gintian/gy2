package com.hjsj.hrms.transaction.train.evaluatingStencil;

import com.hjsj.hrms.businessobject.performance.singleGrade.SingGradeTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SubGradeResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			String r3101=(String)this.getFormHM().get("r3101");
			String object_id=(String)this.getFormHM().get("object_id");
			String templateId=(String)this.getFormHM().get("templateId");			
			String userValue=(String)this.getFormHM().get("userValue");	
			String status=(String)this.getFormHM().get("status");
			if (!Pattern.matches("\\d+", r3101)) {
	            r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
	        }
			
			String info="";			
			{
				SingGradeTemplateBo singGradeTemplateBo=new SingGradeTemplateBo(this.getFrameconn(),20);
			//	System.out.println("select * from TRA_EVAL_"+templateId+" where r3101='"+r3101+"' and A0100='"+object_id+"' and NBASE='"+this.getUserView().getDbname()+"'");
				this.frowset=dao.search("select * from TRA_EVAL_"+templateId+" where r3101='"+r3101+"' and A0100='"+object_id+"' and NBASE='"+this.getUserView().getDbname()+"'");
				if(this.frowset.next())
				{
					info="您已提交过该调查，不能再次提交了！";
				}
				if("".equals(info))
				{
					
					info=singGradeTemplateBo.insertGradeResult(object_id,templateId,userValue,status,this.getUserView().getDbname(),r3101);
					String operate="";
					
					operate=ResourceFactory.getProperty("lable.welcomeinv.sumbit");
					if("1".equals(info))
					{
						info=operate+ResourceFactory.getProperty("lable.performance.success")+"！";
					}
					else
						info=operate+ResourceFactory.getProperty("lable.performance.lost")+"！";
				}	
				HashMap ff=singGradeTemplateBo.getGradeValueMap(templateId);
			}
			
			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		
		
	}

}
