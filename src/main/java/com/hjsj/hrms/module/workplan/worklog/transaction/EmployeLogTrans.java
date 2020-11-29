package com.hjsj.hrms.module.workplan.worklog.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.workplan.worklog.businessobject.EmployeLogBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

public class EmployeLogTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
			try {
				EmployeLogBo bo=new EmployeLogBo(this.userView,this.frameconn);
				
				ArrayList<ButtonInfo> btnlist=new ArrayList<ButtonInfo>();
				ButtonInfo btn=new ButtonInfo("栏目设置","");//栏目设置按钮
				btn.setFunctype(ButtonInfo.FNTYPE_SCHEME);
				if(this.userView.hasTheFunction("0KR02020301"))
					btnlist.add(btn);
				
				ButtonInfo searchBox = new ButtonInfo();
				searchBox.setFunctionId("WP40000002");//查询所走的交易号
				searchBox.setText("请输入姓名、拼音简称、部门名称...");//blank text
				searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
				searchBox.setShowPlanBox(false);//不显示查询方案
				btnlist.add(searchBox);
				
				TableConfigBuilder builder=new TableConfigBuilder("employlog_00001",bo.getColumnList(),"employlog",userView,this.getFrameconn());
				SimpleDateFormat sft=new SimpleDateFormat("yyyy-MM");
				String currentYear=sft.format(new Date());
				builder.setDataSql(bo.getSql(currentYear));
				builder.setTableTools(btnlist);
				builder.setAutoRender(true);
				builder.setEditable(false);//不可编辑
				builder.setConstantName("workplan/cooperationtask");  //设置导出excel
				builder.setTitle("员工工作日志");
				builder.setOrderBy("order by A0000");
				builder.setColumnFilter(true);//列过滤
				builder.setShowPublicPlan(this.userView.hasTheFunction("0KR0202030101"));
				builder.setScheme(true);
				builder.setSchemeItemKey("A01");
				String config=builder.createExtTableConfig();
				this.getFormHM().put("tableConfig", config);
				this.getFormHM().put("date", currentYear);
				//选日期之前需先传当前日期参数作为快速查询的日期条件
				this.userView.getHm().put("date", currentYear);
				
				Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		        if(uplevel==null||uplevel.length()==0)
		            uplevel="0";
		        this.getFormHM().put("uplevel", uplevel);
		        
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
	}

}
