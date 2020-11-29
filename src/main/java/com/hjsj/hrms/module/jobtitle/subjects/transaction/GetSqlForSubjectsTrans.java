package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取查询语句_学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetSqlForSubjectsTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			HashMap hm = this.getFormHM();
			/**  memberType 成员类型 =subject学科组成员 =committee聘委会成员 **/
			String memberType = String.valueOf(hm.get("memberType"));
			ExpertsBo expertsBo = new ExpertsBo(this.getFrameconn(), this.getUserView());
			StringBuilder dataSql = new StringBuilder();
			dataSql.append("select *");
			/** 获得需要查询的字段拼接sql */
			String selectsql = expertsBo.getSelectSql();
			/** 获得sql*/
			dataSql.append(" from ");
			dataSql.append(selectsql+" and d.w0109='1' ");
			
			StringBuilder sql =  new StringBuilder(dataSql);//查询sql
			ArrayList<String> searchItems = new ArrayList<String>();//检索项目
			
			
			//排除已选成员
			if("committee".equalsIgnoreCase(memberType)){
				String committeeId = String.valueOf(hm.get("committee_id"));
				committeeId = PubFunc.decrypt(committeeId);
				sql.append(" and W0101 not in (select W0101 from zc_judgingpanel_experts where committee_id='");
				sql.append(committeeId + "' and ("+Sql_switcher.sqlNow()+" between start_date and end_date ");
				sql.append(" or end_date is null))");
			}else if("subject".equalsIgnoreCase(memberType)){
				String groupId = String.valueOf(hm.get("group_id"));
				groupId = PubFunc.decrypt(groupId);
				sql.append(" and W0101 not in (select expertid from zc_subjectgroup_experts where group_id='");
				sql.append(groupId + "' and ("+Sql_switcher.sqlNow()+" between start_date and end_date ");
				sql.append(" or end_date is null))");
				
			}

			searchItems.add(PubFunc.encrypt("w0103"));
			searchItems.add(PubFunc.encrypt("w0105"));
			searchItems.add(PubFunc.encrypt("w0107"));
			this.getFormHM().put("sql", PubFunc.encrypt(sql.toString()));
			this.getFormHM().put("orderBy", PubFunc.encrypt("order by w0101"));
			this.getFormHM().put("searchItems", searchItems);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
