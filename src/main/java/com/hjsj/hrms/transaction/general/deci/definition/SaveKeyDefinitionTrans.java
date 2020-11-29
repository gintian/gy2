package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SaveKeyDefinitionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String name = (String)this.getFormHM().get("name");
		String desc = (String)this.getFormHM().get("desc");
		String standartValue = (String)this.getFormHM().get("standartValue");
		String controlValue = (String)this.getFormHM().get("controlValue");
		String fieldName = (String)this.getFormHM().get("fieldName");
		String codeItemValues = (String)this.getFormHM().get("codeItemValues");
		String staticMethod = (String)this.getFormHM().get("staticMethod");
		String box = (String)this.getFormHM().get("box");
		String oneFieldItemValue = (String)this.getFormHM().get("oneFieldItemValue");
		String twoFieldItemValue = (String)this.getFormHM().get("twoFieldItemValue");
		String flag = (String)this.getFormHM().get("object");
		String typeid = (String)this.getFormHM().get("typeid");
		
		String operateflag = (String)this.getFormHM().get("operateflag");
		String factorid = (String)this.getFormHM().get("factorid");
		
		String formula = "";
		if(box == null || "".equals(box)){
			formula = oneFieldItemValue;
		}else{
			formula = oneFieldItemValue + "/" + twoFieldItemValue; 
		}
		
		if("1".equals(operateflag)){//增加
			
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String tid = idg.getId("ds_key_factor.factorid");
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into ds_key_factor(factorid,name,formula,description,flag,standard_value,");
			sql.append("control_value,typeid,static_method,field_name,codeitem_value)");
			sql.append(" values('");
			sql.append(tid);
			sql.append("','");
			sql.append(name);
			sql.append("','");
			sql.append(formula);
			sql.append("','");
			sql.append(desc);
			sql.append("','");
			sql.append(flag);
			sql.append("',");
			sql.append(standartValue);
			sql.append(",");
			sql.append(controlValue);
			sql.append(",'");
			sql.append(typeid);
			sql.append("',");
			sql.append(staticMethod);
			sql.append(",'");
			/**
			 * xus 16/12/19
			 * 存入时为大写（适用oracle）
			 */
			sql.append(fieldName.toUpperCase());
			sql.append("','");
			sql.append(codeItemValues);
			sql.append("')");
			
			//System.out.println(sql.toString());
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.update(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		}else if("2".equals(operateflag)){//修改
			
			/*update ds_key_factor set name='dddd', formula='ss' ,description='' ,flag ='', standard_value= ,
			control_value= ,typeid='',static_method= , field_name='' ,codeitem_value=''
			where factorid='00000017'*/
				
			StringBuffer sql = new StringBuffer();
			sql.append(" update ds_key_factor set name='");
			sql.append(name);
			sql.append("' , formula='");
			sql.append(formula);
			sql.append("', description='");
			sql.append(desc);
			sql.append("' , flag='");
			sql.append(flag);
			sql.append("' , standard_value=");
			sql.append(standartValue);
			sql.append(" , control_value = ");
			sql.append(controlValue);
			sql.append(" , typeid='");
			sql.append(typeid);
			sql.append("' ,static_method=");
			sql.append(staticMethod);
			sql.append(" , field_name='");
			/**
			 * xus 16/12/19
			 * 存入时为大写（适用oracle）
			 */
			sql.append(fieldName.toUpperCase());
			sql.append("',codeitem_value='");
			sql.append(codeItemValues);
			sql.append("' where factorid='");
			sql.append(factorid);
			sql.append("'");
			
			//System.out.println(sql.toString());
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.update(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		
		this.getFormHM().put("object",flag);//信息群（A/B/K）
		this.getFormHM().put("nam",typeid);//分类号
	}
	

}
