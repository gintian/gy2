package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.businessobject.hire.EngagePlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitEngagePlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String origin=(String)hm.get("origin");
			String planid=(String)hm.get("z0101");
			if(planid==null){
				planid="0";
			}else{
				/**在修改计划时，z0101是被加密的，需要解密回来**/
				planid=PubFunc.decrypt(planid);
			}
			
			
			hm.remove("z0101");
			
			if(!this.userView.isSuper_admin()){
			    String piv = this.userView.getUnitIdByBusi("7");
			    if(piv != null && piv.length() > 0 && piv.indexOf("UN") == -1)
			        throw new GeneralException("",ResourceFactory.getProperty("hire.position.piv.eorror"),"",""); 
			}
			
			ArrayList list=DataDictionary.getFieldList("z01",Constant.USED_FIELD_SET);
			EngagePlanBo engagePlanBo=new EngagePlanBo(this.getFrameconn());
			ArrayList planFieldList=engagePlanBo.getPlanFieldList(list,this.getUserView(),planid,origin);
			this.getFormHM().put("planFieldList",planFieldList);
			String returnflag="";
			if(hm.get("returnflag")!=null)
			{
				returnflag=(String)hm.get("returnflag");
			}
			else
			{
				returnflag=(String)this.getFormHM().get("returnflag");
			}
			this.getFormHM().put("returnflag", returnflag);
			/*
			
			this.getFormHM().put("operate","a");
			if(origin.equals("b"))
				this.getFormHM().put("z0129","01");
			
			ArrayList hireObjectList=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from codeitem where codesetid='35'");
			while(this.frowset.next())
			{
				CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				hireObjectList.add(vo);
			}
			this.getFormHM().put("hireObjectList",hireObjectList);
			
			String unitcode="";
			if(!this.getUserView().isAdmin())
			{
				PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
				unitcode=positionDemand.getUnitID(this.getUserView());
			}
			this.getFormHM().put("unitID",unitcode);
			
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
