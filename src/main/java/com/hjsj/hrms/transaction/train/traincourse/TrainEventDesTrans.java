package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainEventDesTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("id");
		id=id!=null?id:"";
		hm.remove("id");
		
		String classid = (String)hm.get("classid");
		hm.remove("classid");
		
		String tablename = (String)hm.get("tablename");
		tablename=tablename!=null?tablename:"";
		hm.remove("tablename");
		
		String read = (String)hm.get("read");
		read=read!=null?read:"1";
		hm.remove("read");
		
		String flag = (String)hm.get("flag");
		flag=flag!=null?flag:"add";
		hm.remove("flag");
		String content = "";
		try {

			ContentDAO dao = new ContentDAO(this.frameconn);
			if("save".equals(flag)){
				content = (String)this.getFormHM().get("contentEv");
				content=content!=null?content:"";
				content = PubFunc.keyWord_reback(content);
				ArrayList list = new ArrayList();
				list.add(content);
				list.add(classid);
				ArrayList listvalue = new ArrayList();
				listvalue.add(list);
				
				StringBuffer updatesql = new StringBuffer();
				updatesql.append("update "+tablename+" set ");
				updatesql.append(id);
				updatesql.append("=? ");
				updatesql.append(" where r3101=?");
				dao.batchUpdate(updatesql.toString(),listvalue);
			}else {
				content = "";
			}

			if(content.trim().length()<1){
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("select ");
				sqlstr.append(id);
				sqlstr.append(" from r31 where r3101='");
				sqlstr.append(classid);
				sqlstr.append("'");

				this.frowset = dao.search(sqlstr.toString());

				if(this.frowset.next()){
					content = this.frowset.getString(1);
					content=content!=null?content:"";
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		this.getFormHM().put("contentEv", content);
		this.getFormHM().put("contentid", id);
		this.getFormHM().put("tablename", tablename);
		this.getFormHM().put("readonly", read);
		this.getFormHM().put("classid", classid);
	}

}
