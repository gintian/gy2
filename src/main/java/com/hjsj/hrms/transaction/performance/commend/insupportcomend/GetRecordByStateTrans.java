package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetRecordByStateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String state=(String)this.getFormHM().get("state");
			
			//StringBuffer sb = new StringBuffer();
			ArrayList list = DataDictionary.getFieldList("P02",Constant.USED_FIELD_SET);
			ArrayList commendList = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				Field field = (Field) item.cloneField();
				/**推荐参数*/
				if("extendattr".equalsIgnoreCase(field.getName())){
					continue;
				}
				/**状态指标为只读*/
				if("p0209".equalsIgnoreCase(field.getName()))
					field.setReadonly(true);
				field.setSortable(true);
				if("p0201".equalsIgnoreCase(field.getName()))
					field.setVisible(false);
				if("p0209".equalsIgnoreCase(field.getName()))
					commendList.add(0,field);
				else
				    commendList.add(field);
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			StringBuffer sql2 = new StringBuffer();
			for (int j = 0; j < list.size(); j++) {
				FieldItem aitem = (FieldItem) list.get(j);
				String temp = aitem.getItemid();
				
				sql2.append("," + temp);
			}
			
			Field field = new Field("extr","推荐参数");
			field.setReadonly(true);
			field.setSortable(true);
			if(!this.getUserView().hasTheFunction("0D4102"))
				field.setVisible(false);
			commendList.add(field);
			sql2.append(",'' extr");
			
			Field newField = new Field("b", "提名候选人");
			newField.setReadonly(true);
			commendList.add(newField);
			
			sql.append(sql2.toString().substring(1));
			sql.append(",p0203 b from p02 ");
			if("00".equals(state)){
			}
			else{
				sql.append("where p0209='");
			    sql.append(state+"'");
			}
			sql.append(" order by p02.p0201 ");
			String tabname = "p02";
			this.getFormHM().put("tabname", tabname);
			this.getFormHM().put("sql", sql.toString());
			this.getFormHM().put("commendList", commendList);
			this.getFormHM().put("state",state);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
