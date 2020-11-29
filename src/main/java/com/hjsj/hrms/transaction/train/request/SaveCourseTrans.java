package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>Title:培训班</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class SaveCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String r3101 = (String)this.getFormHM().get("r3101");
		r3101=r3101!=null?r3101:"";
		
		String r3122 = (String)this.getFormHM().get("r3122");
		r3122=r3122!=null?r3122:"";
		r3122=SafeCode.decode(r3122);
		String infor = "no";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "update r31 set r3122='"+r3122+"' where r3101='"+r3101+"'";
		if(!this.userView.isSuper_admin()){
		String where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
		sql += " " + where.replaceFirst("where", "and");
		}
		
		try {
			if(r3101.length()>0){
				dao.update(sql);
				infor="ok";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("infor",infor);
	}
}
