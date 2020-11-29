package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 代理人设置
 * <p>Title:SearchAgentTrans.java</p>
 * <p>Description>:SearchAgentTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 6, 2010 11:19:02 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SearchAgentTrans extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer sql=new StringBuffer();
		String column="id,agent_id,agent_status,principal_id,principal_fullname,principal_status,start_date,end_date";
		sql.append("select "+ column);
		StringBuffer where=new StringBuffer();
		where.append(" from agent_set where ");
		String principal_id="";
		String agent_status="";
		if(this.userView.getStatus()==4)
		{
			principal_id=this.userView.getDbname()+this.userView.getA0100();
			agent_status="4";
		}else
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("selfservice.module.pri")));	
		where.append(" principal_id='"+principal_id+"' ");
		where.append(" and agent_status='"+agent_status+"' ");
		String orderby="order by id desc";
		this.getFormHM().put("sql", sql.toString());
		this.getFormHM().put("where", where.toString());
		this.getFormHM().put("column", column.toString());
		this.getFormHM().put("orderby", orderby);
	}

}
