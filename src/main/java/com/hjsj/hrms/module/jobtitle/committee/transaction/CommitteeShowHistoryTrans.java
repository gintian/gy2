package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 资格评审_评委会显示历史
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class CommitteeShowHistoryTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		String ishistory = (String)this.getFormHM().get("ishistory");//是否显示历史
		try {
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("jobtitle_committee_00001");
			String sql = catche.getTableSql();
			
			StringBuilder newsql = new StringBuilder();
			//and flag=1
			
			if("0".equals(ishistory)){//不显示历史时,只显示任聘人员
				newsql = new StringBuilder(sql);
				newsql.append(" and flag=1 ");
			}else if("1".equals(ishistory)){
				sql = sql.replaceAll("and flag=1", "");
				newsql = new StringBuilder(sql);
			}
			
			
			catche.setTableSql(newsql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
