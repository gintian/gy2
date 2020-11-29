package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class QuashPosBusinessTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String invalid = (String) reqhm.get("invalid");
		reqhm.remove("invalid");
		ArrayList delposbusinesslist = (ArrayList) this.getFormHM().get(
				"selectedlist");
		if (delposbusinesslist == null || delposbusinesslist.size() == 0)
			return;
		StringBuffer delsql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if (invalid != null && !"".equals(invalid)) {//改变状态操作
				for (int i = 0; i < delposbusinesslist.size(); i++) {
					RecordVo codeitemvo = (RecordVo) delposbusinesslist.get(i);
					delsql.delete(0, delsql.length());
					delsql.append("update codeitem set invalid="
							+ invalid
							+ " where codesetid='");
					delsql.append(codeitemvo.getString("codesetid"));
					delsql.append("' and codeitemid like '");
					delsql.append(codeitemvo.getString("codeitemid"));
					delsql.append("%'");
					dao.update(delsql.toString(), new ArrayList());
				}
			} else {//撤销操作
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				String date = sdf.format(calendar.getTime());
				for (int i = 0; i < delposbusinesslist.size(); i++) {
					RecordVo codeitemvo = (RecordVo) delposbusinesslist.get(i);
					delsql.delete(0, delsql.length());
					delsql.append("update codeitem set end_date="
							+ Sql_switcher.dateValue(date)
							+ " where codesetid='");
					delsql.append(codeitemvo.getString("codesetid"));
					delsql.append("' and codeitemid like '");
					delsql.append(codeitemvo.getString("codeitemid"));
					delsql.append("%'");
					/*delsql.append("%' and (end_date>"
							+ Sql_switcher.dateValue(date) + " or "
							+ Sql_switcher.sqlNull("end_date", 0) + "=0)");*/
					dao.update(delsql.toString(), new ArrayList());
				}
			}
			this.getFormHM().put("codelist", new ArrayList());
			this.getFormHM().put("isrefresh", "");
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}

}
