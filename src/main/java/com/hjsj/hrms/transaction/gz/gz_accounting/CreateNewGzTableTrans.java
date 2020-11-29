/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:CreateNewGzTableTrans</p> 
 *<p>Description:新建薪资表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-28:上午11:19:14</p> 
 *@author cmq
 *@version 4.0
 */
public class CreateNewGzTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		/**年份*/
		String year=(String)this.getFormHM().get("year");
		/**月份*/
		String month=(String)this.getFormHM().get("month");
	 	String gz_module=(String)this.getFormHM().get("gz_module");  //=0 薪资 =1 保险
		try
		{
			/**薪资类别*/
	//		long aa=System.currentTimeMillis();
	//		System.out.println(this.userView.getUserName()+"--start");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			
			//部门奖金模块用到
			String from_module=(String)this.getFormHM().get("from_module");
			this.getFormHM().remove("from_module");
			if(from_module!=null&& "monthPremium".equalsIgnoreCase(from_module))
				gzbo.setFrom_module(from_module);
			
			//新建工资同月不能多次  -----北京移动
			if(SystemConfig.getPropertyValue("noManyTimes_gzPlay")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noManyTimes_gzPlay")))
			{
				if(isExistsByMonth(salaryid,year,month))
				{
					throw GeneralExceptionHandler.Handle(new Exception("因设置了每月仅发一次薪资，本月薪资表已创建，不允许再新建!"));
				}
			}
			
			//新建工资表自动变动比对,其实就是不取上月数据  -----北京移动
			if(from_module==null||!"monthPremium".equalsIgnoreCase(from_module))
			{
				if(SystemConfig.getPropertyValue("noPreData_gz")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noPreData_gz").trim()))
				{
					gzbo.setFrom_module("noPreData");
				}
			}
			
			this.getFormHM().put("ff_bosdate", year+"-"+month);
			
			gzbo.createNewGzTable(year, month);
			this.getFormHM().put("salaryid", salaryid);
	//		System.out.println(this.userView.getUserName()+"--end  "+(System.currentTimeMillis()-aa));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 工资类别 ＸＸＸ　在当前时间是否已新建
	 * @param salaryid
	 * @param year
	 * @param month
	 */
	private boolean isExistsByMonth(String salaryid,String year,String month)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			buf.append("select * from gz_extend_log");
			buf.append(" where salaryid=");
			buf.append(salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userView.getUserName().toUpperCase());
			buf.append("'");
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(year+"-"+month+"-1"));		
			buf.append(" and A00Z3=1");
			this.frowset=dao.search(buf.toString());
			if(this.frowset.next())
				flag=true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
}
