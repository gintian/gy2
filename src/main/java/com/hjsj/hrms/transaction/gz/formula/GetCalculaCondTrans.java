package com.hjsj.hrms.transaction.gz.formula;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetCalculaCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		
		String itemid = (String)hm.get("item");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String cond = "";
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao.search("select cond from salaryformula where itemid='"+itemid+"' and  salaryid="+salaryid);
			if(rs.next()){
				cond = rs.getString("cond");
				cond=cond!=null&&cond.trim().length()>0?cond:"";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("conditions",SafeCode.encode(cond));
		

	}

}
