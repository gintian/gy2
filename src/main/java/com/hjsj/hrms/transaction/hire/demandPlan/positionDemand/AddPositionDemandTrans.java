package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class AddPositionDemandTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String z0301=(String)hm.get("z0301");
			//z0301 = PubFunc.decrypt(z0301);
			String opt=(String)hm.get("opt");   //  0:添加  1：修改
			if("1".equals(opt)){
				//修改时,要将加密的参z0301，解密回来
				z0301=PubFunc.decrypt(z0301);
			}
			
			ArrayList positionDemandDescList=(ArrayList)this.getFormHM().get("positionDemandDescList");
			String    isRevert=(String)this.getFormHM().get("isRevert");
			String    mailTemplateID=(String)this.getFormHM().get("mailTemplateID");
			ArrayList posConditionList=(ArrayList)this.getFormHM().get("posConditionList");
			
			PositionDemand bo=new PositionDemand(this.getFrameconn(),this.userView);
			String az0301=bo.addPositionDemand(opt,positionDemandDescList,isRevert,mailTemplateID,z0301);
			if("0".equals(opt))
			{	
//				System.out.println(this.getUserView().getStatus());0业务用户， 4自助用户
//				System.out.println(this.getUserView().getA0100());
//				System.out.println(this.getUserView().getUserFullName());自助用户全名
//				System.out.println(this.getUserView().getUserName());账号名
				String createPerson=this.getUserView().getUserFullName()!=null&&!"".equals(this.getUserView().getUserFullName())?this.getUserView().getUserFullName():this.getUserView().getUserName();
				String sql = "update z03 set z0309='"+createPerson+"' where z0301='"+az0301+"'";
				int status=this.getUserView().getStatus();
				String a0100=this.getUserView().getA0100();
				String nabse=this.userView.getDbname();
				String loginName=this.getUserView().getUserName();
				if(status==0){//0业务用户， 4自助用户
					sql = "update z03 set z0309='"+createPerson+"',currappuser='"+loginName+"' where z0301='"+az0301+"'";
				}else if(status==4){
					sql = "update z03 set z0309='"+createPerson+"',currappuser='"+nabse+a0100+"' where z0301='"+az0301+"'";
				}

				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.update(sql);
			}
			
			bo.addCurrappusername(az0301);
			
			ArrayList list=bo.getParamConditionList(posConditionList);
			DemandCtrlParamXmlBo xmlBo=new DemandCtrlParamXmlBo(this.getFrameconn(),az0301);
			HashMap map=new HashMap();
			map.put("simple",list);
			xmlBo.updateNode("simple",map,az0301);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	public static void main(String[] arg)
	{
		String ss=" , , ";
		String[] sss=ss.split(",");
		System.out.println(sss.length);
		
	}
	public HashMap getNbaseMap()
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search("select pre from dbname ");
			while(rs.next())
			{
				map.put(rs.getString("pre").toUpperCase(), "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
