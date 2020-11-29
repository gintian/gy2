package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *<p>Title:ShowGradeTypeTrans.java</p> 
 *<p>Description:显示等级分类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 15, 2008</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class ShowGradeTypeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			ArrayList perGradeSetList=new ArrayList();
			String perDegree="";
			String busitype=(String)this.getFormHM().get("busitype");
			String planid=(String)this.getFormHM().get("planid");
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			LoadXml loadXml=new LoadXml(this.getFrameconn(), planid);
			Hashtable params = loadXml.getDegreeWhole();
			perDegree=(String)params.get("GradeClass");					//等级分类ID
			
			perGradeSetList=pb.getPerDegreeList(perDegree,busitype);
			this.getFormHM().put("perGradeSetList",perGradeSetList);
			this.getFormHM().put("perDegree",perDegree);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
