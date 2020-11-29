package com.hjsj.hrms.utils.components.dataview;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryHandlerTrans extends IBusiness {

	public void execute() throws GeneralException {

		String subModuleId = (String)this.formHM.get("subModuleId");
		TableDataConfigCache  tableCache=  (TableDataConfigCache)userView.getHm().get(subModuleId);
		
		ArrayList items = (ArrayList)this.formHM.get("items");
		//没有查询条件时，重置查询sql
		if(items==null || items.size()<1){
			tableCache.setQuerySql("");
			return;
		}
		
		StringBuffer condsql = new StringBuffer(" and ");
		for(int i=0;i<items.size();i++){
			DynaBean d = (DynaBean)items.get(i);
			HashMap field = PubFunc.DynaBean2Map(d);
			String itemid = field.get("itemid").toString();
			String valuetext = field.get("value").toString();
			// ' 符号过滤，防止sql注入
			valuetext = valuetext.replace("'", "\"");
			
			//日期类型单独处理
			if("D".equals(field.get("itemtype"))){
				String type = field.get("type").toString();
				//日期类型查询为范围查询，数据格式为 开始日期~结束日期。如果只有开始日期，没有结束日期，则为 开始日期~*；相反没有开始日期只有结束日期，则为 *~结束日期
				String[] value = valuetext.split("~");
				
				condsql.append(" (");
				//日期范围
				if("area".equals(type)){
					
					if(!"*".equals(value[0])){
						condsql.append(itemid).append(">=").append(Sql_switcher.dateValue(value[0])).append(" and ");
					}
					
					if(!"*".equals(value[1])){
						condsql.append(itemid).append("<=").append(Sql_switcher.dateValue(value[1]));
					}else{
						condsql.append("1=1");
					}
						
				}else if("year".equals(type)){//年范围
					
					if(!"*".equals(value[0])){
						condsql.append(Sql_switcher.year(itemid)).append(">=").append(value[0]).append(" and ");
					}
					
					if(!"*".equals(value[1])){
						condsql.append(Sql_switcher.year(itemid)).append("<=").append(value[1]);
					}else{
						condsql.append("1=1");
					}
					
				}else if("month".equals(type)){//月范围
					
					if(!"*".equals(value[0])){
						condsql.append(Sql_switcher.month(itemid)).append(">=").append(value[0]).append(" and ");
					}
					
					if(!"*".equals(value[1])){
						condsql.append(Sql_switcher.month(itemid)).append("<=").append(value[1]);
					}else{
						condsql.append("1=1");
					}
					
				}else if("day".equals(type)){//日范围
					
					if(!"*".equals(value[0])){
						condsql.append(Sql_switcher.day(itemid)).append(">=").append(value[0]).append(" and ");
					}
					
					if(!"*".equals(value[1])){
						condsql.append(Sql_switcher.day(itemid)).append("<=").append(value[1]);
					}else{
						condsql.append("1=1");
					}
				}
				
				condsql.append(" ) and ");
				continue;
			}
			
			/*代码类型可选多个，单独处理*/
			if("A".equals(field.get("itemtype")) && !"0".equals(field.get("codesetid"))){
				
				String value = valuetext;
				String[] valueList = value.split(",");
				condsql.append(" ( ");
				for(int k=0;k<valueList.length;k++){
					condsql.append(itemid).append(" = '").append(valueList[k]).append("' or ");
				}
				condsql.append(" 1=2 ");
				condsql.append(") and ");
				
				continue;
			}
			
			/*日期类型可选多个，单独处理*/
			if("N".equals(field.get("itemtype"))){
				String[] value = field.get("value").toString().split("~");
				condsql.append(" ( ");
				
				if(!"*".equals(value[0])){
					condsql.append(itemid).append(">=").append(value[0]).append(" and ");
				}
				
				if(!"*".equals(value[1])){
					condsql.append(itemid).append("<=").append(value[1]);
				}else{
					condsql.append("1=1");
				}
				
				condsql.append(") and ");
				
				continue;
			}
			
			
			/*字符型支持多个查询，按逗号分隔*/
			valuetext = valuetext.replace("，", ",");
			String[] values = valuetext.split(",");
			condsql.append("(");
			for(int k=0;k<values.length;k++) {
				condsql.append(itemid).append(" like '%").append(values[k]).append("%' or ");
			}
			condsql.append(" 1=2 ) and ");
			//condsql.append(itemid).append(" like '%").append(valuetext).append("%' and ");
			
		}
		
        
    		if(condsql.toString().endsWith("and "))
    			condsql.delete(condsql.length()-5,condsql.length()-1);
    
		tableCache.setQuerySql(condsql.toString());
		userView.getHm().put(subModuleId, tableCache);
	}

}
