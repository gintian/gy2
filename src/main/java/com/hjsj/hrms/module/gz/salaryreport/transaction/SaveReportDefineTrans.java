package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:SaveReportDefineTrans.java</p>
 * <p>Description>:保存薪资报表定义</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 21, 2016 10:17:02 AM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class SaveReportDefineTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String actionType=(String)this.getFormHM().get("actionType");
			if("saveDefine".equalsIgnoreCase(actionType)) {
				String salaryReportName = (String) this.getFormHM().get("salaryReportName");
				String isPrintWithGroup = (String) this.getFormHM().get("isPrintWithGroup");
				String isGroup = (String) this.getFormHM().get("isGroup");
				String f_groupItem = (String) this.getFormHM().get("f_groupItem");
				String s_groupItem = (String) this.getFormHM().get("s_groupItem");
				String reportStyleID = (String) this.getFormHM().get("reportStyleID");
				String reportDetailID = (String) this.getFormHM().get("reportDetailID");
				ArrayList rightList = (ArrayList) this.getFormHM().get("right_fields");
				String ownerType = (String) this.getFormHM().get("ownerType");
				String salaryid = (String) this.getFormHM().get("salaryid");
				salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
				HashMap itemOrderMap = new HashMap();

				if ("0".equals(s_groupItem))
					s_groupItem = "";
				SalaryReportBo bo = new SalaryReportBo(this.getFrameconn(), salaryid, this.userView);
				reportDetailID = bo.saveOrUpdateRecord(reportStyleID, reportDetailID, f_groupItem, s_groupItem, isPrintWithGroup, salaryReportName, rightList, itemOrderMap, isGroup, ownerType, this.userView);

				this.getFormHM().put("salaryReportName", salaryReportName);
				this.getFormHM().put("reportDetailID", reportDetailID);
			}
			//添加常用报表
			else if("addCommon".equalsIgnoreCase(actionType)){

				String rsid = (String) this.getFormHM().get("reportStyleID");
				String tabid = (String) this.getFormHM().get("tabid");
				String salaryid = (String) this.getFormHM().get("salaryid");
				//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
				String model = (String) this.getFormHM().get("model");
				SalaryTemplateBo salaryTemplateBo=new SalaryTemplateBo(this.getFrameconn(),this.getUserView());
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("rsid",Integer.parseInt(rsid));
				if("4".equals(rsid)){
					bean.set("tabid",this.getUserView().getUserName());
				}else{
					bean.set("tabid",tabid);
				}
				bean.set("model",model);
				salaryTemplateBo.addCommon_report(Integer.parseInt(salaryid),bean);

			}
			//取消常用报表
			else if("delCommon".equalsIgnoreCase(actionType)){
				String rsid = (String) this.getFormHM().get("reportStyleID");
				String tabid = (String) this.getFormHM().get("tabid");
				String salaryid = (String) this.getFormHM().get("salaryid");
				//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
				String model = (String) this.getFormHM().get("model");
				SalaryTemplateBo salaryTemplateBo=new SalaryTemplateBo(this.getFrameconn(),this.getUserView());
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("rsid",Integer.parseInt(rsid));
				if("4".equals(rsid)){
					bean.set("tabid",this.getUserView().getUserName());
				}else{
					bean.set("tabid",tabid);
				}
				bean.set("model",model);
				salaryTemplateBo.delCommon_report(Integer.parseInt(salaryid),bean);

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
