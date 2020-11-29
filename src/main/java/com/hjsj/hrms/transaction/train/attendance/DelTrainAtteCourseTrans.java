package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>DelTrainAtteCourseTrans.java</p>
 * <p>Description:删除培训考勤排班信息</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-03 上午09:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class DelTrainAtteCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		String ids=(String)this.getFormHM().get("ids");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(ids!=null&&ids.length()>0){
			try {
				String[] id=ids.split(",");
				for (int i = 0; i < id.length; i++) {
					dao.delete("delete tr_classplan where id="+PubFunc.decrypt(SafeCode.decode(id[i])),null);
				}
				this.getFormHM().put("mess", "success");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
