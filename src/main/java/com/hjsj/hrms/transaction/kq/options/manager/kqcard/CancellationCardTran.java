package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 已作废卡
 * <p>Title:CancellationCardTran.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 9, 2007 4:19:10 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CancellationCardTran extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String sql="select card_no";
    	String where="from kq_cards where status='0'";
    	String orderby="order by card_no asc";
    	String column="card_no";
    	this.getFormHM().put("sql",sql);
    	this.getFormHM().put("where",where);
    	this.getFormHM().put("orderby",orderby);
    	this.getFormHM().put("column",column);
    }

}
