package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AffixTrans extends IBusiness {
	public void execute() throws GeneralException {
		String file_id = (String)getFormHM().get("file_id");
		file_id = PubFunc.decrypt(file_id);
		String basetype = (String) getFormHM().get("basetype");
		this.getFormHM().put("basetype",basetype);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
			frowset = dao.search("select ext_file_id,version,name,ext,create_time,create_user,fileid from law_ext_file where file_id='" + file_id + "' order by create_time");
			while (frowset.next()) {
				RecordVo vo = new RecordVo("law_ext_file");
				vo.setString("ext_file_id", frowset.getString("ext_file_id"));
				vo.setString("version", frowset.getString("version"));
				vo.setString("name", frowset.getString("name"));
				//vo.setString("content", frowset.getString("content"));
				vo.setString("ext", frowset.getString("ext"));
				vo.setString("ext_file_id", frowset.getString("ext_file_id"));
				Date d_create=frowset.getDate("create_time");
				String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
				vo.setString("create_time", d_str);
				vo.setString("create_user", frowset.getString("create_user"));
				vo.setString("fileid", frowset.getString("fileid"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("affixList", list);
	}

}
