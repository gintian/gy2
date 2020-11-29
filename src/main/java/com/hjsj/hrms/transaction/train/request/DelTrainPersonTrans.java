package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DelTrainPersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String r4001 = (String)this.getFormHM().get("r4001");
		r4001=r4001!=null&&r4001.length()>0?r4001:"";
		
		String[] r4001s = r4001.split(",");
		
		String r4005 = (String)this.getFormHM().get("r4005");
		r4005=r4005!=null&&r4005.length()>0?r4005:"";
		
		 int n = 0;
	        String id = "";
	        ArrayList list = new ArrayList();

	        for (int i = 0; i < r4001s.length; i++) {
	            if (n > 0)
	                id += ",";
	            id += "'" + PubFunc.decrypt(SafeCode.decode(r4001s[i])) + "'";
	            n++;

	            if (n == 1000) {
	                list.add(id);
	                id = "";
	                n = 0;
	            }
	        }

	        if (id != null && id.length() > 0) {
	            list.add(id);
	        }
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String flag = "no";
		try {
			if(r4001!=null&&r4001.length()>0&&r4005!=null&&r4005.length()>0){
			    ArrayList sqlList = new ArrayList();
		        for (int i = 0; i < list.size(); i++) {
				StringBuffer buf = new StringBuffer();
				buf.append("delete from r40");
				buf.append(" where r4005='");
				buf.append(r4005);
				buf.append("' and r4001 in(");
				buf.append(list.get(i));
				buf.append(")");
				 sqlList.add(buf.toString());
		        }
				dao.batchUpdate(sqlList);
			}
			flag="ok";
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
	}

}
