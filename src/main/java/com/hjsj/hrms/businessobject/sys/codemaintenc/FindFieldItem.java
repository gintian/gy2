package com.hjsj.hrms.businessobject.sys.codemaintenc;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.SQLException;

public class FindFieldItem {
	ContentDAO dao = null;

	public FindFieldItem(ContentDAO dao) {
		this.dao = dao;
	}

	public boolean isrelitem(String codesetid) throws SQLException {
		String sql = "select  fieldsetdesc,itemid,itemdesc  from (select fieldsetdesc,itemid,itemdesc from fielditem  fit left join (select * from fieldset) fat  on fit.fieldsetid=fat.fieldsetid  where " +
				"codesetid='"+codesetid+"' union all  select fieldsetdesc,itemid,itemdesc from t_hr_busifield thb  left join  (select * from t_hr_busitable) tht on tht.fieldsetid=thb.fieldsetid where codesetid='"+codesetid+"') ccc";
		RowSet rs = dao.search(sql);
		if (rs.next()) {
			return false;
		} else {
			return true;
		}
	}
}
