package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:总额汇总</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 22, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class CollectAmountTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String yearnum=(String)this.getFormHM().get("yearnum");
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.userView,"");
			GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
			String ctrl_by_level=(String)gzXmlMap.get("ctrl_by_level");
			if("1".equals(ctrl_by_level))
				bo.collectData(yearnum);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
