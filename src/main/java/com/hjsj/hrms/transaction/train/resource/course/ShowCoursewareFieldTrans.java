package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ShowCoursewareFieldTrans extends IBusiness {
	public void execute() throws GeneralException {
		String id = (String)this.getFormHM().get("id");
		String field = (String)this.getFormHM().get("field");
		
		String content = "";
		if(id!=null&&id.length()>0&&field!=null&&field.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				String sql = "select "+field+" ff from r51 where r5100="+PubFunc.decrypt(SafeCode.decode(id));
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
