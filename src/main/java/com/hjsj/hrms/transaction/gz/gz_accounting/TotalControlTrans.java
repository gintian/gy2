package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:总额控制</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 24, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class TotalControlTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String gz_module=(String)this.getFormHM().get("gz_module");
			String desc="不予提交工资";
			//工资发放报批里用到的参数
			String userid=(String)this.getFormHM().get("userid");
			String isAppealData=(String)this.getFormHM().get("isAppealData");
			if(isAppealData!=null&&isAppealData.trim().length()>0)
				desc="不予报批";
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			 
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				String amount_ctrl_ff=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
				if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
					isControl=amount_ctrl_ff;
				
			}
			
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			bo.setDesc(desc);
			String info="success";
			String alertInfo = "";
			if("1".equals(bo.getIsControl())&& "1".equals(isControl))
			{ 
				info=bo.calculateTotal();
				if(!"success".equals(info))
				{
					if(info.indexOf(desc)==-1)
						this.getFormHM().put("isOver","0");
					else
						this.getFormHM().put("isOver","1");
					alertInfo = info.replaceAll(desc, "是否继续？").replaceAll("！", "");
				}
			}
			this.getFormHM().put("alertInfo", SafeCode.encode(alertInfo));
			this.getFormHM().put("ctrlType", bo.getCtrlType());
			this.getFormHM().put("info",SafeCode.encode(info));
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("userid",userid);
			this.getFormHM().put("isAppealData", isAppealData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
