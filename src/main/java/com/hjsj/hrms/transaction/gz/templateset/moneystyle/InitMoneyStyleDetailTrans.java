package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitMoneyStyleDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String nstyleid=(String)hm.get("nstyleid");
			String sql = "select nstyleid,CAST(nitemid as numeric(10,2)) as nitemid,cname,nflag,cstate" ;
			String where_sql=" from moneyitem where nstyleid='"+nstyleid+"'";
			String columns="nstyleid,nitemid,cname";
			/*MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			this.getFormHM().put("moneyDetailList",bo.getMoneyStyleDetailInfo(nstyleid));*/
			this.getFormHM().put("nstyleid",nstyleid);
			this.getFormHM().put("sql",sql);
			this.getFormHM().put("where_sql",where_sql);
			this.getFormHM().put("columns",columns);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
