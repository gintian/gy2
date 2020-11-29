package com.hjsj.hrms.module.questionnaire.plan;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author guodd guodd 2015-09-18
 * 我的问卷 快速 按状态查询和通过查询组件查询 
 *
 */
public class PlanQueryTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {

		
		//如果有type参数说明是查询组件进入的
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
		
		if(type!=null){
			StringBuffer condsql = new StringBuffer("and");
			if("1".equals(type)){
				 List values = (ArrayList) this.getFormHM().get("inputValues");
				 if(values==null || values.isEmpty()){
					 cache.setQuerySql("");
					 return;
				 }
				 condsql.append("(");
				 for(int i=0;i<values.size();i++){
					 String value =SafeCode.decode(values.get(i).toString());
					 condsql.append(" planname like '%"+value+"%' or ");
				 }
				 condsql.append(" 1=2 )");
			}else if("2".equals(type)){//方案查询
				 String exp = (String) this.getFormHM().get("exp");
				 exp = SafeCode.decode(exp);
		         String cond = (String) this.getFormHM().get("cond");
		         cond = SafeCode.decode(cond);
		         if(cond.length()<1 || exp.length()<1){
		        	 cache.setQuerySql("");
		        	 	return;
		         }
		         FactorList parser = new FactorList(exp ,cond, userView.getUserName());
		         condsql.append(parser.getSingleTableSqlExpression("myGridData"));
			}
			cache.setQuerySql(condsql.toString());
		}else{//按状态快速查询
		  int status = (Integer)this.getFormHM().get("status");
		  
		  if(status==-1)
			  cache.setQuerySql("");
		  else
			  cache.setQuerySql(" and (status = '"+status+"') ");
		}
	}

}
