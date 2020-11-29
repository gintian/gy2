package com.hjsj.hrms.utils.components.rolepicker.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

public class SearchPerRoleListTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		
	try{
		
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
		
		StringBuffer condsql = new StringBuffer("");
		if("1".equals(type)){//查询栏查询
			 List values = (ArrayList) this.getFormHM().get("inputValues");
			 if(values==null || values.isEmpty()){
				 cache.setQuerySql("");
				 return;
			 }
	         boolean isMul = false;//是否是多个值
	         if(values.size()>1)
	              isMul = true;
	         if(values.size()>1){
	        	 for(int i=0;i<values.size();i++){
					 String value =SafeCode.decode(values.get(i).toString());
					 if (i == 0) {
						 condsql.append(" and ");
						 if(isMul){
							 condsql.append("(( role_name like '%"+value+"%' or ");
						 }else{
							 condsql.append("( role_name like '%"+value+"%' or ");
						 }
		            	 condsql.append(" role_desc like '%"+value+"%')");
					 }
					 if(i==values.size()-1){
						 condsql.append(" or ");
						 condsql.append("( role_name like '%"+value+"%' or ");
						 if(isMul){
							 condsql.append(" role_desc like '%"+value+"%' ))");
						 }
					 }else{
						 condsql.append(" or ");
						 condsql.append("( role_name like '%"+value+"%' or ");
						 condsql.append(" role_desc like '%"+value+"%' )");
					 }
				 }
	         }else{
	        	 condsql.append(" and ");
				 condsql.append("( role_name like '%"+SafeCode.decode(values.get(0).toString())+"%' or ");
            	 condsql.append(" role_desc like '%"+SafeCode.decode(values.get(0).toString())+"%')");
	         }
		}
		cache.setQuerySql(condsql.toString());
	} catch (Exception e) {
        e.printStackTrace();
        GeneralExceptionHandler.Handle(e);
    }
	}
}
