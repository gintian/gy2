package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveAndContinueAddStyleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
		   String cname="";
	       String ctoken="";
	       String cunit="";
	       String nratio="";
	       String isVisable="new";
	  //String cstate="";
	       int nstyleid=0;
	       MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
           nstyleid=bo.getMoneyStyleId();
           ContentDAO dao = new ContentDAO(this.getFrameconn());
	       cname=(String)this.getFormHM().get("cname");
	       ctoken=(String)this.getFormHM().get("ctoken");
	       cunit=(String)this.getFormHM().get("cunit");
	       nratio=(String)this.getFormHM().get("nratio");
	       RecordVo vo = new RecordVo("moneystyle");
	       vo.setInt("nstyleid",nstyleid);
	       vo.setString("ctoken",ctoken);
	       vo.setString("cunit",cunit);
	       vo.setString("nratio",nratio);
	       vo.setString("cstate","");
	       vo.setString("cname",cname);
	       dao.addValueObject(vo);
	       this.getFormHM().put("cname","");
		   this.getFormHM().put("ctoken","");
		   this.getFormHM().put("cunit","");
		   this.getFormHM().put("nratio",nratio);
		   this.getFormHM().put("nstyleid","");
		   this.getFormHM().put("isVisable",isVisable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
