package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EventDesTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("id");
		id=id!=null?id:"";
		hm.remove("id");
		
		String classid = (String) hm.get("classid");
		
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
				ArrayList listvalue = new ArrayList();
				listvalue.add(list);
				
				StringBuffer updatesql = new StringBuffer();
				updatesql.append("update r31 set ");
				updatesql.append(id);
				updatesql.append("=? ");
				updatesql.append(" where r3101='" + classid + "'" );
				dao.batchUpdate(updatesql.toString(),listvalue);
			}else {
				content = "";
			}

			if(content.trim().length()<1){
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("select ");
				sqlstr.append(id);
				sqlstr.append(" from r31 where r3101='" + classid + "' ");

                if (!this.userView.isSuper_admin()) {
                    String where = TrainCourseBo.getUnitIdByBusiWhere(this.userView);
                    if(where.length()>0)
                        sqlstr.append(where.replaceFirst("where", "and"));
                }
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
		this.getFormHM().put("readonly", read);
	}

}
