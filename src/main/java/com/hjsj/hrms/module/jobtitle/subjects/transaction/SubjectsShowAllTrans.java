package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 职称评审——学科组显示全部人员
 * <p>Title: SubjectsShowAllTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Aug 2, 2016 4:12:28 PM</p>
 * @author liuy
 * @version 7.x
 */
@SuppressWarnings("serial")
public class SubjectsShowAllTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		String isshowall = (String)this.getFormHM().get("isshowall");//是否显示历史
		try {
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("jobtitle_subject_00001");
			String sql = catche.getTableSql();
			
			StringBuilder newsql = new StringBuilder();
			//and flag=1
			
			if("0".equals(isshowall)){//不显示全部时,只显示任聘人员
				newsql = new StringBuilder(sql);
				newsql.append(" and flag=1 ");
			}else if("1".equals(isshowall)){
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
