package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.module.qualifications.businessobject.QuanlificationsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 评审条件，首页跳转时建议类
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */

public class QualificationsForHomeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String conditionid = (String)this.getFormHM().get("conditionid");//评审条件编号
		conditionid = PubFunc.decrypt(conditionid);
		ContentDAO  dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			HashMap map = new HashMap();
			
			QuanlificationsBo quanlificationsBo = new QuanlificationsBo(this.getFrameconn(),this.getUserView());
			String sql = "select * from zc_condition where condition_id=?";
			
			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add(conditionid);
			
			rs = dao.search(sql, sqlList);
			while(rs.next()){
				String conid = rs.getString("condition_id");
				map.put("zc_series", rs.getString("zc_series"));
				map.put("description", StringUtils.isEmpty(rs.getString("description")) ? "" : rs.getString("description"));
				map.put("attachmentlist",quanlificationsBo.getAttachmentList(conid));
			}
			this.getFormHM().put("conditioninfo", map);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
