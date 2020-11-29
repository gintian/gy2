package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:SparePositionQueryTrans.java</p>
 * <p>Description:空缺职位查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 21, 2006 10:56:25 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SparePositionQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String codeid=(String)hm.get("codeid");  //组织编码范围
		if(this.getUserView().isSuper_admin()|| "1".equals(this.getUserView().getGroupId()))
		{
			codeid="";
		}
		else
		{
			/*if(this.getUserView().getStatus()==0)
			{
				String unitID= this.getUserView().getUnit_id();
				if(unitID==null||unitID.equals(""))
				{
					codeid="-1";
				}
				else if(unitID.trim().length()==3)
				{
					codeid="";
				}else
				{
					String [] temp=unitID.split("`");
					for(int i=0;i<temp.length;i++)
					{
						if(temp[i]==null||temp[i].equals(""))
							continue;
						codeid+="`"+temp[i].substring(2);
					}
				}
			}
			else
			{
				String codeset = this.getUserView().getManagePrivCode();
				String codevalue=this.getUserView().getManagePrivCodeValue();
				if(codeset==null||codeset.equals(""))
				{
					codeid="-1";
				}else
				{
					if(codevalue==null)
						codevalue="";
					codeid=codevalue;
				}
			}*/
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			codeid=this.userView.getUnitIdByBusi("7");
			String info=bo2.hasSetParam(this.userView);
			if(info!=null&&info.trim().length()>0){
				throw GeneralExceptionHandler.Handle(new Exception(info));
			}
		}
			//codeid=this.getUserView().getManagePrivCodeValue();
		
		ArrayList sparePositionList=new ArrayList();
		
		PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
		ArrayList list=positionDemand.getSparePositionInfo(codeid);
		sparePositionList=(ArrayList)list.get(0);
		HashMap sparePositionMap=(HashMap)list.get(1);
		String temp=(String)list.get(2);
		String[] tempName=temp.split("~");
		
		this.getFormHM().put("sparePositionMap",sparePositionMap);
		this.getFormHM().put("sparePositionList",sparePositionList);
		if(tempName!=null&&tempName.length==2)
		{
			this.getFormHM().put("planNumberFieldName",tempName[1]);
			this.getFormHM().put("actualNumberFieldName",tempName[0]);
		}
	}

}
