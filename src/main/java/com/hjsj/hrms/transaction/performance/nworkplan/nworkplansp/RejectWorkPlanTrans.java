package com.hjsj.hrms.transaction.performance.nworkplan.nworkplansp;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RejectWorkPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		LazyDynaBean bean = (LazyDynaBean) this.getFormHM().get("vo");
		String p0100=(String) bean.get("p0100");
		String reason=(String) bean.get("reason");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=sdf.format(new Date());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo=new RecordVo("p01");
		vo.setString("p0100", p0100);
		try {
			vo=dao.findByPrimaryKey(vo);
			vo.setString("p0113", reason);
			vo.setString("p0117", this.userView.getUserName());
			vo.setString("p0116", date);
			vo.setString("p0115", "07");
			dao.updateValueObject(vo);
			String state=vo.getString("state");
			String belong_type=vo.getString("belong_type");
			this.getFormHM().put("p0100", p0100);
			this.getFormHM().put("state", state);
			this.getFormHM().put("belong_type", belong_type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
