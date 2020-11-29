package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.sql.SQLException;

public class EditNameTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String conditionid = null;
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionid")))
			conditionid = (String)this.getFormHM().get("conditionid");
		String zc_series = null;
		if(!StringUtils.isEmpty((String)this.getFormHM().get("zc_series2")))
			zc_series = (String)this.getFormHM().get("zc_series2");
		RecordVo vo = new RecordVo("zc_condition");
		vo.setString("condition_id",PubFunc.decrypt(conditionid));
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			vo = dao.findByPrimaryKey(vo);
			vo.setString("zc_series", zc_series);
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("revalue", "success");
	}
}
