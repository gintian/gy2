/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 *<p>Title:SearchGzDateTrans</p> 
 *<p>Description:查询当前薪资类别处理到业务日期</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-27:下午02:26:12</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGzDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		try
		{
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			hm.remove("opt");
			
			HashMap map=gzbo.getMaxYearMonthCount();
			/**yyyy-MM-dd*/
			String date=(String)map.get("ym");
			GregorianCalendar gc=new GregorianCalendar();
			gc.setTime(DateUtils.getDate(date,"yyyy-MM-dd"));
			gc.add(Calendar.MONTH, 1);
			date=DateUtils.format(gc.getTime(),"yyyy-MM-dd");
			/**发放次数*/
			//String count=(String)map.get("count");
			String[] ym=StringUtils.split(date,"-");

			if(opt!=null&& "reset".equalsIgnoreCase(opt))
			{
				String appdate=ConstantParamter.getAppdate(this.userView.getUserName());
				if(appdate!=null&&appdate.length()>0)
				{
					String[] temps=appdate.split("\\.");
					ym[0]=temps[0]; 
					ym[1]=temps[1].trim(); //String.valueOf(Integer.parseInt(temps[1]));  20150303 dengcan
					map.put("count","1");
				}
			}
			
			this.getFormHM().put("theyear", ym[0]);
			this.getFormHM().put("themonth",ym[1]);
			this.getFormHM().put("count", map.get("count"));
			this.getFormHM().put("finalDate",gzbo.getMaxYearMonth());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}

}
