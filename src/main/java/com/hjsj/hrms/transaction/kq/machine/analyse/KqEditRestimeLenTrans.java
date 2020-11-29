package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 *<p>Title:KqEditRestimeLenTrans.java</p> 
 *<p>Description:编辑休息日转加班休息扣除数</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2013-6-21下午03:19:49</p> 
 *@author wangmj
 *@version 1.0
 */
public class KqEditRestimeLenTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String table = "";
		KqParam kqParam = KqParam.getInstance();
		String analyseType = kqParam.getData_processing();
		if (analyseType != null && "0".equals(analyseType)) {
			table = "kt_" + this.userView.getUserName() + "_co";//休息日刷卡转加班
			
		} else if (analyseType != null && "1".equals(analyseType)) {
			table = "kq_analyse_cardtoovertime";
		} else {
			table = "kt_" + this.userView.getUserName() + "_co";
		}
		
		String a0100 = (String) this.getFormHM().get("a0100");
		String nbase = (String) this.getFormHM().get("nbase");
		String timelen = (String) this.getFormHM().get("timeLen");
		String start = (String) this.getFormHM().get("start");
		String end = (String) this.getFormHM().get("end");
		String fact_time = (String)this.getFormHM().get("fact_time");
		if (timelen != null && timelen.length() > 0 && Float.parseFloat(fact_time) < Float.parseFloat(timelen)) 
		{
			this.getFormHM().put("flag", "false");
			this.getFormHM().put("mess", "请输入有效的休息扣除数！");
		}else 
		{
			StringBuffer sql = new StringBuffer();
			sql.append("update " + table );
			sql.append(" set q11xx = '" + timelen + "'");
			sql.append(" where nbase = '" + nbase + "' and a0100 = '" + a0100 + "'");
			sql.append(" and begin_date = " + Sql_switcher.dateValue(start));
			sql.append(" and end_date = " + Sql_switcher.dateValue(end));
			
			ContentDAO dao = new ContentDAO(getFrameconn());
			try {
				dao.update(sql.toString());
				this.getFormHM().put("flag", "true");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}

	}

}
