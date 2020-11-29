package com.hjsj.hrms.transaction.attestation.unicom;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 10300130052
 * <p>Title:ShowResultPanelTrans.java</p>
 * <p>Description>:ShowResultPanelTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 9, 2010 10:38:01 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ShowResultPanelTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			ResultBo bo = new ResultBo(this.getFrameconn());
			ArrayList myList = bo.getMyResultList(this.getUserView().getA0100(), "0", "-1", this.getUserView());
		    this.getFormHM().put("myResultList", myList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
