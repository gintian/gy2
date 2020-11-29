package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ChangeListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String oper=(String)this.getFormHM().get("oper");
			String param=(String)this.getFormHM().get("param");
			PositionDemand pd = new PositionDemand(this.getFrameconn(),this.getUserView());
			if("1".equals(oper))
			{
				String fieldsetid=(String)this.getFormHM().get("fieldsetid");
				String zItemid=(String)this.getFormHM().get("zItemId");
				ArrayList itemlist = pd.getKItemList(fieldsetid,zItemid,param);
				this.getFormHM().put("itemList",itemlist);
			}else{
				String aparam=(String)this.getFormHM().get("aparam");
				if("2".equals(oper))
				{
					if("".equals(param))
						param=aparam;
					else
						param+=","+aparam;
				}else{
					if(!"".equals(param))
					{
						StringBuffer buf = new StringBuffer("");
						String[] tmp=param.split(",");
						for(int i=0;i<tmp.length;i++)
						{
							if(tmp[i]==null|| "".equals(tmp[i])||tmp[i].equalsIgnoreCase(aparam))
								continue;
							buf.append(","+tmp[i]);
						}
						if(buf.toString().length()>0)
							param=buf.toString().substring(1);
						else 
							param="";
					}
				}
				String fieldsetid=(String)this.getFormHM().get("fieldsetid");
				ArrayList demandFieldList=pd.getZ03Field(param);
				ArrayList postFieldItemList=pd.getKItemList(fieldsetid,"",param);
				String tableStr=pd.getDemand_post(param);
				this.getFormHM().put("demandFieldList", demandFieldList);
				this.getFormHM().put("postFieldItemList", postFieldItemList);
				this.getFormHM().put("table",SafeCode.encode(tableStr));
				this.getFormHM().put("param", param);
			}
			this.getFormHM().put("oper",oper);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
