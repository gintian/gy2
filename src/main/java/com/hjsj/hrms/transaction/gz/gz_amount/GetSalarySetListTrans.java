package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 30200710255
 * <p>Title:GetSalarySetListTrans.java</p>
 * <p>Description>:GetSalarySetListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 10, 2009 4:25:04 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetSalarySetListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String itemid=(String)map.get("itemid");
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),0);
			String salaryid=bo.getSalarySet(itemid);
			bo.setUv(this.userView);
			ArrayList list = bo.getAllSalarySet(salaryid);
			this.getFormHM().put("salarySetList", list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
