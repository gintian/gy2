package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/****
 * 二次推荐中人员列表查询分析功能交易类
 * <p>
 * Title: RecruitProcessQueryPlanTrans
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2018-1-4
 * </p>
 * 
 * @author xiegh
 * @version 1.0
 */
public class CandidateQueryPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			// 如果有type参数说明是查询组件进入的
			String type = (String) this.getFormHM().get("type");
			String subModuleId = (String) this.getFormHM().get("subModuleId");
			TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(subModuleId);
			StringBuffer condsql = new StringBuffer();
			if (type != null) {
				// 快速查询
				if ("1".equals(type)) {
					List values = (ArrayList) this.getFormHM().get("inputValues");
					if (values == null || values.isEmpty()) {
						// 刷新userView中的sql参数
						tableCache.setQuerySql(condsql.toString());
						userView.getHm().put(subModuleId, tableCache);
						return;
					}
					DbNameBo bo = new DbNameBo(getFrameconn());
					String emailField = bo.getEmailField();
					FieldItem emailItem = DataDictionary.getFieldItem(emailField,"A01");
					FieldItem A0435 = DataDictionary.getFieldItem("A0435","A04");
					for (int i = 0; i < values.size(); i++) {
						condsql.append(" and (");
						String value = SafeCode.decode(values.get(i).toString());
						value = value.toLowerCase();
						condsql.append(" lower(myGridData.A0101) like '%" + value + "%' ");
						if (emailItem != null && !"".equals(emailItem)) {
							String email = emailItem.getItemid();
							condsql.append(" or lower(myGridData." + email + ") like '%" + value + "%'  ");
						}
						if (A0435 != null && !"".equals(A0435)) {
							condsql.append(" or lower(myGridData.A0435) like '%" + value + "%' ");//a0435 授学单位
						}
						condsql.append(" )");
					}
					tableCache.setQuerySql(condsql.toString());
				} else if ("2".equals(type)) {// 方案查询
					condsql.append(" and ");
					String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
					exp = PubFunc.keyWord_reback(exp);
					String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
					if (cond.length() < 1 || exp.length() < 1) {
						// 刷新userView中的sql参数
						tableCache.setQuerySql("");
						userView.getHm().put(subModuleId, tableCache);
						return;
					}
					FactorList parser = new FactorList(exp, cond, userView.getUserName());
					condsql.append(parser.getSingleTableSqlExpression("myGridData"));
				}
				tableCache.setQuerySql(condsql.toString());
				userView.getHm().put(subModuleId, tableCache);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
