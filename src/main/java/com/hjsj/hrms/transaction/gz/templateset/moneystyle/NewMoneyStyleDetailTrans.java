package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.DecimalFormat;
import java.util.HashMap;

public class NewMoneyStyleDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String cname="";//名称
			String nitemid="";//面值
			
			String nstyleid="";//货币种类id
			String isVisable="";
			String beforenitemid="";
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			nstyleid=(String)this.getFormHM().get("nstyleid");
			String opt=(String)hm.get("opt");
			if("add".equals(opt))//新建
			{
				cname="";
				nitemid="";
				beforenitemid="";
				isVisable="new";
				
			}
			else if("edit".equals(opt))//修改
			{
				DecimalFormat format = new DecimalFormat("0.00");
				isVisable="edit";
				nitemid=(String)hm.get("nitemid");
				String sql ="select  nstyleid,CAST(nitemid as numeric(10,2)) as nitemid,cname,nflag,cstate from moneyitem where nstyleid="+nstyleid+" and nitemid='"+nitemid+"'";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search(sql);
				while(this.frowset.next()){
					cname=this.frowset.getString("cname")==null|| "null".equals(this.frowset.getString("cname"))?"":this.frowset.getString("cname");
					nitemid=this.frowset.getString("nitemid")==null|| "null".equals(this.frowset.getString("nitemid"))?"":this.frowset.getString("nitemid");
				}
				if(!"".equals(nitemid))
				{
		    		nitemid=format.format(Double.parseDouble(nitemid));
				}
				beforenitemid=nitemid;
				
			}
			this.getFormHM().put("cname",cname);
			this.getFormHM().put("nitemid",nitemid);
			this.getFormHM().put("nstyleid",nstyleid);
			this.getFormHM().put("isVisable",isVisable);
		    this.getFormHM().put("beforenitemid",beforenitemid);   
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

		
}


