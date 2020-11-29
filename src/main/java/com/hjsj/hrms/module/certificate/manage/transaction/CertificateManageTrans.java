package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CertificateManageTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String errorMessage = "";
		try {
			CertificateManageBo cmbo = new CertificateManageBo(this.userView, this.frameconn);
			// 获取系统设置的唯一性指标
			String onlyField = cmbo.getOnlyFieldItem().get(0);
			if (StringUtils.isEmpty(onlyField)) {
				errorMessage = "人员唯一性指标不能为空！";
				return;
			}
			
			CertificateConfigBo ccbo = new CertificateConfigBo(this.frameconn, this.userView);
			// 获取证书子集
			String fieldSetId = ccbo.getCertSubset();
			String certCategoryCode = ccbo.getCertCategoryCode();
			String certNOItemId = ccbo.getCertNOItemId();
			ArrayList<String> userbaseList = ccbo.getCertNbase();
			String certName = ccbo.getCertName();
			String certCategoryItemId = ccbo.getCertCategoryItemId();
			String certOrg = ccbo.getCertOrganization();
			if (userbaseList == null || userbaseList.size() == 0) {
				errorMessage = "人员库不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(fieldSetId)) {
				errorMessage = "证书子集不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certCategoryCode)) {
			    errorMessage = "证书分类不能为空！";
			    return;
			}
			
			if (StringUtils.isEmpty(certNOItemId)) {
				errorMessage = "证书编号指标不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certName)) {
				errorMessage = "证书名称指标不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certCategoryItemId)) {
				errorMessage = "证书类别指标不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certOrg)) {
				errorMessage = "证书所属组织指标不能为空！";
				return;
			}
			
			String nbases = "";
			for (String nbase : userbaseList) {
			    nbases += nbase + ",";
			}
			
			HashMap<String, Object> pivMap = new HashMap<String, Object>();
			pivMap.put("isScheme", this.userView.hasTheFunction("4000305"));
			pivMap.put("addPiv", PubFunc.encrypt("4000301"));
			pivMap.put("updatePiv", PubFunc.encrypt("4000301"));
			pivMap.put("delPiv", PubFunc.encrypt("4000302"));
			// 增加批量修改
			pivMap.put("batchUpdatePiv", PubFunc.encrypt("4000309"));
			
			StringBuffer menuJson = new StringBuffer("[");
			if(this.userView.hasTheFunction("4000303")) {
				menuJson.append("{text : '导入证书信息',handler : certificateManage.importCertificateInfo},");
				menuJson.append("{text : '导入证书附件',handler : certificateManage.ImportAttachment}");
			}
			
			if(this.userView.hasTheFunction("4000304"))
			    menuJson.append(",{text: '导出Excel',handler: certificateManage.exportDate}");
			
			if(this.userView.hasTheFunction("4000308"))
				menuJson.append(",{text: '证书借阅台账',handler: certificateManage.CertifiExcelInfo}");
			
			menuJson.append("]");
			if(menuJson.length() < 3)
				menuJson.setLength(0);
			
			pivMap.put("menuJson", menuJson.toString());
			
			pivMap.put("borrowPiv", this.userView.hasTheFunction("4000306"));
			pivMap.put("returnPiv", this.userView.hasTheFunction("4000307"));
			pivMap.put("ledgerPiv", this.userView.hasTheFunction("4000308"));
			
			String date = DateUtils.format(new Date(), "yyyy-MM-dd");
			StringBuffer where = new StringBuffer();
			where.append(" and " + certCategoryItemId);
			where.append(" in (select codeitemid from codeitem");
			where.append(" where codesetid='" + certCategoryCode + "'");
			where.append(" and " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
            where.append(">='" + date + "')");
			
			this.getFormHM().put("fieldSet", fieldSetId);
			this.getFormHM().put("nbases", nbases);
			this.getFormHM().put("certName", certName);
			this.getFormHM().put("certNOItemId", certNOItemId);
			this.getFormHM().put("certCategoryItemId", certCategoryItemId);
			this.getFormHM().put("pivMap", pivMap);
			this.getFormHM().put("certOrg", certOrg);
			// 48114 由于过滤器增加SQL校验，所以这里传SQL按加密处理
			this.getFormHM().put("whereFilter", PubFunc.encrypt(where.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("errorMsg", errorMessage);
		}
	}

}
