package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SearchTemplateFormTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String module_id = (String)this.getFormHM().get("module_id");
		String tab_id = (String)this.getFormHM().get("tab_id");//单个或者多个 模板号
		String approve_flag = (String)this.getFormHM().get("approve_flag");//任务类型
		String return_flag = (String)this.getFormHM().get("return_flag");
		String sys_type = (String)this.getFormHM().get("sys_type");
		String days = (String)this.getFormHM().get("days");//最近几天
		String start_date = (String)this.getFormHM().get("start_date");//开始时间
		String end_date = (String)this.getFormHM().get("end_date");//结束时间
		String query_type = (String) this.getFormHM().get("query_type");//按日期or按时间段 1 or other

		try{
			
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("start_date", start_date!=null?start_date:"");
			paramBean.set("end_date", end_date!=null?end_date:"");
			paramBean.set("days", days!=null?days:"");
			paramBean.set("query_type", query_type!=null?query_type:"");
			paramBean.set("tabid", tab_id!=null?tab_id:"");
			paramBean.set("module_id", module_id!=null?module_id:"");
			paramBean.set("bs_flag", approve_flag!=null?approve_flag:"");
			TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.frameconn,this.userView);
			ArrayList dataList=templatePendingTaskBo.getDBList(paramBean,this.userView);
			
			if(dataList.size()>0){
				this.getFormHM().put("temflag", "1");
				//this.getFormHM().put("tableConfig", config.toString());
				//this.getFormHM().put("templatejson", templateList);
				this.getFormHM().put("dataList", dataList);
			}else{
				this.getFormHM().put("temflag", "0");
			}
		
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
