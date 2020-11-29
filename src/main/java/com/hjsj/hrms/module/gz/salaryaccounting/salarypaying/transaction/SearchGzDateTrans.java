package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
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
 * 
 * 项目名称：hcm7.x
 * 类名称：SearchGzDateTrans 
 * 类描述：查询当前薪资类别处理到业务日期 
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 11:03:20 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 11:03:20 AM
 * 修改备注： 
 * @version
 */
public class SearchGzDateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		String opt=(String)this.getFormHM().get("opt");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		try
		{
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			String[] ym = new String[3];
			//重置业务日期
			if(opt!=null&& "reset".equalsIgnoreCase(opt))
			{
				String appdate=ConstantParamter.getAppdate(this.userView.getUserName());
				if(appdate!=null&&appdate.length()>0)
				{
					String[] temps=appdate.split("\\.");
					ym[0]=temps[0]; //年
					ym[1]=temps[1].trim();//月
					this.getFormHM().put("count", "1");
					/** 判断薪资发放记录表中是否有没提交的工资 */
					if(bo.isSalaryPayed(salaryid))
						this.getFormHM().put("salaryIsSubed", "false");
				}
			}else{
				/** 判断薪资发放记录表中是否有没提交的工资 */
				if(bo.isSalaryPayed(salaryid))
				{
					//数据未提交,不能生成新的数据表
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.cannot_create")));
				}			
				if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
					throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));			
				HashMap map=bo.getMaxYearMonthCount(null,false);
				String date=(String)map.get("ym");
				GregorianCalendar gc=new GregorianCalendar();
				gc.setTime(DateUtils.getDate(date,"yyyy-MM-dd"));
				gc.add(Calendar.MONTH, 1);
				date=DateUtils.format(gc.getTime(),"yyyy-MM-dd");
				ym=StringUtils.split(date,"-");
			}
			this.getFormHM().put("theyear", ym[0]);
			this.getFormHM().put("themonth",ym[1]);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}
}
