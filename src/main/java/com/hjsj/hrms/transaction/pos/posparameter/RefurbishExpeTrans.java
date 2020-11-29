package com.hjsj.hrms.transaction.pos.posparameter;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 
 *<p>Title:RefurbishExpeTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 23, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class RefurbishExpeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql="select id,name from LExpr";
		StringBuffer expr = new StringBuffer();
		expr.append("<select id=\"selectid\" style=\"width:150;\" name=\"selectid\" onblur=\"onLeave1('#');\" onchange=\"addexpr()\">");
		expr.append("<option value=\"new\"></option>");
		try {
			this.frowset = dao.search(sql);
			while(frowset.next()){
				expr.append("<option value=\""+frowset.getString("id")+"\">");
				expr.append(frowset.getString("name"));
				expr.append("</option>");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		expr.append("<option value=\"0\">新增...</option>");
		this.getFormHM().put("expr",SafeCode.encode(expr.toString()));
	}

}
