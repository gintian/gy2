package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ComputeDataCollectTrans.java</p>
 * <p>Description:根据指标公式计算得分</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-08-08 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ComputeDataCollectTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		try
		{
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//			String isReCalcu = (String) hm.get("isReCalcu");
//			hm.remove("isReCalcu");
			
//			ContentDAO dao = new ContentDAO(this.frameconn);
			String point=(String) hm.get("point");
			hm.remove("point");
			this.getFormHM().put("point", point);
			String plan_id = (String)this.getFormHM().get("planId");				 
						
			DataFormulaBo bo = new DataFormulaBo(this.getFrameconn(),this.userView,plan_id);
			
			/** 考核指标 打分权限范围内的并且定义了计算公式的指标列表，统一打分指标 某计划的 */						
			ArrayList pointList = bo.getPointList(); 
			
			if(pointList!=null && pointList.size()<=20)
			{
				// 新建指标计算公式临时表并进行的一些操作				 
				bo.builtKpiFormulaTable(pointList);					
				// 根据指标计算公式计算出所有值并保存入库
				bo.getFormulaSql(pointList);	
			
			}else
			{	
				ArrayList list = new ArrayList();
				for(int i = 0; i < pointList.size(); i++)
				{	
					LazyDynaBean abean = (LazyDynaBean)pointList.get(i);	
//					String formula = (String)abean.get("formula");
					
					if(i!=0 && i%20==0)  // 每20个指标操作一次
					{
						list.add(abean);						
						// 新建指标计算公式临时表并进行的一些操作				 
						bo.builtKpiFormulaTable(list);					
						// 根据指标计算公式计算出所有值并保存入库
						bo.getFormulaSql(list);	
						
						list = new ArrayList();	
						
					}else
					{
						list.add(abean);
					}				
				}
				
				if(list!=null && list.size()>0)
				{
					// 新建指标计算公式临时表并进行的一些操作	
					bo.builtKpiFormulaTable(list);					
					// 根据指标计算公式计算出所有值并保存入库
					bo.getFormulaSql(list);	
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
