package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:校验　任务的权重或分值是否超过项目</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ValidateRankOrScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String item_id=(String)this.getFormHM().get("item_id");
			String status=(String)this.getFormHM().get("status");
			String p0400=(String)this.getFormHM().get("p0400");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String type=(String)this.getFormHM().get("type");   //1。保存 2。保存继续
			String itemType=(String)this.getFormHM().get("itemType"); ////编辑的目标的项目属性  1:共性  2：个性
			String object_id=(String)this.getFormHM().get("object_id");
			String plan_objectType=(String)this.getFormHM().get("plan_objectType");
			String item_type=(String)this.getFormHM().get("item_type");//=1加扣分指标，是在评分时维护=0普通指标
			String isTraceOrMust=(String)this.getFormHM().get("isTraceOrMust");//附件指标是否是跟踪指标，必填指标，=0什么也不是=1是跟中=2是必填，=3即使跟踪又是必填
			String objectSpFlag=(String)this.getFormHM().get("objectSpFlag");
			String AllowLeaderTrace=(String)this.getFormHM().get("AllowLeaderTrace");
			LoadXml loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);	
			boolean flag = false;
			if(loadxml.getDegreeWhole().get("ProcessNoVerifyAllScore")!=null && "true".equalsIgnoreCase((String)loadxml.getDegreeWhole().get("ProcessNoVerifyAllScore")))
				flag = true;
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView());
			int planStatus=bo.getPlan_vo().getInt("status");
			if(bo.getPlan_vo().getInt("object_type")==1||bo.getPlan_vo().getInt("object_type")==3||bo.getPlan_vo().getInt("object_type")==4)
			{
				object_id=bo.getUn_functionary();  
			} 
			if("2".equals(itemType)&&flag)
			{
				String str="";
				if("True".equalsIgnoreCase((String)bo.getPlanParam().get("TaskSupportAttach"))) //任务支持附件上传
				 {
					if("2".equals(isTraceOrMust)&&("01".equals(objectSpFlag)|| "07".equals(objectSpFlag)|| "02".equals(objectSpFlag)))
					{
						str=this.isHaveAttach(plan_id, object_id, p0400);
					}
					else if(("3".equals(isTraceOrMust))&&(("03".equals(objectSpFlag)&&planStatus==8)||("03".equals(objectSpFlag)&& "true".equalsIgnoreCase(AllowLeaderTrace))))
		    		{
						str=this.isHaveAttach(plan_id, object_id, p0400);
		    		}
				 }
		 		this.getFormHM().put("info",str);
			}
			else{
				String str="";
				if("True".equalsIgnoreCase((String)bo.getPlanParam().get("TaskSupportAttach"))) //任务支持附件上传
				{

					if("2".equals(isTraceOrMust)&&("01".equals(objectSpFlag)|| "07".equals(objectSpFlag)|| "02".equals(objectSpFlag)))
					{
						str=this.isHaveAttach(plan_id, object_id, p0400);
					}
					else if(("3".equals(isTraceOrMust))&&(("03".equals(objectSpFlag)&&planStatus==8)||("03".equals(objectSpFlag)&& "true".equalsIgnoreCase(AllowLeaderTrace))))
		    		{
						str=this.isHaveAttach(plan_id, object_id, p0400);
		    		}
				}
				this.getFormHM().put("info",str);
			}
			
			this.getFormHM().put("type",type);
			this.getFormHM().put("item_type", item_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public String isHaveAttach(String planid,String a_objectID,String _p0400)
	{
		String str="";
		try
		{
			StringBuffer strsql=new StringBuffer("select * from per_article  where plan_id="+planid+" and a0100='"+a_objectID+"' " );
			strsql.append(" and lower(nbase)='usr' and task_id="+_p0400+"  and article_type=3 order by Article_id");  
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql.toString());
			boolean flag=false;
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==2) 
				{
					flag=true;
					break;
				}
			}
			if(!flag)
			{
				str="附件为必填项！";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

}
