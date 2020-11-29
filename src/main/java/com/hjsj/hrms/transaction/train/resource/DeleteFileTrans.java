package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class DeleteFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		String fileFlag = (String)this.getFormHM().get("fileFlag");
		
		String p0100 = (String)this.getFormHM().get("p0100");
		p0100=p0100!=null&&p0100.trim().length()>0?p0100:"";
		
		String fileid = (String)this.getFormHM().get("fileid");
		fileid=fileid!=null&&fileid.trim().length()>0?fileid:"";
		
		String check = "no";
		try {
			if(fileid.length()>0){
				fileid = fileid.substring(0,fileid.length()-1);
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("delete from ");
				if("56".equals(flag)){
					sqlstr.append("per_diary_file where file_id in("+fileid+")");
				}
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.update(sqlstr.toString());
				check = "yes";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("check", check);
		this.getFormHM().put("fileFlag", fileFlag);
		this.getFormHM().put("p0100", PubFunc.encryption(p0100));
	}

}
