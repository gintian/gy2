package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetGzReportParamTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)hm.get("salaryid");
			salaryid=salaryid!=null?salaryid:"";
			String gz_module=(String)hm.get("gz_module");
			gz_module=gz_module!=null?gz_module:"";
			String condid=(String)hm.get("condid");
			condid=condid!=null?condid:"";
			String a_code=(String)hm.get("a_code");
			String filterSql = (String) this.userView.getHm().get("gz_filterWhl");
			String noManagerFilterSql = (String) this.userView.getHm().get("noManagerFilterSql");
			String model=(String)hm.get("model");//薪资发放 0；历史数据分析 3
			this.userView.getHm().put("gzmodel", model);
			String count="";
			String bosdate="";
			if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
			{
				count=(String)hm.get("count");
				bosdate=(String)hm.get("bosdate");
			}
			this.getFormHM().put("gz_module",gz_module);
			//String dd=SafeCode.decode(filterSql);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("condid",condid);
			this.getFormHM().put("a_code",a_code);
			/* 安全问题 sql-in-url(这个已经存放到了userView中) xiaoyun 2014-9-17 start */
			// this.getFormHM().put("filterWhl", filterSql);
			/* 安全问题 sql-in-url(这个已经存放到了userView中) xiaoyun 2014-9-17 end */
			this.getFormHM().put("noManagerFilterSql", noManagerFilterSql);
			this.getFormHM().put("boscount",count);
			this.getFormHM().put("bosdate",bosdate);
			this.getFormHM().put("model",model);
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
