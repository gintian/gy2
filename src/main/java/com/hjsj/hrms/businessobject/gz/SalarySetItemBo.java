package com.hjsj.hrms.businessobject.gz;

import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SalarySetItemBo {
	public HashMap fieldItemMap(Connection conn,String salaryid,UserView userView){
		HashMap hmp = new HashMap();
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		SalaryTemplateBo sabo = new SalaryTemplateBo(conn,Integer.parseInt(salaryid),userView);
		ArrayList list = sabo.getFieldlist();
		for(int i=0;i<list.size();i++){
			Field field = (Field)list.get(i);
			hmp.put(field.getName().toUpperCase(),field);
		}
		return hmp;
	}
}
