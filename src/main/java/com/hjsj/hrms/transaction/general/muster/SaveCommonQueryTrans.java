package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 0521010025
 * <p>Title:SaveCommonQueryTrans.java</p>
 * <p>Description>:SaveCommonQueryTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 20, 2010 11:18:30 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveCommonQueryTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String condid=(String)this.getFormHM().get("condid");
			condid=condid==null?"":condid;
			String tabID=(String)this.getFormHM().get("tabID");
			MusterXMLStyleBo mxbo=new MusterXMLStyleBo(this.getFrameconn(),tabID);
			mxbo.setPropertyValue(MusterXMLStyleBo.Param, "usual_query", condid);
			mxbo.saveSetValue();
			String tt="0";
			if(condid==null|| "".equals(condid.trim()))
				tt="1";
			this.getFormHM().put("tt", tt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
