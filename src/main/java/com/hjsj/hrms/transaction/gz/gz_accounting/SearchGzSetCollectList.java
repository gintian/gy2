package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
public class SearchGzSetCollectList extends IBusiness {


	public void execute() throws GeneralException {
		String gz_module=(String)this.getFormHM().get("gz_module");
		
		try
		{
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,Integer.parseInt(gz_module));
			ArrayList list=pgkbo.searchGzSetCollectList();
			this.getFormHM().put("setlist", list);
			
			this.getFormHM().put("gz_module", gz_module);
			String flow_flag=(String)this.getFormHM().get("flow_flag");
			this.getFormHM().put("flow_flag", flow_flag);
			this.getFormHM().put("itemid","all");
			this.getFormHM().put("condid","all");
			this.getFormHM().put("proright_str","");
			this.getFormHM().put("userid",this.getUserView().getUserId());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}
