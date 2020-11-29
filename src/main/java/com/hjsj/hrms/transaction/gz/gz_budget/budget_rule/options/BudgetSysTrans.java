package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.options;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSysTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			BudgetSysBo bo = new BudgetSysBo(this.getFrameconn(),this.userView);
			HashMap map = bo.getSysValueMap();
			
			String kindstr = (String)map.get("kindstr");//预算表分类
			String rylb_codeset = (String)map.get("rylb_codeset");//人员类别代码类
			String unitmenu = (String)map.get("unitmenu"); //归属单位指标
			String dblist = (String)map.get("dblist");//参与预算的人员库
			String dblist_name = bo.getDblistName(dblist);
			String range = (String)map.get("range");//参与预算的人员范围
			String units = (String)map.get("units");//参与预算的单位 
			String units_name = bo.getUnitsName(units);
			String createTXrecord = (String)map.get("createTXrecord");//是否生成退休记录0=否，1=是
			String txCode = (String)map.get("txCode");
			String datatoze = (String)map.get("datatoze");//预算数据到总额
			
			String ysze_set = (String)map.get("ysze_set");//预算总额子集
			String ysze_idx_menu = (String)map.get("ysze_idx_menu");//预算索引指标
			String ysze_ze_menu = (String)map.get("ysze_ze_menu");//预算总额指标
			String ysze_status_menu = (String)map.get("ysze_status_menu");//状态
			
			String ysparam_set = (String)map.get("ysparam_set");//预算参数子集
			String ysparam_idx_menu = (String)map.get("ysparam_idx_menu");//预算索引指标
			String ysparam_newmonth_menu = (String)map.get("ysparam_newmonth_menu");//新员工入职月份指标
			
			//得到所有的下拉列表
			ArrayList txrecordList = bo.getTxrecordList(rylb_codeset);
			ArrayList rylbList = bo.getRylbList();
			ArrayList unitList = bo.getUnitList();
			ArrayList budgetSetList = bo.getBudgetSetList();
			ArrayList budgetIndexList = bo.getBudgetIndexList(ysze_set);
			ArrayList budgetTotalList = bo.getBudgetTotalList(ysze_set);
			ArrayList spStatusList = bo.getSpStatusList(ysze_set);
			ArrayList budgetParamSetList = bo.getBudgetParamSetList();
			ArrayList budgetIndexFieldList = bo.getBudgetIndexFieldList(ysparam_set);
			ArrayList employeeList = bo.getEmployeeList(ysparam_set);
			
			/*put出去 **/
			//put属性
			this.getFormHM().put("txCode", txCode);
			this.getFormHM().put("kindstr", kindstr);
			this.getFormHM().put("rylb_codeset", rylb_codeset);
			this.getFormHM().put("unitmenu", unitmenu);
			this.getFormHM().put("dblist", dblist);
			this.getFormHM().put("dblist_name", dblist_name);
			this.getFormHM().put("range", range);
			this.getFormHM().put("units", units);
			this.getFormHM().put("units_name", units_name);
			this.getFormHM().put("createTXrecord", createTXrecord);
			this.getFormHM().put("datatoze", datatoze);
			
			this.getFormHM().put("ysze_set",ysze_set);//预算总额子集
			this.getFormHM().put("ysze_idx_menu",ysze_idx_menu);//预算索引指标
			this.getFormHM().put("ysze_ze_menu",ysze_ze_menu);//预算总额指标
			this.getFormHM().put("ysze_status_menu",ysze_status_menu);//状态
			
			this.getFormHM().put("ysparam_set",ysparam_set);//预算参数子集
			this.getFormHM().put("ysparam_idx_menu",ysparam_idx_menu);//预算索引指标
			this.getFormHM().put("ysparam_newmonth_menu",ysparam_newmonth_menu);//新员工入职月份指标
			//put list
			this.getFormHM().put("txrecordList", txrecordList);
			this.getFormHM().put("rylbList", rylbList);
			this.getFormHM().put("unitList", unitList);
			this.getFormHM().put("budgetSetList", budgetSetList);
			this.getFormHM().put("budgetIndexList", budgetIndexList);
			this.getFormHM().put("budgetTotalList", budgetTotalList);
			this.getFormHM().put("spStatusList", spStatusList);
			this.getFormHM().put("budgetParamSetList", budgetParamSetList);
			this.getFormHM().put("budgetIndexFieldList", budgetIndexFieldList);
			this.getFormHM().put("employeeList", employeeList);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

