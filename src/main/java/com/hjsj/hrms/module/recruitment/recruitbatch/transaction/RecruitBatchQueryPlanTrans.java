package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：RecruitBatchQueryPlanTrans 
 * 类描述：招聘批次查询
 * 创建人：sunming 
 * 创建时间：2015-12-8
 * 
 * @version
 */
public class RecruitBatchQueryPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
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
					 userView.getHm().put(subModuleId, tableCache);
					 return;
				 }
				 for(int i=0;i<values.size();i++){
						condsql.append(" and (");
					 	String value = SafeCode.decode(values.get(i).toString());
					 	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				        String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
				        if(pinyin_field==null || pinyin_field.length()==0)
				            pinyin_field="c0103";
				        condsql.append(" lower(myGridData.z0103) like '%"+value.toLowerCase()+"%'");
			            condsql.append(" )");
				 }
				 tableCache.setQuerySql(condsql.toString());
			}else if("2".equals(type)){//方案查询
				 condsql.append(" and ");
				 String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
				 exp = PubFunc.keyWord_reback(exp);
		         String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
		         if(cond.length()<1 || exp.length()<1){
		        	//刷新userView中的sql参数
		        	tableCache.setQuerySql("");
					userView.getHm().put(subModuleId, tableCache);
		        	return;
		         }
		         FactorList parser = new FactorList(exp ,cond, userView.getUserName());
		         condsql.append(parser.getSingleTableSqlExpression("myGridData"));
			}
			tableCache.setQuerySql(condsql.toString());
			userView.getHm().put(subModuleId, tableCache);
			return;
		}
	}
}

