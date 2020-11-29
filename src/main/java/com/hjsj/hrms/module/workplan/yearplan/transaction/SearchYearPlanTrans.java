package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchYearPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			TableDataConfigCache tablecatch=(TableDataConfigCache)this.userView.getHm().get("yearplan_00001");
			String condSql="";
			String sql=tablecatch.getTableSql();
			String querysql=tablecatch.getQuerySql();
			StringBuffer sbf=new StringBuffer();
			ArrayList<String> valuesList = new ArrayList<String>();
			String year = (String)this.getFormHM().get("year");
			if(year!=null&&year.length()>0)
				this.userView.getHm().put("year", year);
			else
				year=(String)this.userView.getHm().get("year");
			valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
			if(valuesList!=null&&valuesList.size()>0){
				for (int i = 0; i < valuesList.size(); i++) {
					String queryVal = valuesList.get(i);
					if(i==0)
						sbf.append(" and ( ");
					else
						sbf.append(" or ");
					
					sbf.append(" P1705 like '%"+queryVal+"%'");
					
					if(i==(valuesList.size()-1)){
						sbf.append(" ) ");
						condSql=condSql+sbf.toString();
					}
				}
			}
			
			if(year!=null&&year.length()>0)
				condSql=condSql+" and P1701="+year;
			/*else
				condSql=condSql+querysql;*/
				
				
			tablecatch.setQuerySql(condSql.toString());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
