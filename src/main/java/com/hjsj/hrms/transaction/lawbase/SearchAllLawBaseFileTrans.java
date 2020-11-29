package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchAllLawBaseFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		String base_id = (String) this.getFormHM().get("base_id");
		String basetype = (String) this.getFormHM().get("basetype");
		String orgId = userView.getUserOrgId();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String order_by = " order by displayorder , fileorder  desc ";
		ArrayList lawbasefilelist = new ArrayList();
		try {
			String orgTerm = "basetype=" + basetype;
			String law_file_priv = SystemConfig.getPropertyValue("law_file_priv");
			if (!"false".equals(law_file_priv.trim())) {
				if (!this.userView.isSuper_admin()) {
					if (orgId == null || orgId.length() <= 0) {
						orgTerm = orgTerm + " and (dir = '' or dir = '-1' or dir is null)";

					} else {
						LawDirectory lawDirectory = new LawDirectory();
						String orgsrt = lawDirectory.getOrgStrs(orgId, "UN", this.getFrameconn());
						orgTerm = orgTerm + " and (dir = '' or dir = '-1' or dir in (" + orgsrt + ") or dir is null )";
					}

				}
			}
			
			StringBuffer sqlBuffer = new StringBuffer("select file_id,law_base_file.base_id,content_type,law_base_file.name,title,type,valid,note_num,"
							+ "issue_org,notes,issue_date,implement_date,valid_date,ext, viewcount"
							+ " from law_base_file left join law_base_struct on law_base_file.base_id = law_base_struct.base_id");
			sqlBuffer.append(" where law_base_file.base_id = '" + base_id + "'");
			if ("5".equalsIgnoreCase(basetype) && !"false".equals(law_file_priv.trim())) {
				String unitid = userView.getUserOrgId();
				String org = " and (b0110 in (";
				String b0110 = unitid;
				if (!"".equalsIgnoreCase(b0110)) {
					org += "'" + unitid + "')";
				}
				
				if (!this.userView.isSuper_admin()) {
					if (!"".equalsIgnoreCase(b0110)) {
						sqlBuffer.append(org + " or b0110 is null) ");
					} else {
						sqlBuffer.append(" and b0110 is null ");
					}
				}
			}
			
			sqlBuffer.append(order_by);
			frowset = dao.search(sqlBuffer.toString());
			while (frowset.next()) {
				if (!"false".equals(law_file_priv.trim())) {
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset.getString("file_id")))
						continue;
				}
				
				String title = PubFunc.nullToStr(this.frowset.getString("title"));
				title = title.replaceAll("\'","’");
				title = title.replaceAll("\"","”");
				title = title.replaceAll(",","，");
				CommonData ordervo = new CommonData(PubFunc.nullToStr(this.frowset.getString("file_id")),title);
				lawbasefilelist.add(ordervo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			this.getFormHM().put("lawbasefilelist", lawbasefilelist);
		}
	}
}
