package com.hjsj.hrms.transaction.police;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>
 * Title:SaveSetYqdtTrans
 * </p>
 * <p>
 * Description:保存狱情动态设置
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-13
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SaveSetYqdtTrans extends IBusiness {

	public void execute() throws GeneralException {
		String checkValues = (String) this.getFormHM().get("policeConstant");
		this.getFormHM().remove("policeConstant");
		RecordVo vo = new RecordVo("constant");
		vo.setString("constant", "POLICE_SETYQDT");
		vo.setString("str_value", checkValues);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select constant from constant where constant='POLICE_SETYQDT'";
		try {
			frowset = dao.search(sql);
			if (frowset.next()) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	

}
