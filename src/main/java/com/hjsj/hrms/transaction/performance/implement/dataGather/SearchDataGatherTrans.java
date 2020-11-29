package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 *<p>Title:SearchDataGatherTrans.java</p> 
 *<p>Description:初始化数据采集页面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 19, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class SearchDataGatherTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			/*
			String manage_id="-1";
			if(!this.userView.isSuper_admin())
			{
				ContentDAO dao = new ContentDAO(this.frameconn);
				manage_id=pb.getPrivCode(this.getUserView());
				if(!manage_id.equals("-1"))
				{
					if(AdminCode.getCode("UM",manage_id)!=null)
					{
						while(true)
						{
							this.frowset=dao.search("select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+manage_id+"')");
							if(this.frowset.next())
							{
								if(this.frowset.getString("codesetid").equalsIgnoreCase("UN"))
								{
									manage_id=this.frowset.getString("codeitemid");
									break;
								}
								else
									manage_id=this.frowset.getString("codeitemid");
							}
						}
					}
				}
			}
			
			ArrayList planList=pb.getGatherPlanList(manage_id);                         //考核计划列表
			*/
					
			String planId=(String)this.getFormHM().get("planId");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			if(hm.get("plan_id")!=null)
			{
				planId=(String)hm.get("plan_id");
		/*		int n=0;
				for(int i=0;i<planList.size();i++)
				{
					CommonData d=(CommonData)planList.get(i);
					if(d.getDataValue().equals(planId))
						n++;
				}
				if(n==0)
					planId="";*/
				hm.remove("plan_id");
			}			
			if(planId!=null && planId.trim().length()>0 && "~".equalsIgnoreCase(planId.substring(0,1))) // JinChunhai 2012-08-07 如果是通过转码传过来的需解码
	        { 
				String _temp = SafeCode.decode(planId);;
				planId = _temp.substring(1); 
	        }			
			
			/*
			if((planId==null||planId.length()==0)&&planList.size()>0)
			{
				planId=((CommonData)planList.get(0)).getDataValue();	  //考核计划
			}
			else if(planId!=null&&planId.length()>0&&planList.size()>0)
			{
				int n=0;
				for(int i=0;i<planList.size();i++)
				{
					CommonData d=(CommonData)planList.get(i);
					if(d.getDataValue().equals(planId))
						n++;
				}
				if(n==0)
					planId=((CommonData)planList.get(0)).getDataValue();	  //考核计划
			}*/
			
			RecordVo vo=pb.getPerPlanVo(planId);
			
			this.getFormHM().put("fromUrl", (String)hm.get("fromUrl"));  // 0:绩效实施模块进入  1:菜单进入
			if(vo!=null)
				this.getFormHM().put("gather_type",String.valueOf(vo.getInt("gather_type")));
			this.getFormHM().put("planId",planId);
			this.getFormHM().put("planList",new ArrayList());
	//		this.getFormHM().put("planList",planList);
			this.getFormHM().put("gradeHtml", "");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
