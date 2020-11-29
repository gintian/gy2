package com.hjsj.hrms.transaction.general.kanban;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class RemarkTrans extends IBusiness {

	public void execute() throws GeneralException {
		String itemid = (String)this.getFormHM().get("itemid");
		itemid = itemid != null ? itemid : "";

		String p0500 = (String)this.getFormHM().get("p0500");
		p0500 = p0500 != null ? p0500 : "";
		p0500 = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter(p0500);
		
		String resume = "";
		if(itemid.trim().length()>1 && p0500.trim().length()>5){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(itemid);
			sqlstr.append(" FROM P05 ");
			sqlstr.append(" WHERE p0500=?");
			
			ArrayList param = new ArrayList();
			param.add(p0500);
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				RowSet rs = dao.search(sqlstr.toString(), param);
				if(rs.next()){
					resume = Sql_switcher.readMemo(rs,itemid);
				}
				
				if(resume!=null&&resume.trim().length()>0)
					resume = resume.replaceAll("\n", "<br>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("resume", SafeCode.encode(resume));
	}

}
