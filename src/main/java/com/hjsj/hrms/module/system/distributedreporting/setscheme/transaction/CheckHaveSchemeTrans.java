package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;

/**
 * 校验此单位是否已经制定了数据标准，是true
 * @author caoqy 2019-5-6 15:25:24
 *
 */
public class CheckHaveSchemeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String sql = "SELECT Str_Value FROM Constant WHERE Constant = 'BS_ASYN_PLAN_S'";
		RowSet rs = null;
		ContentDAO dao = null;
		boolean haveSchemeFlag = true;
		try {
			dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql);
			if(rs.next()) {
				String param = rs.getString("Str_Value");
				if(StringUtils.isBlank(param)) {
					haveSchemeFlag = false;
				}
			}else {
				haveSchemeFlag = false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		this.getFormHM().put("sflag", haveSchemeFlag);
	}

}
