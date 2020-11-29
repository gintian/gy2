package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.options;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveBudgetSysTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		//首先从form中获取要存取的数据
		try{
			String kindstr = SafeCode.decode((String)this.getFormHM().get("kindstr"));//预算表分类
			kindstr = PubFunc.keyWord_reback(kindstr);
			String rylb_codeset = SafeCode.decode((String)this.getFormHM().get("rylb_codeset"));//人员类别代码类
			rylb_codeset = PubFunc.keyWord_reback(rylb_codeset);
			String unitmenu = SafeCode.decode((String)this.getFormHM().get("unitmenu")); //归属单位指标
			unitmenu = PubFunc.keyWord_reback(unitmenu);
			String dblist = SafeCode.decode((String)this.getFormHM().get("dblist"));//参与预算的人员库
			dblist = PubFunc.keyWord_reback(dblist);
			String range = SafeCode.decode((String)this.getFormHM().get("range"));//参与预算的人员范围
			range = PubFunc.keyWord_reback(range);
			String units = SafeCode.decode((String)this.getFormHM().get("units"));//参与预算的单位 
			units = PubFunc.keyWord_reback(units);
			String createTXrecord = SafeCode.decode((String)this.getFormHM().get("createTXrecord"));//是否生成退休记录0=否，1=是
			String txCode = SafeCode.decode((String)this.getFormHM().get("txCode"));//退休人员代码
			txCode = "1".equals(createTXrecord)?PubFunc.keyWord_reback(txCode):"";
			String datatoze = SafeCode.decode((String)this.getFormHM().get("datatoze"));//预算数据到总额
			
			String ysze_set = SafeCode.decode((String)this.getFormHM().get("ysze_set"));//预算总额子集
			String ysze_idx_menu = SafeCode.decode((String)this.getFormHM().get("ysze_idx_menu"));//预算索引指标
			String ysze_ze_menu = SafeCode.decode((String)this.getFormHM().get("ysze_ze_menu"));//预算总额指标
			String ysze_status_menu = SafeCode.decode((String)this.getFormHM().get("ysze_status_menu"));//状态
			
			String ysparam_set = SafeCode.decode((String)this.getFormHM().get("ysparam_set"));//预算参数子集
			String ysparam_idx_menu = SafeCode.decode((String)this.getFormHM().get("ysparam_idx_menu"));//预算索引指标
			String ysparam_newmonth_menu = SafeCode.decode((String)this.getFormHM().get("ysparam_newmonth_menu"));//新员工入职月份指标
			
			//预算参数设置选项卡控制
			if (ysparam_idx_menu.equals(ysparam_newmonth_menu) && !"".equals(ysparam_idx_menu)) {
				this.getFormHM().put("mess", ResourceFactory.getProperty("gz.budget.savefail"));
				return;
			}
			//预算总额设置选项卡控制
			if (ysze_idx_menu.equals(ysze_ze_menu) && !"".equals(ysze_idx_menu)) {
				this.getFormHM().put("mess", ResourceFactory.getProperty("gz.budget.savetotalfail"));
				return;
			}
			//把units中的UN全部去掉
			BudgetSysBo bo = new BudgetSysBo(this.getFrameconn(),this.userView);
			String newUnits = bo.getNewUnits(units);
			//开始存储
			ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BUDGET_PARAMS", "params");
			xml.setAttributeValue("/params/rylb", "codeset", rylb_codeset);
			xml.setTextValue("/params/kindstr", kindstr);
			xml.setTextValue("/params/units", newUnits);
			xml.setAttributeValue("/params/namelist", "createTXrecord", createTXrecord);
			xml.setAttributeValue("/params/namelist", "txCode", txCode);
			xml.setTextValue("/params/namelist/unitmenu", unitmenu);
			xml.setTextValue("/params/namelist/dblist", dblist);
			xml.setTextValue("/params/namelist/range", range);
			xml.setTextValue("/params/namelist/rylb_value", "");
			xml.setTextValue("/params/namelist/txrq_value", "");
			
			xml.setAttributeValue("/params/ysze", "set",ysze_set);
			xml.setAttributeValue("/params/ysze", "idx_menu",ysze_idx_menu);
			xml.setAttributeValue("/params/ysze", "ze_menu",ysze_ze_menu);
			xml.setAttributeValue("/params/ysze", "status_menu",ysze_status_menu);
			
			xml.setAttributeValue("/params/ysparam", "set",ysparam_set);
			xml.setAttributeValue("/params/ysparam", "idx_menu",ysparam_idx_menu);
			xml.setAttributeValue("/params/ysparam", "newmonth_menu",ysparam_newmonth_menu);
			
			xml.setTextValue("/params/datatoze", datatoze);
			xml.saveStrValue();
			this.getFormHM().put("mess", ResourceFactory.getProperty("label.save.success"));
		}catch(Exception e){
			this.getFormHM().put("mess", e.toString());
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
