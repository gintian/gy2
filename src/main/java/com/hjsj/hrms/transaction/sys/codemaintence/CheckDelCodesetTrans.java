package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:代码体系删除判断
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * 
 * @author wangyao
 * @version 1.0
 * 
 */
public class CheckDelCodesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList codesetlist = (ArrayList) this.getFormHM().get("list");
		if (codesetlist == null || codesetlist.size() == 0)
			return;
		String delflag = "1";
		for (int i = 0; i < codesetlist.size(); i++) {
			{
				String codesetid = codesetlist.get(i).toString().trim();
				if (!savecheck(codesetid)) {
					delflag = "0";
				}
				if ("0".equals(delflag))
					break;
			}
		}
		this.getFormHM().put("msg", delflag);
	}

	public boolean savecheck(String codesetid) {
		boolean flag = true;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String dev_flag = null;
			try {
				dev_flag = SystemConfig.getProperty("dev_flag");
			} catch (Exception e) {
				dev_flag = "0";
			}
			if (dev_flag == null) {
				dev_flag = "0";
			} else if (!"1".equalsIgnoreCase(dev_flag)) {
				dev_flag = "0";
			}
			String sql = "select status from codeset where codesetid = '"
					+ codesetid + "'";
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				String dd = rs.getString("status");
				if ("0".equalsIgnoreCase(dev_flag) && "1".equalsIgnoreCase(dd)) {
					flag = false;
				}
				if ("0".equalsIgnoreCase(dev_flag) && "2".equalsIgnoreCase(dd)) {
					flag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
