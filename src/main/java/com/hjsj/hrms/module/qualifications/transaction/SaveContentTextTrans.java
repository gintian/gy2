package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveContentTextTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String conditionid = null;
		String texthtml = "";
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionid")))
			conditionid = (String)this.getFormHM().get("conditionid");
		//将前台html数据解密
		if(!StringUtils.isEmpty((String)this.getFormHM().get("texthtml"))){
			texthtml = (String)this.getFormHM().get("texthtml");
			texthtml = PubFunc.keyWord_reback(SafeCode.decode(texthtml));
		}
			
		//获取当前时间
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList list = new ArrayList();
		RecordVo vo = new RecordVo("zc_condition");
		try {
			vo.setString("condition_id",PubFunc.decrypt(conditionid));
			vo = dao.findByPrimaryKey(vo);
			vo.setDate("modify_time", new java.sql.Date(new java.util.Date().getTime()));
			vo.setString("description", texthtml);
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
