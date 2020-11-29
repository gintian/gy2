package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 简历中心及人才库查询分析交易类
 * <p>Title: ResumeQueryPlanTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-29 下午05:23:43</p>
 * @author xiexd
 * @version 1.0
 */
public class ResumeQueryPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		//如果有type参数说明是查询组件进入的
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
		String searchSql =  tableCache.getTableSql();
		StringBuffer condsql = new StringBuffer();
		if(type!=null){
			//快速查询
			if("1".equals(type)){
				 List values = (ArrayList) this.getFormHM().get("inputValues");
				 if(values==null || values.isEmpty()){
					 //刷新userView中的sql参数
					 tableCache.setQuerySql(condsql.toString());
					 userView.getHm().put(subModuleId, tableCache);
					 if(tableCache.getCustomParamHM()==null)
							tableCache.setCustomParamHM(new HashMap<String, String>());
			         tableCache.getCustomParamHM().put("fastQuerySql", condsql.toString());
					 return;
				 }
				 FieldItem A0410 = DataDictionary.getFieldItem("A0410");
				 FieldItem A0405 = DataDictionary.getFieldItem("A0405");
				 FieldItem Z0351 = DataDictionary.getFieldItem("Z0351");
				 FieldItem A0435 = DataDictionary.getFieldItem("A0435");
				 for(int i=0;i<values.size();i++){
						condsql.append(" and (");
					 	String value = SafeCode.decode(values.get(i).toString());
					 	value = value.toLowerCase();
			            FieldItem fi = DataDictionary.getFieldItem("a0410");
			            String codesetid = "";
			            if(fi!=null&&!"".equals(fi))
						{
			            	codesetid = fi.getCodesetid();
						}
			            condsql.append(" lower(myGridData.a0101) like '%"+value+"%' ");//姓名
			            
			            if(A0405!=null && searchSql.indexOf("education") > -1)
			            	condsql.append(" or lower(myGridData.education) like '%"+value+"%' ");//学历
			            
			            if(A0410!=null && searchSql.indexOf("professional") > -1)
			            	condsql.append(" or lower(myGridData.professional) like '%"+value+"%' ");//专业
			            
			            if(Z0351!=null && searchSql.indexOf("z0351") > -1)
			            	condsql.append(" or lower(myGridData.z0351) like '%"+value+"%' ");//职位名
			            
			            if(A0435!=null && searchSql.indexOf("a0435") > -1) {
			            	condsql.append(" or lower((select codeitemdesc from codeitem where codesetid='"+codesetid+"' ");
			            	condsql.append(" and codeitemid=myGridData.a0435");
			            	condsql.append(" )) like '%"+value+"%' " );
			            	condsql.append(" or lower(myGridData.a0435) like '%"+value+"%' ");//毕业院校
			            }
			            
			            condsql.append(" )");
				 }
				 tableCache.setQuerySql(condsql.toString());
				//保存快速查询条件备用
				 if(tableCache.getCustomParamHM()==null)
						tableCache.setCustomParamHM(new HashMap<String, String>());
		         tableCache.getCustomParamHM().put("fastQuerySql", condsql.toString());
			}else if("2".equals(type)){//方案查询
				 condsql.append(" and ");
				 String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
				 exp = PubFunc.keyWord_reback(exp);
				 String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
		         if(cond.length()<1 || exp.length()<1){
		        	 //查询方案点击全部，刷新保存的快速查询sql
		        	 if(tableCache.getCustomParamHM()!=null)
		        		 tableCache.getCustomParamHM().put("fastQuerySql", "");
		        	//刷新userView中的sql参数
		        	tableCache.setQuerySql("");
					userView.getHm().put(subModuleId, tableCache);
		        	return;
		         }
		         FactorList parser = new FactorList(exp ,cond, userView.getUserName());
		         condsql.append(parser.getSingleTableSqlExpression("myGridData"));
		       //保存快速查询条件备用
				 if(tableCache.getCustomParamHM()==null)
						tableCache.setCustomParamHM(new HashMap<String, String>());
		         tableCache.getCustomParamHM().put("fastQuerySql", condsql.toString());
			}
			//公共查询条件
			String querySql = "";
			if(tableCache.getCustomParamHM()!=null){
				querySql = (String) tableCache.getCustomParamHM().get("pubQuerySql");
			}
	       	 if(StringUtils.isNotEmpty(querySql)){
	       		 condsql.append(querySql);
	       	 }
			tableCache.setQuerySql(condsql.toString());
			userView.getHm().put(subModuleId, tableCache);
			return;
		}
	}

}
