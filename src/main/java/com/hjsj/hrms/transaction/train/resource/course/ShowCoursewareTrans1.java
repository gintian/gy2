package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>
 * Title:ShowCoursewareTrans
 * </p>
 * <p>
 * Description:浏览培训课程课件内容
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-06-20 16:10:06
 * </p>
 * 
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class ShowCoursewareTrans1 extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5100 = (String) hm.get("r5100");
		r5100 = r5100 != null ? r5100 : "";
		r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		hm.remove("r5100");

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r5115 from r51 where r5100="+r5100;
		String r5115="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				r5115=this.frowset.getString("r5115");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("r5115", r5115);
	}

}
