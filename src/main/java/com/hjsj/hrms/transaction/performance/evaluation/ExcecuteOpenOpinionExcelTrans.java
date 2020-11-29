package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>ExcecuteExcelTrans.java</p>
 * <p>Description:输出开放式意见明细表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExcecuteOpenOpinionExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
		//	PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
		//	String code=(String)this.getFormHM().get("code");
			String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			String planid=(String)this.getFormHM().get("planid");
		//	String computeFashion=(String)this.getFormHM().get("computeFashion");
			String pointResult=(String)this.getFormHM().get("pointResult");
			String bodyid=(String)this.getFormHM().get("bodyid");
			
			if(planid!=null && planid.length()>0)
			{
				PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
				String whl="";
//				if(!code.equals("-1"))
//				{
//					if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
//						whl=" and b0110 like '"+code+"%'";
//					else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
//						whl=" and e0122 like '"+code+"%'";
//					
//				}
				whl = (String)this.getFormHM().get("khObjWhere2");
				whl = SafeCode.decode(whl);
				
				
				String order_str =  (String)this.getFormHM().get("order_str");
				order_str=SafeCode.decode(order_str);//界面显示的排序规则
				
				String fileName=pe.getEvaluationTableExcel(5,whl,pointResult,order_str,bodyid,busitype);
				this.getFormHM().put("filename",fileName);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
