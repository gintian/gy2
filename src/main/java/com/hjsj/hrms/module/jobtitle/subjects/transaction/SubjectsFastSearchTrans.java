package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 职称评审——学科组检索
 * <p>Title: SubjectsFastSearchTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Aug 2, 2016 2:57:21 PM</p>
 * @author liuy
 * @version 7.x
 */
@SuppressWarnings("serial")
public class SubjectsFastSearchTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			String condSql = " ";
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("jobtitle_subject_00001");
			
			StringBuilder newsql = new StringBuilder();
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 模块id
			
			if("jobtitle_subject_00001".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
					// 快速查询
					for(int i = 0; i < valuesList.size(); i++){
						String queryVal = valuesList.get(i);
						queryVal = SafeCode.decode(queryVal);
						if(i == 0){
							newsql.append(" and (");
						}else{
							newsql.append(" or ");
						}
						newsql.append("W0103 like '%"+queryVal+"%' or W0105 like '%"+queryVal+"%' or W0107 like '%"+queryVal+"%' ");
					}
					if(valuesList.size() > 0){
						newsql.append(" ) ");
						condSql += newsql.toString();
					}
				} else if ("2".equals(type)) {
					HashMap queryFields = catche.getQueryFields();
					String exp = (String) this.getFormHM().get("exp");
					String cond = (String) this.getFormHM().get("cond");
					if(!StringUtils.isEmpty(exp) && !StringUtils.isEmpty(cond)){//自定义检索方案
						// 解析表达式并获得sql语句
						FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
						condSql += (" and " + parser.getSingleTableSqlExpression("data").replaceAll("data.", ""));
					}else {//全部
						condSql = "";
					}
				}
			}
			catche.setQuerySql(condSql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
