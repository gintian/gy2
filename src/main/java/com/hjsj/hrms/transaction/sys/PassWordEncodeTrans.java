package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.PassWordEncodeOrDecode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * 口令加密
 * @author Owner
 *
 */
public class PassWordEncodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String item = (String)this.getFormHM().get("pass");
		String name = (String)this.getFormHM().get("name");
		PassWordEncodeOrDecode ped = new PassWordEncodeOrDecode(this.getFrameconn(),item,"1",name);
		String info = ped.exectue();
		ped.saveUserNamePassword();
		if("ok".equals(info)){
		    RecordVo vo=new RecordVo("constant");
		    vo.setString("constant","EncryPwd");        
		    vo.setString("str_value","1");
		    ContentDAO dao=new ContentDAO(this.frameconn);
            try {
            	ped.ifNoParameterInsert("EncryPwd");
				dao.updateValueObject(vo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	ConstantParamter.putConstantVo(vo,"EncryPwd");
		}
		this.getFormHM().put("info",info);
	}

}
