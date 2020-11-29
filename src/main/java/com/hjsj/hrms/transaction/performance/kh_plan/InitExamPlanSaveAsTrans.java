package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:InitExamPlanSaveAsTrans.java</p>
 * <p>Description:初始化另存考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class InitExamPlanSaveAsTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String busitype = (String) hm.get("busitype");
		String status = (String) hm.get("status");
		String planId = (String) hm.get("planId");
		planId = planId.replaceAll("／", "/");
		hm.remove("planId");
		
		String[] planIds = null;
		String[] status_s = null;
		if(planId!=null && planId.trim().length()>0)
		{
			planId = planId.substring(0, planId.length() - 1);
			planIds = planId.split("/");	
		}
		if(status!=null && status.trim().length()>0)
		{
			status = status.substring(0, status.length() - 1);
			status_s = status.replaceAll("／", "/").split("/");
		}		
		String signDesc = "(选此项,不能更换考核模板)";
		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			signDesc = "(选此项,不能更换测评量表)";
				
		// 另存单个考核计划
		if(planIds!=null && planIds.length==1)
		{			
			DbWizard dbWizard = new DbWizard(this.frameconn);		
			if("0".equals(status))
			    status="起草";
			else if("1".equals(status))
			    status="报批";
			else if("2".equals(status))
			    status="已批";
			else if("3".equals(status))
			    status="已发布";
			else if("4".equals(status))
			    status="正在执行";
			else if("5".equals(status))
			    status="暂停";
			else if("6".equals(status))
			    status="评估";
			else if("7".equals(status))
			    status="结束";
			else
			    status="";
			// 复制基本信息
			String copy_self = "1";
			// 复制考核主体类别
			String copy_khmainbodytype = "0";
			// 复制考核对象
			String copy_khobject = "0";
			// 复制考核主体
			String copy_khmainbody = "0";
			// 复制考核主体的指标权限
			String copy_khmainbody_pri = "0";
		
			if(isExistBodyType(planId))
			    copy_khmainbodytype = "1";	
			
			if(isExistKhObject(planId))
			    copy_khobject = "1";	
			
			if(isExistKhMainbody(planId))
			    copy_khmainbody = "1";
			
			if ("1".equals(copy_khobject) && "1".equals(copy_khmainbody))
			{
				 boolean flag=false;
				 ContentDAO dao = new ContentDAO(this.getFrameconn());	
				 try 
				 {
					 if(dbWizard.isExistTable("per_pointpriv_" + planId, false))
					 {
						 StringBuffer strsql = new StringBuffer();
						 strsql.append("SELECT * FROM per_pointpriv_"+planId);
						 this.frowset = dao.search(strsql.toString());
						 if (this.frowset.next()) 
							flag = true;
					 }
					 if(dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
					 {
						 StringBuffer strsql = new StringBuffer();
						 strsql.append("SELECT * FROM PER_ITEMPRIV_"+planId);
						 this.frowset = dao.search(strsql.toString());
						 if (this.frowset.next()) 
							flag = true;
					 }
					 
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				if(flag)
					copy_khmainbody_pri = "1";
			}		    
			
			this.getFormHM().put("copy_khmainbodytype",copy_khmainbodytype);
			this.getFormHM().put("copy_self",copy_self);
			this.getFormHM().put("copy_khobject",copy_khobject);
			this.getFormHM().put("copy_khmainbody",copy_khmainbody);
			this.getFormHM().put("copy_khmainbody_pri",copy_khmainbody_pri);	
			
			String copy_khmainbody_pri_title = "复制考核主体的指标权限,动态指标权重,项目权限<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+signDesc;
			PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn());
			String method = bo.getPlanVo(planId).getString("method");
			if ("2".equals(method))
			{
				String template_id =  bo.getPlanVo(planId).getString("template_id");
				
				if(template_id!=null && template_id.trim().length()>0)
				{												
					RecordVo templateVo=bo.getTemplateVo(template_id);
					String templateStatus=templateVo!=null?templateVo.getString("status"):"0";
					if("1".equals(templateStatus))
						 copy_khmainbody_pri_title = "复制考核主体的指标权限,动态指标权重,动态项目权重,项目权限<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+signDesc;
					else
						 copy_khmainbody_pri_title = "复制考核主体的指标权限,动态指标权重,动态项目分值,项目权限<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+signDesc;
				}else
					copy_khmainbody_pri_title = "复制考核主体的指标权限,动态指标权重,动态项目分值/权重,项目权限<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+signDesc;
			}
		    this.getFormHM().put("copy_khmainbody_pri_title", copy_khmainbody_pri_title);	
			
			this.getFormHM().put("copyResultStr","源计划："+getPlanName(planId)+"\n状态  ："+status+"\n------------------------------------------------");
    
		}else if(planIds!=null && planIds.length>0) // 同时另存多个考核计划
		{			
			this.getFormHM().put("copy_khmainbodytype","1");
			this.getFormHM().put("copy_self","1");
			this.getFormHM().put("copy_khobject","1");
			this.getFormHM().put("copy_khmainbody","1");
			this.getFormHM().put("copy_khmainbody_pri","1");
			
			String copy_khmainbody_pri_title = "复制考核主体的指标权限,动态指标权重,动态项目分值/权重,项目权限<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+signDesc;			
			this.getFormHM().put("copy_khmainbody_pri_title", copy_khmainbody_pri_title);
			this.getFormHM().put("copyResultStr","");
		}
		
    }

    /**
     * 是否考核主体类别可选
     * @throws GeneralException   
     */
    public boolean isExistBodyType(String planId) throws GeneralException
    {
		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select  plan_id from per_plan_body where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				flag = true;
		} catch (SQLException e) 
		{
			throw new GeneralException("查询数据异常！");
		}	
		return flag;	
    }
    /**
     * 是否考核主体的指标权限可选
     */
    public boolean isExistKhMainbodyPri(String planId) 
    {
		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT name FROM sysobjects WHERE name = 'per_pointpriv_"+planId+"' AND type = 'U ' ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				flag = true;
			
		} catch (SQLException e) 
		{
			flag=false;
		}	
		return flag;	
    }
    
    /**
     * 是否考核主体可选
     * @throws GeneralException 
     */
    public boolean isExistKhMainbody(String planId) throws GeneralException
    {
		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_mainbody where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				flag = true;
			
		} catch (SQLException e) 
		{
			throw new GeneralException("查询数据异常！");
		}	
		return flag;	
    }
    /**
     * 是否考核对象可选
     * @throws GeneralException 
     */
    public boolean isExistKhObject(String planId) throws GeneralException
    {
		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_object where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				flag = true;
			
		} catch (SQLException e) 
		{
			throw new GeneralException("查询数据异常！");
		}	
		return flag;	
    }
    
    public String getPlanName(String planId) throws GeneralException
    {
		String name="";
		StringBuffer strsql = new StringBuffer();
		strsql.append("select  name from per_plan where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				name = this.frowset.getString("name");
			
		} catch (SQLException e) 
		{
			throw new GeneralException("查询数据异常！");
		}	
		return name;
    }
    
}
