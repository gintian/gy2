package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.gz.premium.PremiumBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class BatchComputePremiumTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ArrayList itemids=(ArrayList)this.getFormHM().get("itemids");
			String  operateUnitCode=(String)this.getFormHM().get("operateUnitCode");  //操作单位
			String  year=(String)this.getFormHM().get("year");
			String  month=(String)this.getFormHM().get("month");
			
			PremiumBo bo=new PremiumBo(this.getFrameconn(),this.getUserView());
			String premiumSetId=bo.getXml().getNodeAttributeValue("/Params/BONUS_SET","setid"); //"B05";   //奖金子集
			
			
			/**人员计算过滤条件*/
			String strwhere="select codeitemid from organization where parentid='"+operateUnitCode+"'";
			bo.computing(strwhere,itemids,year,month,operateUnitCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
