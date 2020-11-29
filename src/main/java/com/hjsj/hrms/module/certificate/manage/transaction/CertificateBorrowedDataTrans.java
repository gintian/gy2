package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.utils.CertificatePrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

public class CertificateBorrowedDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		RowSet rs = null;
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String fieldSetid = bo.getCertBorrowSubset().toLowerCase();
			String certCategoryCode = bo.getCertCategoryCode();
			ArrayList dbnames = bo.getCertNbase();
			String certSubset = bo.getCertSubset();
			String certCategoryItemId = bo.getCertCategoryItemId();
			String certNOItemId = bo.getCertNOItemId();
			String certOrg = bo.getCertOrganization();
			if(StringUtils.isEmpty(fieldSetid)) {
				errorMsg = "请设置证书借阅记录子集！";
				return;
			}
			
			if (StringUtils.isEmpty(certCategoryCode)) {
				errorMsg = "请设置证书类别代码类！";
				return;
			}
			
			ArrayList<String> userbaseList = bo.getCertNbase();
			if (userbaseList == null || userbaseList.size() == 0) {
				errorMsg = "人员库不能为空！";
				return;
			}
			
			FieldItem fi = DataDictionary.getFieldItem(fieldSetid + "03", fieldSetid);
			if(fi == null) {
				errorMsg = "证书编号指标不存在！";
				return;
			}
				
			fi = DataDictionary.getFieldItem(fieldSetid + "05", fieldSetid);
			if(fi == null) {
				errorMsg = "证书名称指标不存在！";
				return;
			}
			
			fi = DataDictionary.getFieldItem(fieldSetid + "01", fieldSetid);
			if(fi == null) {
				errorMsg = "证书类别指标不存在！";
				return;
			}
			
			fi = DataDictionary.getFieldItem(fieldSetid + "07", fieldSetid);
			if(fi == null) {
				errorMsg = "证书所有人指标不存在！";
				return;
			}
			
			fi = DataDictionary.getFieldItem(fieldSetid + "09", fieldSetid);
			if(fi == null) {
				errorMsg = "借阅日期指标不存在！";
				return;
			}
			
			fi = DataDictionary.getFieldItem(fieldSetid + "13", fieldSetid);
			if(fi == null) {
				errorMsg = "借阅事由指标不存在！";
				return;
			}

			String personid = (String) this.getFormHM().get("personid");
			personid = PubFunc.decrypt(personid);
			String nbase = personid.substring(0, 3);
			personid = personid.substring(3);
			
			if(!userbaseList.contains(nbase)) {
				errorMsg = "人员不在权限范围内！";
				return;
			}
			
			StringBuffer columns = new StringBuffer();
			columns.append(fieldSetid + "03,");
			columns.append(fieldSetid + "05,");
			columns.append(fieldSetid + "01,");
			columns.append(fieldSetid + "07,");
			columns.append(Sql_switcher.dateToChar(fieldSetid + "09", "yyyy-MM-dd"));
			columns.append(" " + fieldSetid + "09,");
			columns.append(fieldSetid + "13");		
			
			StringBuffer subsetColumns = new StringBuffer();
			subsetColumns.append(certCategoryItemId + ",");
			subsetColumns.append(certNOItemId + ",");
			subsetColumns.append(certOrg);
			
			CertificatePrivBo privBo = new CertificatePrivBo();
			String whereSql = privBo.getCertOrgWhere(this.frameconn, userView, certSubset);
			StringBuffer subsetSql = new StringBuffer();
			for (int i = 0; i < dbnames.size(); i++) {
				String dbname = (String) dbnames.get(i);
				String CertSet = dbname + certSubset;
				if (StringUtils.isNotEmpty(subsetSql.toString()))
					subsetSql.append(" union all ");
				
				subsetSql.append("select " + subsetColumns);
				subsetSql.append(" from " + CertSet + " " + certSubset);
				subsetSql.append(" where " + whereSql);
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append(columns);
			sql.append(" from " + nbase + fieldSetid + " " + fieldSetid);
			sql.append(" right join (" + subsetSql + ") " + certSubset);
			sql.append(" on " + certSubset + "." + certCategoryItemId);
			sql.append("=" + fieldSetid + "." + fieldSetid + "01");
			sql.append(" and " + certSubset + "." + certNOItemId);
			sql.append("=" + fieldSetid + "." + fieldSetid + "03");
			sql.append(" where a0100=?");
			sql.append(" and " + fieldSetid + "19=?");
			sql.append(" and (" + fieldSetid + "23=2");
			sql.append(" or " + fieldSetid + "23 is null");
			sql.append(" or "+ fieldSetid + "23='')" );
			
			ArrayList<String> paramLsit = new ArrayList<String>();
			paramLsit.add(personid);
			paramLsit.add("03");
			ArrayList<LazyDynaBean> valueList = new ArrayList<LazyDynaBean>();
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search(sql.toString(), paramLsit);
			while(rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set(fieldSetid + "03", rs.getString(fieldSetid + "03"));
				bean.set(fieldSetid + "05", rs.getString(fieldSetid + "05"));
				String  certCodeitemValue =  AdminCode.getCodeName(certCategoryCode, rs.getString(fieldSetid + "01"));
				bean.set(fieldSetid + "01", rs.getString(fieldSetid + "01") + "`" + certCodeitemValue);
				bean.set(fieldSetid + "07", rs.getString(fieldSetid + "07"));
				bean.set(fieldSetid + "09", rs.getString(fieldSetid + "09"));
				bean.set(fieldSetid + "13", rs.getString(fieldSetid + "13"));	
				bean.set("operation", "归还");	
				valueList.add(bean);
			}
			
			this.getFormHM().put("data", valueList);
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
		} finally {
			this.getFormHM().put("errorMsg", errorMsg);
		}
		
	}

}
