package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuerySignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		MorphDynaBean md=(MorphDynaBean)this.getFormHM().get("customParams");
		ArrayList fieldsMap=(ArrayList)md.get("fieldsMap");
		HashMap map=new HashMap();
		for(int i=0;i<fieldsMap.size();i++){
			MorphDynaBean fieldMd=(MorphDynaBean)fieldsMap.get(i);
			String itemid=fieldMd.get("itemid")+"";
			String itemdesc=fieldMd.get("itemdesc")+"";
			String itemtype=fieldMd.get("itemtype")+"";
			String codesetid=fieldMd.get("codesetid")+"";
			String useflag=fieldMd.get("useflag")+"";
			FieldItem item = new FieldItem();
			item.setCodesetid(codesetid);
			item.setUseflag(useflag);
			item.setItemtype(itemtype);
			item.setItemid(itemid);
			item.setItemdesc(itemdesc);
			map.put(itemid,item);
		}
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
		StringBuffer condsql = new StringBuffer(" and ");
		if("1".equals(type)){//查询栏查询
			 List values = (ArrayList) this.getFormHM().get("inputValues");
			 if(values==null || values.isEmpty()){
				 if(null != cache)
					 cache.setQuerySql("");
				 this.getFormHM().put("condsql", " and 1=1 ");
				 return;
			 }
			 String valueCol = "fullname";
			 for(int i=0;i<values.size();i++){
				 String value =SafeCode.decode(values.get(i).toString());
				 if (i == 0) {
					 {
						 condsql.append(" (" + valueCol + " like '%"+value.replace("'", "")+"%'");
					 }
				 }else {
					 condsql.append(" or " + valueCol + " like '%"+value.replace("'", "")+"%'");
				 }
			 }
			 condsql.append(")");
			 
		}else if("2".equals(type)){//方案查询
			 String exp = (String) this.getFormHM().get("exp");
			 exp = SafeCode.decode(exp);
	         String cond = (String) this.getFormHM().get("cond");
	         cond = SafeCode.decode(cond);
	         if(cond.length()<1 || exp.length()<1){
	        	 if(null != cache)
	        		 cache.setQuerySql("");
	        	 this.getFormHM().put("condsql", " and 1=1");
	        	 return;
	         }
	         FactorList factor_bo=new FactorList(PubFunc.keyWord_reback(exp),PubFunc.keyWord_reback(cond),userView.getUserId(),map);
	         condsql.append(factor_bo.getSingleTableSqlExpression("T"));
		}
		this.getFormHM().put("condsql", condsql.toString());
	}
}
