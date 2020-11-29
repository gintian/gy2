/*
 * 创建日期 2005-7-4
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 * 得到用户对象Id
 */
public class GetObjectIdTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planFlag=(String)hm.get("planFlag");		//2：本人考核结果  1：员工考核结果 
		String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测
		this.getFormHM().put("model",model);
		String objectId="0";
		if(hm.get("objectId")!=null)
		{
			this.getFormHM().put("objectId",hm.get("objectId").toString());
			objectId=hm.get("objectId").toString();
		}
		else
		{
						
			this.getFormHM().put("objectId","0");
		}
		String sql=" select plan_id,name from per_plan where status=?  and plan_id in (select plan_id from per_mainbody where object_id='"+objectId+"')";
		
		String sql2=" select plan_id,name from per_plan where status=?  and plan_id in (select plan_id from per_mainbody where object_id='"+this.userView.getA0100()+"')";
		this.getFormHM().put("selfStr",sql2);
		
		this.getFormHM().put("objectIdStr",sql);
		
		try
		{
			ArrayList list=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(planFlag!=null&& "2".equals(planFlag))
				this.frowset=dao.search(" select plan_id,name,parameter_content from per_plan where status=7  and plan_id in (select plan_id from per_mainbody where object_id='"+this.userView.getA0100()+"')");
			else
				this.frowset=dao.search(" select plan_id,name,parameter_content from per_plan where status=7  and plan_id in (select plan_id from per_mainbody where object_id='"+objectId+"')");
			
			 CommonData vo1=new CommonData("#","请选择");
			 list.add(vo1);
	       	 LoadXml loadXml=new LoadXml();
	          while(this.frowset.next())
	          {
	                String name=this.getFrowset().getString("name");
	                String plan_id=this.getFrowset().getString("plan_id");
	                String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
	                String performanceType=loadXml.getPerformanceType(xmlContent);
	                if(model.equals(performanceType))
	                {
	                	CommonData vo=new CommonData(plan_id,name);
	                	list.add(vo);
	                }
	           }
	          this.getFormHM().put("planList",list);
	          
	        
	          
	          
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		/*
		if(hm.get("companyName")!=null)
		{
			
			try
			{
				this.getFormHM().put("companyName",PubFunc.ToGbCode(hm.get("companyName").toString()));
			
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
		else
		{
			this.getFormHM().put("companyName","");
		}
		*/
		String companyId="0";
		if(hm.get("companyId")!=null)
		{
			    this.getFormHM().put("companyId",hm.get("companyId").toString());
				companyId=hm.get("companyId").toString();				
				if(AdminCode.getCode("UN",companyId)==null||AdminCode.getCode("UN",companyId).getCodename()==null || "".equals(AdminCode.getCode("UN",companyId).getCodename().toString()))
				{
					this.getFormHM().put("companyName","");
				}
				else
				{
					this.getFormHM().put("companyName",AdminCode.getCode("UN",companyId).getCodename().toString());
				}
		}
		else
		{
			this.getFormHM().put("companyId","");
			this.getFormHM().put("companyName","");
		}
		
		//System.out.println("---->com.hjsj.hrms.transaction.statistic.GetObjectIdTrans-->"+companyId);	
		
		
		/**
		 * 图形与表格标识
		 */
		//this.getFormHM().put("drawingFlag","1");
		//hm.put("picFlag","0");
		//this.getFormHM().put("requestPamaHM",hm);

	}

}
