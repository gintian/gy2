package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadTableSelectCond extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag="false";
			String subModuleId = (String)this.formHM.get("subModuleId");
			String setName="",queryItem="";
				
			setName=(String)this.formHM.get("setName");
			queryItem=this.formHM.get("queryItem")==null?"":(String)this.formHM.get("queryItem");
			
			if(setName==null||"".equals(setName)){
				HashMap customParamMap=(HashMap)((TableDataConfigCache)this.userView.getHm().get(subModuleId)).getCustomParamHM();
				setName = (String) customParamMap.get("setName");
				queryItem = (String) customParamMap.get("queryItem");
			}
			//查询框内容		
			ArrayList<String> valuesList = new ArrayList<String>();
			valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
            //快速查询
			String condsql=getCondsql(setName,queryItem);
//	            this.userView.getHm(subModuleId).getCustomParamHM();
	            
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 快速查询
	 * @param setName
	 * @return
	 * @throws GeneralException
	 */
	public String getCondsql(String setName,String queryItem) throws GeneralException{
		String subModuleId = (String)this.formHM.get("subModuleId");
		TableDataConfigCache  tableCache=  (TableDataConfigCache)userView.getHm().get(subModuleId);
		String fieldid=getFieldKey(setName);
		
			//如果有type参数说明是查询组件进入的
			String type = (String)this.getFormHM().get("type");
			StringBuffer condsql = new StringBuffer(" and ");
			//快速查询
				 List values = (ArrayList) this.getFormHM().get("inputValues");
				 if(values==null || values.isEmpty()){
					 tableCache.setQuerySql(" and 1=1 ");
					 return " and 1=1 ";
				 }
				 //查询指标
				 String cond="";
				 if("".equals(queryItem)){
					 cond="A0101 like '%?%'";
				 }else{
					 String[] items=queryItem.split(",");
					for(int i=0;i<items.length;i++){
						if(i>0)
							cond+=" or ";
						cond += items[i]+" like '%?%' ";
					}
				 }
				 for(int i=0;i<values.size();i++){
					 String value =SafeCode.decode(values.get(i).toString());
					 value = value.replace('\'', '‘');
					 if(setName.startsWith("A"))
						 condsql.append(" ( "+cond.replace("?", value)+" ) ");
					else 
						condsql.append( fieldid+" in (select codeitemid from organization where codeitemdesc like '%"+value+"%' )");
					 condsql.append( " or ");
					 
				 }
				 condsql.append(" 1=2 ");
			tableCache.setQuerySql(condsql.toString());
			userView.getHm().put(subModuleId, tableCache);
		return condsql.toString();
	}
	/**
	 * 获取子集主键
	 * @param setName
	 * @return
	 */
	public String getFieldKey(String setName){
		String key = "A0100";
		if(setName.startsWith("A"))
			key = "A0100";
		else if(setName.startsWith("B"))
			key = "B0110";
		else if(setName.startsWith("K"))
			key = "E0122";
		else if(setName.startsWith("H"))
			key = "H0100";
		return key;
	}
}

