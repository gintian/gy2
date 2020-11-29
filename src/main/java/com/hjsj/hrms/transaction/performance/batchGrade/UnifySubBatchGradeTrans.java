package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Hashtable;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:统一提交多人打分</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 27, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class UnifySubBatchGradeTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String planIDs=(String)this.getFormHM().get("planIDs");
			String noFinishedPlanID=(String)this.getFormHM().get("noFinishedPlanID");
			String tableFlag=(String)this.getFormHM().get("tableFlag");
			String objectivePlan=(String)this.getFormHM().get("objectivePlan");
			String[] _planIDs=planIDs.split(",");
			LoadXml loadxml=null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet = null;
			
			
			String _info="";
			if(SystemConfig.getPropertyValue("investigation_id")!=null&&SystemConfig.getPropertyValue("investigation_id").trim().length()>0)
			{
				if(SystemConfig.getPropertyValue("visibleUserName")!=null&& "False".equalsIgnoreCase(SystemConfig.getPropertyValue("visibleUserName").trim()))
				{
				
			//		if(this.getUserView().isHaveResource(IResourceConstant.INVEST,SystemConfig.getPropertyValue("investigation_id").trim())||this.getUserView().isAdmin())
					{
						if(!validateIsSubInvestigation(SystemConfig.getPropertyValue("investigation_id").trim(),this.userView.getUserName()))
						{
							throw GeneralExceptionHandler.Handle(new Exception("您的调查问卷没有提交,无法提交！"));
						}
					}
				}
			}
			
			if(noFinishedPlanID!=null&&noFinishedPlanID.trim().length()>0)
			{
				rowSet=dao.search("select name from per_plan where plan_id in ("+noFinishedPlanID+")");
				if(rowSet.next())
					throw GeneralExceptionHandler.Handle(new Exception("计划: "+rowSet.getString("name")+" 您没执行完成操作,无法提交！"));
				
			}
			
			if(tableFlag!=null&&!"2".equals(tableFlag.trim()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("您的民主推荐表没执行完成操作,无法提交！"));
			}
			
			
			
			
			for(int i=0;i<_planIDs.length;i++)
			{
				if(_planIDs[i].trim().length()==0)
					continue;
				String planid=_planIDs[i];
				StringBuffer sql=new StringBuffer("select  pp.name from per_mainbody pm ,per_plan pp where pm.plan_id=pp.plan_id and ");
				sql.append(" pm.plan_id="+planid+" and pm.status<>8  and pm.status<>2   and pm.status<>4  and pm.status<>7  and pm.mainbody_id='"+this.userView.getA0100()+"'");
				if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
				{
					loadxml = new LoadXml(this.getFrameconn(),planid);
					BatchGradeBo.getPlanLoadXmlMap().put(planid,loadxml);
				}
				else
					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
				Hashtable htxml = loadxml.getDegreeWhole();
				String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 多人打分时同时显示自我评价
				if ("False".equalsIgnoreCase(mitiScoreMergeSelfEval))
						sql.append(" and pm.object_id<>'" + this.userView.getA0100() + "'");
				
				rowSet=dao.search(sql.toString());
				if(rowSet.next())
				{
					throw GeneralExceptionHandler.Handle(new Exception("计划:"+rowSet.getString("name")+"没有执行打分完成操作,提交失败!"));
				}
			}
			if(objectivePlan!=null&&!"".equals(objectivePlan)&&!"-1".equals(objectivePlan))
			{
				String[] arr=objectivePlan.split(",");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					StringBuffer sql = new StringBuffer("");
					sql.append("select po.a0101 from per_mainbody pm,per_object po where pm.plan_id=po.plan_id and ");
					sql.append(" pm.object_id=po.object_id and pm.plan_id="+arr[i]);
					sql.append(" and pm.mainbody_id='"+this.userView.getA0100()+"'");
					sql.append(" and pm.status<>8 and pm.status<>2");
					this.frowset=dao.search(sql.toString());
					while(this.frowset.next())
					{
						RecordVo vo = new RecordVo("per_plan");
						vo.setInt("plan_id", Integer.parseInt(arr[i]));
						vo =dao.findByPrimaryKey(vo);
						throw GeneralExceptionHandler.Handle(new Exception("考核计划:"+vo.getString("name")+"中考核对象"+this.frowset.getString("a0101")+"还没有执行打分完成操作,提交失败!"));
					}
				}
			}
			
			for(int i=0;i<_planIDs.length;i++)
			{
				if(_planIDs[i].trim().length()==0)
					continue;
				String planid=_planIDs[i];
				String sql="update per_mainbody set status=2 where plan_id="+planid+"   and status<>4  and status<>7  and   status<>2 and mainbody_id='"+this.userView.getA0100()+"'";
				if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
				{
					loadxml = new LoadXml(this.getFrameconn(),planid);
					BatchGradeBo.getPlanLoadXmlMap().put(planid,loadxml);
				}
				else
					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
				Hashtable htxml = loadxml.getDegreeWhole();
				String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 多人打分时同时显示自我评价
				if ("False".equalsIgnoreCase(mitiScoreMergeSelfEval))
						sql+=" and object_id<>'" + this.userView.getA0100() + "'";
				dao.update(sql);
				sql="update per_mainbody set status=7 where plan_id="+planid+"   and    status=4 and mainbody_id='"+this.userView.getA0100()+"'";
				if ("False".equalsIgnoreCase(mitiScoreMergeSelfEval))
					sql+=" and object_id<>'" + this.userView.getA0100() + "'";
				dao.update(sql);
			}
		   
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			if (dbWizard.isExistTable("per_recommend_result", false))
				dao.update("update per_recommend_result set flag=2 where a0100='"+userView.getA0100()+"' and upper(nbase)='"+userView.getDbname().toUpperCase()+"' ");
			if(objectivePlan!=null&&!"".equals(objectivePlan))
			{
				String[] arr=objectivePlan.split(",");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					StringBuffer sql = new StringBuffer("");
					sql.append("update per_mainbody  set status=2");
					sql.append(" where plan_id="+arr[i]);
					sql.append(" and mainbody_id='"+this.userView.getA0100()+"'");
					dao.update(sql.toString());
				}
			}
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	
	/**
     * 判断当前用户是否提交调查报告
     * @param id
     * @param username
     * @return
     */
    public boolean validateIsSubInvestigation(String id,String username)
    {
    	boolean flag=false;
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		RowSet     frowset=null;
    		StringBuffer sb = new StringBuffer();
    		sb.append("select itemid from investigate_content where staff_id='");
    		sb.append(username);
    		sb.append("' and itemid in (select itemid from investigate_item where id='");
    		sb.append(id);
    		sb.append("') union select itemid from investigate_result where staff_id='");
    		sb.append(username);
    		sb.append("' and itemid in (select itemid from investigate_item where id='");
    		sb.append(id);
    		sb.append("')");
    		frowset=dao.search(sb.toString());
    		if(frowset.next())
    			flag=true;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }

}
