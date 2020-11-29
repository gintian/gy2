package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
/**
 * 
 *<p>Title:TimingSearch.java</p> 
 *<p>Description:这个类是一个用来扫描人员、机构、岗位信息
 *与人员变动表(t_hr_view)、机构信息变动表(t_org_view)、
 *岗位信息变动表(t_post_view)是否相同，如果不同，将按照人员、
 *岗位、机构表更新变动表内容。</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 8, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class TimingSearch implements Job {
	// 日志
	private Category cat = Category.getInstance(TimingSearch.class);
	
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		// 作业类id
		String jobId = context.getJobDetail().getName();
		// TODO Auto-generated method stub
		Connection conn=null;
		try {
			conn = (Connection) AdminDb.getConnection();
			HrSyncBo hsb = new HrSyncBo(conn);
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	    	if(uplevel==null||uplevel.length()==0) {
				uplevel="0";
			}
	    	int nlevel=Integer.parseInt(uplevel);
	    	if(nlevel>0)
	    	{
	    		hsb.setNlevel(nlevel);
	    		hsb.uplevelDeptTable("dept_table");
	    	}
	    	hsb.createGuidkey();//初始化A01 ORGANIZATION表中添加GUIDKEY字段
			hsb.operUserSynchronization();//业务人员
			hsb.organizationSynchronization();//组织机构视图
			String fields = hsb.getTextValue(HrSyncBo.FIELDS);
			//hsb.setCodefields(codefields);
			if(fields==null) {
				fields = "";
			}
			hsb.setCodefields(fields);
			String codefields = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
			if(codefields==null) {
				codefields = "";
			}
			hsb.setCodefields(codefields);
			hsb.empSynchronization(fields);
			//同步单位部门
			String orgfields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
			if(orgfields==null) {
				orgfields = "";
			}
			hsb.setOrgcodefields(orgfields);
			String orgcodefields = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
			if(orgcodefields==null) {
				orgcodefields = "";
			}
			hsb.setOrgcodefields(orgcodefields);
			hsb.orgSynchronization(orgfields);
			//职位
			String postfields = hsb.getTextValue(HrSyncBo.POST_FIELDS);
			if(postfields==null) {
				postfields = "";
			}
			hsb.setPostcodefields(postfields);
			String postcodefields = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);
			if(postcodefields==null) {
				postcodefields = "";
			}
			hsb.setPostcodefields(postcodefields);
			hsb.postSynchronization(postfields);
			
			
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		finally
		{
		   try
		   {
			if(conn!=null) {
				conn.close();
			}
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
		   }
				
		}
		
		// 执行发送的类
		try {
			SysoutSyncJob job =new SysoutSyncJob();
			job.execute(context);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
