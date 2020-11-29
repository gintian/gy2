package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class DeleteEngagePlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList engagePlanlist=(ArrayList)this.getFormHM().get("selectedList");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operate=(String)hm.get("operate");   // del:删除   issue：发布
		String flag=(String)hm.get("flag");   // 是否需要删除该计划下的用工需求  1:删除  0:不删除
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer whl_str=new StringBuffer("");
		try
		{
			for(Iterator t=engagePlanlist.iterator();t.hasNext();)
        	{
        		LazyDynaBean a=(LazyDynaBean)t.next();
        		whl_str.append(" or z0101='"+a.get("z0101").toString()+"'");
        	}
			if("del".equalsIgnoreCase(operate))
			{
				if("0".equals(flag))
				{			
					dao.update("update z03 set z0101='',z0317='0' where "+whl_str.substring(3));
				}
				else
					dao.delete("delete from  z03  where  "+whl_str.substring(3),new ArrayList());
				dao.delete("delete from z01 where "+whl_str.substring(3),new ArrayList());
			}
			else
			{
				
				for(Iterator t=engagePlanlist.iterator();t.hasNext();)
	        	{
	        		LazyDynaBean a=(LazyDynaBean)t.next();
	        		String z0101=a.get("z0101").toString();
	        		//System.out.println("select count(z0301),z0103 from z03,z01 where z03.z0101=z01.z0101 and z01.z0101='"+z0101+"' group by z01.z0103");
	        		this.frowset=dao.search("select z0103,(select count(z0301) from z03,z01 where z03.z0101=z01.z0101 and z01.z0101='"+z0101+"' ) from z01 where z0101='"+z0101+"'");
	        		if(this.frowset.next())
	        		{
	        			//System.out.println(this.frowset.getInt(1));
	        			if(this.frowset.getInt(2)==0)
	        			{
	        				//throw GeneralExceptionHandler.Handle(new Exception("计划："+this.frowset.getString(1)+" 没有相关联的招聘需求，不予发布！"));
	        				this.getFormHM().put("infoflag", "2");
	        				this.getFormHM().put("info", "计划："+this.frowset.getString(1)+" 没有相关联的招聘需求，不予发布！");
	        				hm.remove("operate");
	        				return;
	        			}else{
		        			this.getFormHM().put("infoflag", "");
	        				this.getFormHM().put("info", "");
		        		}	
	        			
	        		}	
	        		
	        	}
				
				Calendar d=Calendar.getInstance();
				String date=Sql_switcher.dateValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE));
				dao.update("update z01 set z0129='04',z0123="+date+" where "+whl_str.substring(3),new ArrayList());
				dao.update("update z03 set z0319='04' where "+whl_str.substring(3));
				StringBuffer sql = new StringBuffer();
				sql.append("select z0301,z0315,z0313 from z03 where "+whl_str.substring(3));
				this.frowset=dao.search(sql.toString());
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				ArrayList z04list=DataDictionary.getFieldList("Z04",Constant.USED_FIELD_SET);
				PositionDemand bo = new PositionDemand(this.getFrameconn());
				while(this.frowset.next())
				{
					int z0315=this.frowset.getInt("z0315");
					String z0301 = this.frowset.getString("z0301");
					bo.addHireOrder(z03list, z04list, z0315, z0301,this.getUserView());
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		hm.remove("operate");
	}

}
