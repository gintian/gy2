package com.hjsj.hrms.transaction.general.query.complex;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 得到临时变量定义公式
 * <p>Title:GetMidvariableNValueTrans.java</p>
 * <p>Description>:GetMidvariableNValueTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 17, 2010 4:57:44 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class GetMidvariableNValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		String nid=(String)this.getFormHM().get("nid");
		if(nid==null||nid.length()<=0)
		{
			this.getFormHM().put("nvalue", "");
			return ;
		}
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select cname,chz,nid,cValue");
		sqlstr.append(" from midvariable where nid='"+nid+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String nvalue="";
		try {
			this.frowset=dao.search(sqlstr.toString());
			if(this.frowset.next())
				nvalue=this.frowset.getString("cValue");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nvalue=SafeCode.encode(nvalue);
		this.getFormHM().put("nvalue", nvalue);
	}

}
