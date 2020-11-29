package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.dashboard.businessobject.CertificateDashboardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

public class CheckCertificateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		RowSet rs = null;
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String fieldSetId = bo.getCertSubset();
			String certNOItemId = bo.getCertNOItemId();
			ArrayList<String> userbaseList = bo.getCertNbase();
			String recordFieldSet = bo.getCertBorrowSubset();
			String certStatus = bo.getCertStatus();
			String certBorrowState = bo.getCertBorrowState();
			String certCategoryCode = bo.getCertCategoryCode();
			// 证书信息集证书类别指标
			String certCategoryItemId = bo.getCertCategoryItemId();
			// 证书信息集证书到期日期指标
			String certEndDateItemId = bo.getCertEndDateItemId();
			// 证书信息集证书名称
			String certName = bo.getCertName();
			if (userbaseList == null || userbaseList.size() == 0) {
				errorMsg = "人员库不能为空！";
				return;
			}

			if (StringUtils.isEmpty(fieldSetId)) {
				errorMsg = "请设置证书子集！";
				return;
			}

			if (StringUtils.isEmpty(certNOItemId)) {
				errorMsg = "请设置证书编号指标！";
				return;
			}

			if (StringUtils.isEmpty(recordFieldSet)) {
				errorMsg = "请设置证书借阅记录子集！";
				return;
			}

			if (StringUtils.isEmpty(certStatus)) {
				errorMsg = "请设置证书状态指标！";
				return;
			}
			
			if (StringUtils.isEmpty(certCategoryCode)) {
				errorMsg = "请设置证书类别代码类！";
				return;
			}

			if (StringUtils.isEmpty(certCategoryItemId)) {
				errorMsg = "请设置证书类别指标！";
				return;
			}

			if (StringUtils.isEmpty(certEndDateItemId)) {
				errorMsg = "请设置证书结束日期指标！";
				return;
			}

			if (StringUtils.isEmpty(certName)) {
				errorMsg = "请设置证书名称指标！";
				return;
			}

			if (StringUtils.isEmpty(certBorrowState)) {
				errorMsg = "请设置证书是否借出指标！";
				return;
			}

			ArrayList<String> allDbnameList = new ArrayList<String>();
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("select Pre from DBName");
			rs = dao.search(sql.toString());
			while (rs.next()) {
				allDbnameList.add(rs.getString("Pre"));
			}
			
			String nowDate = DateUtils.format(new Date(), "yyyy-MM-dd");
			sql.setLength(0);
			sql.append("select a.a0101,");
			sql.append("b." + certCategoryItemId + ",");
			sql.append("b." + certNOItemId + ",");
			sql.append("b." + certName + ",");
			sql.append("b." + certStatus + ",");
			sql.append("b." + certBorrowState + ",");
			sql.append(Sql_switcher.dateToChar("b." + certEndDateItemId, "yyyy-MM-dd"));
			sql.append(" " + certEndDateItemId);
			sql.append(" from #nbase#" + fieldSetId + " b");
			sql.append(" left join #nbase#a01 a");
			sql.append(" on b.a0100=a.a0100");
			sql.append(" where b." + certCategoryItemId + "=?");
			sql.append(" and b." + certNOItemId + "=?");
//			sql.append(" and b." + certStatus + "=?");
//			sql.append(" and b." + certBorrowState + "=?");
//			sql.append(" and (" + Sql_switcher.dateToChar("b." + certEndDateItemId, "yyyy-MM-dd"));
//			sql.append(">? or b."+ certEndDateItemId+ " IS NULL ");
			// 49411 到期日期为空的证书也可以借阅
