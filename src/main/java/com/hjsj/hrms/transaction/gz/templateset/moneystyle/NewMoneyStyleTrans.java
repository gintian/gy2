package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class NewMoneyStyleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String cname="";
			String ctoken="";
			String cunit="";
			String nratio="";
			String nstyleid="";
			String isVisable="";
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			String opt=(String)hm.get("opt");
			if("add".equals(opt))//新建
			{
				cname="";
				ctoken="";
				cunit="";
				nratio="1.00";
				nstyleid="";
				isVisable="new";
				
			}
			else if("edit".equals(opt))//修改
			{
				isVisable="edit";
				nstyleid=(String)hm.get("nstyleid");
				String sql ="select * from moneystyle where nstyleid="+nstyleid;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search(sql);
				while(this.frowset.next()){
					cname=this.frowset.getString("cname")==null|| "null".equals(this.frowset.getString("cname"))?"":this.frowset.getString("cname");
					ctoken=this.frowset.getString("ctoken")==null|| "null".equals(this.frowset.getString("ctoken"))?"":this.frowset.getString("ctoken");
					cunit=this.frowset.getString("cunit")==null|| "null".equals(this.frowset.getString("cunit"))?"":this.frowset.getString("cunit");
					nratio=bo.getXS(String.valueOf(this.frowset.getFloat("nratio")),2);
					nstyleid=this.frowset.getString("nstyleid")==null|| "null".equals(this.frowset.getString("nstyleid"))?"":this.frowset.getString("nstyleid");
				}
				
			}
			this.getFormHM().put("cname",cname);
			this.getFormHM().put("ctoken",ctoken);
			this.getFormHM().put("cunit",cunit);
			this.getFormHM().put("nratio",nratio);
			this.getFormHM().put("nstyleid",nstyleid);
			this.getFormHM().put("isVisable",isVisable);
		       
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
