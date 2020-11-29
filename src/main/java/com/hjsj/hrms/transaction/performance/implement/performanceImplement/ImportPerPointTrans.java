package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchTargetCardSetTrans.java</p>
 * <p>Description:考核实施/目标卡制定 引入绩效指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-01 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ImportPerPointTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String itemid=(String)hm.get("itemid");
			String importPoint_value=(String)this.getFormHM().get("importPoint_value");
			String objCode=(String)this.getFormHM().get("objCode");
			String planid=(String)this.getFormHM().get("planid");
			String body_id="";
			String model="2";
			
			String object_id="";
			if("p".equalsIgnoreCase(objCode.substring(0, 1)))
				object_id=objCode.substring(1);
			else if("um".equalsIgnoreCase(objCode.substring(0, 2))|| "un".equalsIgnoreCase(objCode.substring(0, 2)))
				object_id=objCode.substring(2);
			
			String a_p0400="";
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id);
			bo.importPerPoint(importPoint_value,"2",itemid,a_p0400);
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			String[] temps=importPoint_value.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].length()==0)
					continue;
				String[] temp=temps[i].split("``");
				String p0401=temp[0];
				String sql="update p04 set itemtype=2 where plan_id="+planid+" and item_id='"+itemid+"' and p0401='"+p0401+"' ";
				if ("p".equalsIgnoreCase(objCode.substring(0, 1)))
					sql += " and a0100='" + object_id + "'";
				else if("um".equalsIgnoreCase(objCode.substring(0, 2))|| "un".equalsIgnoreCase(objCode.substring(0, 2)))
					sql += " and b0110='" + object_id + "'";
				
				dao.update(sql);				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
