package com.hjsj.hrms.transaction.train.attendance.card;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:DelEmpRegRecTrans.java</p>
 * <p>Description>:DelEmpRegRecTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2011 3:05:59 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class DelEmpRegRecTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		ArrayList list = (ArrayList) this.getFormHM().get("selected");
		ContentDAO dao = new ContentDAO(this.frameconn);
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean from = (LazyDynaBean) list.get(i);
			String a0100 = (String) from.get("a0100");
			String nbase = (String) from.get("nbase");
			String card_time = (String) from.get("card_time");
			String r4101 = (String) from.get("r4101");
			String sql = "DELETE FROM tr_cardtime where a0100='"
					+ a0100
					+ "' AND nbase='"
					+ nbase
					+ "' AND r4101='"
					+ r4101
					+ "' AND card_time="
					+ Sql_switcher.dateValue(card_time);
			try {
				dao.delete(sql, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
