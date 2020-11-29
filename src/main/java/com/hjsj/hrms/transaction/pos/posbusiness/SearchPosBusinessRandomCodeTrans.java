package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:SearchPosBusinessRandomCodeTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * Create time:Aug 23, 2008:11:53:06 AM
 * </p>
 * 
 * @author huaitao
 * @version 1.0
 */
public class SearchPosBusinessRandomCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap<?, ?> hm = (HashMap<?, ?>) this.getFormHM().get("requestPamaHM");
		String codesetid = (String) hm.get("codesetid");
		String codeitem = (String) hm.get("codeitem");
		hm.remove("codesetid");

		// 当codesetid等于certificate，从证书参数配置中获取实际的代码类
		if ("certificate".equalsIgnoreCase(codesetid)) {
			try {
				Class<?> forName = Class.forName("com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo");
				Method certCode = forName.getMethod("getCertCategoryCode");
				Method setconn = forName.getMethod("setConn", Connection.class);
				Method setuser = forName.getMethod("setUserView", UserView.class);
				Method readParam = forName.getMethod("ReadParam");

				Object obj = forName.getConstructor().newInstance();
				setconn.invoke(obj, this.frameconn);
				setuser.invoke(obj, userView);
				readParam.invoke(obj);
				Object code = certCode.invoke(obj);
				codesetid = (String) code;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 如果未设置证书类别代码类 直接抛出
			if(StringUtils.isBlank(codesetid))
				throw new GeneralException("", "未指定证书类别代码类！请到证照管理/配置/证书类别代码类设置！","", "");
			this.getFormHM().put("fromflag", "6");
		}

		this.getFormHM().put("codesetid", codesetid);
		this.getFormHM().put("codeitem", codeitem);
		String sql = "select codesetdesc from codeset where codesetid='" + codesetid + "'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next())
				this.getFormHM().put("codesetdesc", this.frowset.getString("codesetdesc"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
