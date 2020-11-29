package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ConentTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String contentid = (String)this.getFormHM().get("contentid");
		contentid=contentid!=null&&contentid.trim().length()>0?contentid:"";
		String flag = (String) this.getFormHM().get("flag");
		String p0600 = (String) this.getFormHM().get("p0600");
		p0600=p0600!=null&&p0600.trim().length()>0?p0600:"";
		if(!"antev".equals(flag)){
			p0600=SafeCode.decode(p0600);
			p0600 = PubFunc.decrypt(p0600);
		}
		String wherestr = "from p06 where p0600='"+p0600+"'";

		
		if(contentid.length()>0&&wherestr.length()>5){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(contentid);
			sqlstr.append(" ");
			sqlstr.append(wherestr);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String memo = "";
			try {
				this.frowset =dao.search(sqlstr.toString());
				if(this.frowset.next())
					memo = this.frowset.getString(1);
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			memo=memo!=null&&memo.length()>0?memo.replaceAll("\n","<br>"):"";
			
			this.getFormHM().put("content",SafeCode.encode(memo));
		}
	}

}