//			if(Sql_switcher.searchDbServer() == Constant.MSSQL)
//				sql.append(" or b."+ certEndDateItemId+"='' ");
//			sql.append(")");

			String certificateIds = (String) this.getFormHM().get("certificateIds");
			String[] certificates = certificateIds.split(",");
			ArrayList<String> certificateLsit = new ArrayList<String>();
			HashMap<String, HashMap<String, String>> certificateMap = new HashMap<String, HashMap<String, String>>();
			int index = 0;
			for (String certificate : certificates) {
				ArrayList<String> paramList = new ArrayList<String>();
				String[] certificateinfo = certificate.split(":");
				paramList.add(certificateinfo[0].split("`")[0]);
				paramList.add(certificateinfo[1]);
//				paramList.add("01");
//				paramList.add("2");
//				paramList.add(nowDate);
				String nbase = certificateinfo[2];
				String searchSql = sql.toString().replace("#nbase#", nbase);
				rs = dao.search(searchSql, paramList);
				if (rs.next()) {
					if ("01".equalsIgnoreCase(rs.getString(certStatus))) {
						if ("2".equalsIgnoreCase(rs.getString(certBorrowState))) {
							if (rs.getString(certEndDateItemId)==null|| "".equals(rs.getString(certEndDateItemId))||rs.getString(certEndDateItemId).compareTo(nowDate)>0) {
								certificateLsit.add(certificate);
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("a0101", rs.getString("a0101"));
								String  certCodeitemValue =  AdminCode.getCodeName(certCategoryCode, rs.getString(certCategoryItemId));
								map.put(certCategoryItemId, rs.getString(certCategoryItemId) + "`" + certCodeitemValue);
								map.put(certNOItemId, rs.getString(certNOItemId));
								map.put(certName, rs.getString(certName));
								String certEndDateItemIdValue = rs.getString(certEndDateItemId);
								map.put(certEndDateItemId, (StringUtils.isBlank(certEndDateItemIdValue)) ? "" : certEndDateItemIdValue);
								map.put("operation", index + "");
								map.put("id", index + "");
								certificateMap.put(certificate, map);
								index++;
							}else {
								errorMsg += certificateinfo[3] + "证书已过期 </br>";
							}
						}else {
							errorMsg += certificateinfo[3] + "证书已借出</br>";
						}
					}else {
						errorMsg += certificateinfo[3] + "证书不可用 </br>";
					}
				}
			}

			sql.setLength(0);
			for (String nbase : allDbnameList) {
				if (sql.length() > 0)
					sql.append(" union all ");

				sql.append("select 1 from " + nbase + recordFieldSet);
				sql.append(" where " + recordFieldSet + "01=?");
				sql.append(" and " + recordFieldSet + "03=?");
				sql.append(" and " + recordFieldSet + "23=?");
			}
			// 表格记录集合
			StringBuffer certificateJson = new StringBuffer("[");
			for (String certificate : certificateLsit) {
				ArrayList<String> paramList = new ArrayList<String>();
				String[] certificateinfo = certificate.split(":");
				for (String nbase : allDbnameList) {
					paramList.add(certificateinfo[0].split("`")[0]);
					paramList.add(certificateinfo[1]);
					paramList.add("2");
				}
				
				rs = dao.search(sql.toString(), paramList);
				if (rs.next()) {
					errorMsg += certificateinfo[3] + ",";
					certificateMap.remove(certificate);
				} else {
					certificateJson.append("{");
					HashMap<String, String> map = certificateMap.get(certificate);
					Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> entry = iterator.next();
						certificateJson.append(entry.getKey() + ":'" + entry.getValue() + "',");
					}
					
					if(certificateJson.toString().endsWith(","))
						certificateJson.setLength(certificateJson.length() - 1);
					
					certificateJson.append("},");
				}
			}

			if(certificateJson.toString().endsWith(","))
				certificateJson.setLength(certificateJson.length() - 1);
			
			certificateJson.append("]");
			
//			if (errorMsg.length() > 0) {
//				errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
//				errorMsg += "等证书已过期、不可借或已借出！";
//			}
			// 列指标集合
			StringBuffer columns = new StringBuffer("[");
			columns.append("'" + certCategoryItemId + "',");
			columns.append("'" + certNOItemId + "',");
			columns.append("'" + certName + "',");
			columns.append("'a0101',");
			columns.append("'" + certEndDateItemId + "',");
			columns.append("'operation'");
			columns.append("]");
			// 列对象集合
			StringBuffer columnJson = new StringBuffer("[");
			FieldItem fi = DataDictionary.getFieldItem(certCategoryItemId, fieldSetId);
			if(fi == null) {
				errorMsg = "证书编号指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(certNOItemId, fieldSetId);
			if(fi == null) {
				errorMsg = "证书名称指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(certName, fieldSetId);
			if(fi == null) {
				errorMsg = "证书类别指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem("a0101", "A01");
			if(fi == null) {
				errorMsg = "证书所有人指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(certEndDateItemId, fieldSetId);
			if(fi == null) {
				errorMsg = "证书到期日期指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = new FieldItem();
			fi.setItemdesc("操作");
			fi.setItemid("operation");
			setcolumnJson(columnJson, fi);
			
			if(columnJson.toString().endsWith(","))
				columnJson.setLength(columnJson.length() - 1);
			
			columnJson.append("]");
			String nbases = this.userView.getDbpriv().toString();
			if(this.userView.isSuper_admin()) {
				nbases = "";
				rs = dao.search("select Pre from DBName");
				while (rs.next()) {
					nbases += rs.getString("Pre") + ",";
				}
			}
			
			this.formHM.put("columns", columns.toString());
			this.formHM.put("columnJson", columnJson.toString());
			this.formHM.put("certificateJson", certificateJson.toString());
			this.formHM.put("nbases", nbases);
			this.formHM.put("certEndDateItemId", certEndDateItemId);
			
			CertificateDashboardBo cerBo = new CertificateDashboardBo(this.frameconn, this.userView);
			// 处理其他指标集合
            this.getFormHM().put("fieldItems", cerBo.getfieldSetList());
		} catch (Exception e) {
			e.printStackTrace();
			this.formHM.put("errorMsg", e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
			this.formHM.put("errorMsg", errorMsg);
		}

	}
	
	private void setcolumnJson (StringBuffer columnJson, FieldItem fi) {
		columnJson.append("{text: '" + fi.getItemdesc() + "',");
		columnJson.append("dataIndex: '" + fi.getItemid().toLowerCase() + "',");
		if("operation".equals(fi.getItemid()))
			columnJson.append("renderer:certificateManage.rendererBorrowFun,");
		else if(!"0".equalsIgnoreCase(fi.getCodesetid())) 
			columnJson.append("renderer:certificateManage.showCodeitemDesc,");
		
		if("operation".equals(fi.getItemid()))
			columnJson.append("align:'center',");
		else if("N".equalsIgnoreCase(fi.getItemtype())) 
			columnJson.append("align:'right',");
		else
			columnJson.append("align:'left',");
		
		columnJson.append("sortable: false,width:100,menuDisabled:true},");
	}

}
