package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveMoneyStyleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String cname="";
		    String ctoken="";
		    String cunit="";
		    String nratio="";
		  //String cstate="";
		   int nstyleid=0;
		    int flag=0;
		    String id=(String)this.getFormHM().get("nstyleid");
		    if(id!=null &&id.trim().length()!=0)
		    {
		    	nstyleid=Integer.parseInt(id);
		    	flag=0;
		    }
		    
		    else
		    {
		        MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
		        nstyleid=bo.getMoneyStyleId();
		        flag=1;
		    }
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    cname=(String)this.getFormHM().get("cname");
		    ctoken=(String)this.getFormHM().get("ctoken");
		    cunit=(String)this.getFormHM().get("cunit");
		    nratio=(String)this.getFormHM().get("nratio");
		    RecordVo vo = new RecordVo("moneystyle");
		    vo.setInt("nstyleid",nstyleid);
		    vo.setString("ctoken",PubFunc.hireKeyWord_filter_reback(ctoken));
		    vo.setString("cunit",cunit);
		    vo.setString("nratio",nratio);
		    vo.setString("cstate","");
		    vo.setString("cname",cname);
		    if(flag==0)
		    {
		    	dao.updateValueObject(vo);
		    }
		    else if(flag==1)
		    {
		    	dao.addValueObject(vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
