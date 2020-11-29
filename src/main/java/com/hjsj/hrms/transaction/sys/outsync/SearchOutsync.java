package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SearchOutsync extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sys_id = (String) hm.get("sys_id");
		String flag = (String)hm.get("flag");
		DBMetaModel dbmeta = new DBMetaModel(this.frameconn);
		dbmeta.reloadTableModel("t_sys_outsync");
		RecordVo vo = new RecordVo("t_sys_outsync");
		if ("1".equals(flag)) {
			vo.setString("sys_id", "");
			vo.setString("sys_name", "");
			vo.setString("send", "1");
			vo.setString("url", "http://?wsdl");
			vo.setString("sync_method", "sendSyncMsg");
			vo.setString("targetnamespace", "http://WebXml.com.cn/");
			this.getFormHM().put("jobId", "");
		} else {
			if (sys_id == null || sys_id.length() == 0)
				return;
			String sql = "select sys_id,send,sys_name,url,sync_method,control,fail_time,state,targetNamespace,other_param from t_sys_outsync where sys_id='"
					+ sys_id + "'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet re = null;
			try {
				re = dao.search(sql);
				if (re.next()) {
					vo.setString("sys_id", re.getString("sys_id") == null ? ""
							: re.getString("sys_id"));
					vo.setString("sys_name",
							re.getString("sys_name") == null ? "" : re
									.getString("sys_name"));
					vo.setString("send", re.getString("send") == null ? "0" : re.getString("send"));
					vo.setString("url", re.getString("url"));
					vo.setString("sync_method",
							re.getString("sync_method") == null ? "" : re
									.getString("sync_method"));
					vo.setString("control", re.getString("control") == null ? "" : re.getString("control"));
					vo.setInt("fail_time", re.getInt("fail_time"));
					vo.setInt("state", re.getInt("state"));
					vo.setString("targetnamespace",
							re.getString("targetnamespace") == null ? "" : re
									.getString("targetnamespace"));
					String other_param = Sql_switcher.readMemo(re, "other_param");
					vo.setString("other_param", SafeCode.encode(other_param));
					//数据视图，外部系统设置编辑报错  jingq add 2014.09.21
					String jobId = "";
					if(!"".equals(other_param)&&other_param!=null){
						PareXmlUtils utils = new PareXmlUtils(other_param);
						jobId = utils.getTextValue("/params/jobId");
					}
					
					this.getFormHM().put("jobId", jobId);
				}
			} catch (SQLException e) {
				throw new GeneralException(e.getMessage());
			}finally{
				if(re != null)
					try {
						re.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		this.getFormHM().put("record", vo);
	}
}
