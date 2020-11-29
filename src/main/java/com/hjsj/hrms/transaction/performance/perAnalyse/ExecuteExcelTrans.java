package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 *<p>Title:ExecuteExcelTrans.java</p> 
 *<p>Description:绩效分析导出Excel</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 18, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ExecuteExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String model=(String)this.getFormHM().get("model");  //11:统计分析   1:单指标趋势分析 2：多指标趋势分析 3：单人对比分析 4：综合评测标 5：多人对比分析 6：主体分类对比分析(单考核对象) 10主体分类对比分析(多考核对象)   7：了解程度对比分析 8：评语 9：选票统计
			String picName=(String)this.getFormHM().get("picName");
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			String fileName = "";
			if("1".equals(model))
			{
				fileName =  bo.getExcelName(picName,new ArrayList());
			}
			else if("2".equals(model)|| "3".equals(model)|| "5".equals(model)|| "6".equals(model)|| "7".equals(model))
			{
				ArrayList pointToNameList=(ArrayList)this.getUserView().getHm().get("pointToNameList");
				if(pointToNameList==null)
					pointToNameList=new ArrayList();
				if("6".equals(model)|| "7".equals(model))
				{
					ArrayList list=new ArrayList();
					for(int i=0;i<pointToNameList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)pointToNameList.get(i);
						list.add(new CommonData((String)abean.get("point_id"),(String)abean.get("pointname")));
					}
					pointToNameList=list;
				}
				fileName =  bo.getExcelName(picName,pointToNameList);
			}
			else if("11".equals(model))
			{
				String planid=(String)this.getFormHM().get("planid");
				singleGradeBo_new bb=new singleGradeBo_new(this.getFrameconn(),planid,this.getUserView());
				fileName =  bb.getPerAnalyseStatExcel(planid);
			}
			else if("9".equals(model))
			{
				String codeitemid=(String)this.getFormHM().get("codeitemid");
				String planid=(String)this.getFormHM().get("planid");
				singleGradeBo_new bb=new singleGradeBo_new(this.getFrameconn(),planid,this.getUserView());
				fileName =  bb.getPerVoteStatExcel(planid, codeitemid);
			}
			fileName = PubFunc.encrypt(fileName);
			fileName = SafeCode.encode(fileName);	
			this.getFormHM().put("filename",fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
