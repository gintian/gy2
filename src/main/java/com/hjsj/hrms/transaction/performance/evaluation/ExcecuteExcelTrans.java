package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>ExcecuteExcelTrans.java</p>
 * <p>Description:绩效评估导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExcecuteExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
		//	PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
		//	String code=(String)this.getFormHM().get("code");
			String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			String planid=(String)this.getFormHM().get("planid");
			String computeFashion=(String)this.getFormHM().get("computeFashion");
			String pointResult=(String)this.getFormHM().get("pointResult");
			String bodyid=(String)this.getFormHM().get("bodyid");
			String a0100="";
			String showbenbu="";
			String showmethod="";
			String showaband="";
			String templateid="";
			String code=(String)this.getFormHM().get("code");
			if(computeFashion!=null&& "6".equalsIgnoreCase(computeFashion.trim())){
				a0100=(String)this.getFormHM().get("a0100");// 绩效评估 指标票书记占比反馈
				showbenbu=(String)this.getFormHM().get("showbenbu");// 绩效评估 指标票书记占比反馈
				showmethod=(String)this.getFormHM().get("showmethod");// 绩效评估 指标票书记占比反馈
				showaband=(String)this.getFormHM().get("showaband");// 绩效评估 指标票书记占比反馈
				templateid=(String)this.getFormHM().get("templateid");
			}
			if(planid!=null && planid.length()>0)
			{
				PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"",this.userView);
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

				if(computeFashion!=null&& "6".equalsIgnoreCase(computeFashion.trim())){
				
					pe=new PerEvaluationBo(this.getFrameconn(),planid,templateid,this.userView);
					pe.setA0100(a0100);
					pe.setShowbenbu(showbenbu);
					pe.setShowmethod(showmethod);
					pe.setAband(showaband);
				}
				
				pe.setShowDetails((String) formHM.get("showDetails"));
				pe.setObject_type((String) formHM.get("object_type"));
				
				String fileName=pe.getEvaluationTableExcel(Integer.parseInt(computeFashion),whl,pointResult,order_str,bodyid,busitype);
				this.getFormHM().put("filename",fileName);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
