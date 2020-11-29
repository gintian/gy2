package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:ShowRemarkTrans.java</p> 
 *<p>Description:展现评语</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 22, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class ShowRemarkTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			String plan_id=(String)hm.get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			ArrayList planList=bo.getPlanList_commonData("7",0,0,this.getUserView(),plan_id,busitype);
			String planID="";
			String object_id="";
			LazyDynaBean abean=null;
			if(hm.get("b_perRemark0")!=null&& "query0".equals((String)hm.get("b_perRemark0")))
			{
			    if(planList.size()>0)
				planID=((CommonData)planList.get(0)).getDataValue();
				hm.remove("b_perRemark0");
			}
			else if(hm.get("b_perRemark0")!=null&& "query".equals((String)hm.get("b_perRemark0")))
			{
				planID=(String)this.getFormHM().get("planIds");
				hm.remove("b_perRemark0");
			}
			else
			{
				planID=(String)this.getFormHM().get("planIds");
				object_id=PubFunc.decrypt((String)hm.get("codeitemid"));
			}
			String remark="";
			String a0101="";
			if(planID.length()>0 && object_id.trim().length()>0)
			{
			    abean=getObjectID(planID,object_id);			
			    remark=bo.getPerObjectRemark(planID,(String)abean.get("object_id"));
			    a0101=(String)abean.get("a0101");
			}
			
			this.getFormHM().put("objectName",a0101);
			this.getFormHM().put("remark",remark);
			this.getFormHM().put("planIds",planID);
			this.getFormHM().put("plan_ids",planID);
			this.getFormHM().put("perPlanList",planList);
			
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			this.getFormHM().put("busitype",busitype);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	public LazyDynaBean getObjectID(String planid,String object_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			 ContentDAO dao = new ContentDAO(this.frameconn);
			 String sql="select object_id,a0101 from per_result_"+planid+" order by b0110,e0122";
			 if(object_id.length()>0)
				 sql="select object_id,a0101 from per_result_"+planid+" where object_id='"+object_id+"'";
			 this.frowset=dao.search(sql);
			 if(this.frowset.next())
			 {
				 
				 abean.set("object_id",this.frowset.getString(1));
				 abean.set("a0101", this.frowset.getString(2));
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
}
