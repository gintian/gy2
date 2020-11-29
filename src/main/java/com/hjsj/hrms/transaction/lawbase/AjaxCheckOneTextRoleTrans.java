package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 检查文件是否被授权
 * 
 * @author Owner
 *
 */
public class AjaxCheckOneTextRoleTrans extends IBusiness {
	public void execute() throws GeneralException {
		try {
			String file_id = (String) this.getFormHM().get("file_id");
			String query = (String) this.getFormHM().get("query");
			String sturt = "ok";
			String law_file_priv = SystemConfig.getPropertyValue("law_file_priv");
			if (!"false".equals(law_file_priv.trim())) {
				if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, PubFunc.decrypt(SafeCode.decode(file_id))))
					sturt = "false";
			}

			if ("ok".equalsIgnoreCase(sturt)
					&& ("download".equalsIgnoreCase(query) || "original".equalsIgnoreCase(query))) {
				StringBuffer sql = new StringBuffer();
				sql.append("select fileid,originalfileid");
				sql.append(" from law_base_file");
				sql.append(" where file_id=?");
				ArrayList<String> param = new ArrayList<String>();
				param.add(PubFunc.decrypt(file_id));
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search(sql.toString(), param);
				if (this.frowset.next()) {
					if ("download".equalsIgnoreCase(query)) {
						file_id = this.frowset.getString("fileid");
					} else if ("original".equalsIgnoreCase(query)) {
						file_id = this.frowset.getString("originalfileid");
					}
				} else {
					file_id = "";
				}

				file_id = StringUtils.isEmpty(file_id) ? "" : file_id;
			}
			
			this.getFormHM().put("sturt", sturt);
			this.getFormHM().put("query", query);
			this.getFormHM().put("file_id", file_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
