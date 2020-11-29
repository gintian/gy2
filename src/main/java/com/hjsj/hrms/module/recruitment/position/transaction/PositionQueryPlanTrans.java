package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ParseQueryItemsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/****
 * 招聘职位查询分析交易类
 * <p>Title: PositionQueryPlanTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-29 下午05:24:18</p>
 * @author xiexd
 * @version 1.0
 */
public class PositionQueryPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		//如果有type参数说明是查询组件进入的
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
		StringBuffer condsql = new StringBuffer();
		if(type!=null){
			//快速查询
			if("1".equals(type)){
				 List values = (ArrayList) this.getFormHM().get("inputValues");
				 if(values==null || values.isEmpty()){
					 //刷新userView中的sql参数
					 tableCache.setQuerySql(condsql.toString());
					 if(tableCache.getCustomParamHM()==null)
							tableCache.setCustomParamHM(new HashMap<String, String>());
			         tableCache.getCustomParamHM().put("fastQuerySql", condsql.toString());
					 userView.getHm().put(subModuleId, tableCache);
					 return;
				 }
				 for(int i=0;i<values.size();i++){
						condsql.append(" and (");
					 	String value = SafeCode.decode(values.get(i).toString());
					 	value = value.toLowerCase();
					 	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				        String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
				        if(pinyin_field==null || pinyin_field.length()==0)
				            pinyin_field="c0103";
				        condsql.append(" lower(myGridData.z0351) like '%"+value+"%'");
				        condsql.append(" or lower(myGridData.position) like '%"+value+"%'");
				        condsql.append(" or lower(myGridData.z0333) like '%"+value+"%'");
				        condsql.append(" or lower((select organization.codeitemdesc from organization where organization.codeitemid = myGridData.Z0325)) like '%"+value+"%'");
				        condsql.append(" or lower((select organization.codeitemdesc from organization where organization.codeitemid = myGridData.Z0321)) like '%"+value+"%'");
			            condsql.append(" )");
				 }
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
		         if(tableCache.getCustomParamHM()==null)
						tableCache.setCustomParamHM(new HashMap<String, String>());
		         tableCache.getCustomParamHM().put("fastQuerySql", condsql.toString());
			}else if("3".equals(type)){
				String batchsql_replace = (String) userView.getHm().get("batchsql_replace");
				ArrayList<MorphDynaBean> items = (ArrayList<MorphDynaBean>)this.getFormHM().get("items");
				ParseQueryItemsBo queryItemsBo = new ParseQueryItemsBo();
				RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
				String dbname="";  //应聘人员库
				if(vo!=null)
					dbname=vo.getString("str_value");
				else
					throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
				
				String queryString = queryItemsBo.queryString(items,dbname,"");
				if(tableCache.getCustomParamHM()==null)
					tableCache.setCustomParamHM(new HashMap<String, String>());
				
				String combineSql = tableCache.getTableSql();
				if(items!=null&&StringUtils.isNotEmpty(batchsql_replace)){
					for (MorphDynaBean bean : items) {
						HashMap dynaBeanMap = PubFunc.DynaBean2Map(bean);
						String itemid = (String)dynaBeanMap.get("itemid");
						if("Z0103".equalsIgnoreCase(itemid)){
							combineSql = combineSql.replace(batchsql_replace, "");
							break;
						}
					}
				}
				//公共查询去掉批次条件后，拼接原来的批次sql
				if(items.size()==0) {
					combineSql += batchsql_replace;
				}
				tableCache.setTableSql(combineSql);
				String fastQuerySql = (String) tableCache.getCustomParamHM().get("fastQuerySql");
				fastQuerySql = StringUtils.isEmpty(fastQuerySql)?"":fastQuerySql;
				tableCache.setQuerySql(queryString+fastQuerySql);
				tableCache.getCustomParamHM().put("pubQuerySql", queryString);
				userView.getHm().put(subModuleId, tableCache);
				return;
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

