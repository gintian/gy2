package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CertificateReturnDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String fieldBorrowSetId = bo.getCertBorrowSubset();
			if(StringUtils.isEmpty(fieldBorrowSetId)) {
				errorMsg = "请设置证书借阅记录子集！";
				return;
			}
			
			// 获取证书子集
			String fieldSetId = bo.getCertSubset();
			String certNOItemId = bo.getCertNOItemId();
			String certCategoryItemId = bo.getCertCategoryItemId();
			String certBorrowState = bo.getCertBorrowState();
			ArrayList<String> userbaseList = bo.getCertNbase();
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
			
			if (StringUtils.isEmpty(certCategoryItemId)) {
				errorMsg = "请设置证书类别指标！";
				return;
			}

			if (StringUtils.isEmpty(certBorrowState)) {
				errorMsg = "请设置证书是否借出指标！";
				return;
			}
			String certificateIds = (String) this.getFormHM().get("certificateIds");
			String a0100 = (String) this.getFormHM().get("personId");
			a0100 = PubFunc.decrypt(a0100);
			String nbase = a0100.substring(0, 3);
			a0100 = a0100.substring(3);
			String returnPerson = (String) this.getFormHM().get("returnPerson");
			String returnDate = (String) this.getFormHM().get("returnDate");
			String returnDesc = (String) this.getFormHM().get("returnDesc");
			
			StringBuffer sql = new StringBuffer();
			sql.append("update " + nbase + fieldBorrowSetId);
			sql.append(" set " + fieldBorrowSetId + "23=1,");
			sql.append(fieldBorrowSetId + "15=?,");
			sql.append(fieldBorrowSetId + "17=?,");
			sql.append(fieldBorrowSetId + "25=?,");
			sql.append("ModTime=?,");
			sql.append("ModUserName=?");
			sql.append(" where a0100=?");
			sql.append(" and " + fieldBorrowSetId + "01=?");
			sql.append(" and " + fieldBorrowSetId + "03=?");
			
			ArrayList<String> sqlList = new ArrayList<String>();
			for(String dbname : userbaseList) {
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("update " + dbname + fieldSetId);
				updateSql.append(" set " + certBorrowState + "=?");
				updateSql.append(" where " + certCategoryItemId + "=?");
				updateSql.append(" and " + certNOItemId + "=?");
				sqlList.add(updateSql.toString());
			}
			
			ArrayList<ArrayList<Object>> paramLsit = new ArrayList<ArrayList<Object>>();
			ArrayList<ArrayList<String>> updateParamLsit = new ArrayList<ArrayList<String>>();
			String[] certificateId = certificateIds.split(",");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			for(String certificate : certificateId) {
				ArrayList<Object> valueList = new ArrayList<Object>();
				int index = certificate.indexOf(":");
				if(index < 0)
					continue;
				
				valueList.add(new Timestamp(DateUtils.getDate(returnDate, "yyyy-MM-dd").getTime()));
				valueList.add(returnPerson);
				valueList.add(returnDesc);
				valueList.add(new Timestamp(DateUtils.getDate(date, "yyyy-MM-dd HH:mm:ss").getTime()));
				valueList.add(this.userView.getUserId());
				valueList.add(a0100);
				String cerType = certificate.substring(0, index);
				valueList.add(cerType.split("`")[0]);
				valueList.add(certificate.substring(index + 1));
				paramLsit.add(valueList);
				
				ArrayList<String> updateValueList = new ArrayList<String>();
				updateValueList.add("2");
				updateValueList.add(certificate.substring(0, index).split("`")[0]);
				updateValueList.add(certificate.substring(index + 1));
				
				updateParamLsit.add(updateValueList);
			}
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.batchUpdate(sql.toString(), paramLsit);
			for(ArrayList<String> valueList : updateParamLsit) {
				ArrayList<ArrayList<String>> updateValueList = new ArrayList<ArrayList<String>>();
				for(int m = 0; m < userbaseList.size(); m++) {
					updateValueList.add(valueList);
				}
				
				dao.batchUpdate(sqlList, updateValueList);
			}
			
			this.getFormHM().put("flag", "true");
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", errorMsg);
		} finally {
			this.getFormHM().put("errorMsg", errorMsg);
		}
		
	}

}
