package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class AffixDeleteTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list = (ArrayList) this.formHM.get("selectList");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo = (RecordVo) list.get(i);
				String ext_file_id = vo.getString("ext_file_id");
				StringBuffer sql = new StringBuffer();
				sql.append("select fileid from law_ext_file where ext_file_id=?");
				ArrayList<String> param = new ArrayList<String>();
				param.add(ext_file_id);
				this.frowset = dao.search(sql.toString(), param);
				String fileid = "";
				if (this.frowset.next()) {
					fileid = this.frowset.getString("fileid");
				}

				if (StringUtils.isNotEmpty(fileid)) {
					VfsService.deleteFile(this.getUserView().getUserName(), fileid);
				}
				String sqlText = "delete from law_ext_file where ext_file_id = '" + ext_file_id + "'";
				dao.update(sqlText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
