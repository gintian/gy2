package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ExchangeContentTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String r5701 = (String)this.getFormHM().get("r5701");
		this.getFormHM().remove("r5701");
		r5701 = PubFunc.decrypt(SafeCode.decode(r5701));
		String field = (String)this.getFormHM().get("field");
		
		String content = "";
		if(r5701!=null&&r5701.length()>0&&field!=null&&field.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				String sql = "select "+field+" ff from r57 where r5701="+r5701;
				this.frowset = dao.search(sql);
				if(this.frowset.next())
					content = this.frowset.getString("ff");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		content = content==null||content.length()<1?"":content;
		content = content.replaceAll("\r\n", "</br>");
		this.getFormHM().put("content", SafeCode.encode(content));
	}
}
