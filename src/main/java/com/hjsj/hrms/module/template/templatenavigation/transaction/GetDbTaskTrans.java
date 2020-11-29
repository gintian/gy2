package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * 
 * <p>Title:GetDbTaskTrans.java</p>
 * <p>Description>:待办任务页面数据获取（表格及查询条件）</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 10, 2016 3:08:47 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetDbTaskTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/* 模块ID
		 * 1、人事异动
		 * 2、薪资管理
		 * 3、劳动合同
		 * 4、保险管理
		 * 5、出国管理
		 * 6、资格评审
		 * 7、机构管理
		 * 8、岗位管理
		 * 9、业务申请（自助）
		 * 10、考勤管理
		 * 11、职称评审
		*/	
		String module_id=(String)this.getFormHM().get("module_id");
		String tabid = (String) this.getFormHM().get("tabid");//模板号
		String bs_flag = (String) this.getFormHM().get("bs_flag");//任务类型  任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
		String days=(String)this.getFormHM().get("days");//最近几天
		String start_date=(String)this.getFormHM().get("start_date");//开始时间
		String end_date=(String)this.getFormHM().get("end_date");//结束时间
		String query_type = (String) this.getFormHM().get("query_type");//按日期or按时间段 1 or other  
		TemplateNavigationBo bo = new TemplateNavigationBo(this.frameconn,this.userView);
		
		try{
			
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("start_date", start_date!=null?start_date:"");
			paramBean.set("end_date", end_date!=null?end_date:"");
			paramBean.set("days", days!=null?days:"");
			paramBean.set("query_type", query_type!=null?query_type:"");
			paramBean.set("tabid", tabid!=null?tabid:"");
			paramBean.set("module_id", module_id!=null?module_id:"");
			paramBean.set("bs_flag", bs_flag!=null?bs_flag:"");
			    
			
			TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.frameconn,this.userView);
			ArrayList dataList=templatePendingTaskBo.getDBList(paramBean,this.userView);
			HashSet tabidSet=(HashSet)paramBean.get("tabidSet");
			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			column = bo.getDbColumnsInfo();
			TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt("dbtask")), column, "dbtask1", userView,this.getFrameconn());
			builder.setDataList(dataList);
		// builder.setDataSql(strsql.toString());
	 	// builder.setOrderBy(orderby);
			builder.setSelectable(true);
//			builder.setColumnFilter(true);
			builder.setPageSize(20);
			builder.setTableTools(bo.getDbTaskButtons());
			String config = builder.createExtTableConfig();
			ArrayList templateList = bo.getTemplateList(tabidSet);
			this.getFormHM().put("templatejson", templateList);
			this.getFormHM().put("tableConfig", config.toString());
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

			
}
