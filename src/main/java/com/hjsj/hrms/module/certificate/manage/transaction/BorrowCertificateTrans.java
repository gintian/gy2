package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class BorrowCertificateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		RowSet rs = null;
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			ArrayList<String> userbaseList = bo.getCertNbase();
			String recordFieldSet = bo.getCertBorrowSubset();
			String fieldSetId = bo.getCertSubset();
			String certNOItemId = bo.getCertNOItemId();
			// 证书信息集证书类别指标
			String certCategoryItemId = bo.getCertCategoryItemId();
			// 证书信息集证书名称
			String certName = bo.getCertName();
			String certBorrowState = bo.getCertBorrowState();
			if (userbaseList == null || userbaseList.size() == 0) {
				errorMsg = "人员库不能为空！";
				return;
			}

			if (StringUtils.isEmpty(fieldSetId)) {
				errorMsg = "请设置证书子集！";
				return;
			}
			
			if (StringUtils.isEmpty(recordFieldSet)) {
				errorMsg = "请设置证书借阅记录子集！";
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

			if (StringUtils.isEmpty(certName)) {
				errorMsg = "请设置证书名称指标！";
				return;
			}

			if (StringUtils.isEmpty(certBorrowState)) {
				errorMsg = "请设置证书名称指标！";
				return;
			}

			ArrayList browStoreData = (ArrayList) this.getFormHM().get("records");
			String personId = (String) this.getFormHM().get("personId");
			personId = PubFunc.decrypt(personId);
			String nbase = personId.substring(0, 3);
			personId = personId.substring(3);
			String browDate = (String) this.getFormHM().get("browDate");
			String retunDate = (String) this.getFormHM().get("retunDate");
			String borrowDesc = (String) this.getFormHM().get("borrowDesc");
			// 借阅子集维护的其他指标集合
    		ArrayList fieldsData = (ArrayList) this.getFormHM().get("fieldsData");
    		
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.frameconn);
			int num = 0;
			sql.append("select max(I9999) I9999  from ").append(nbase).append(recordFieldSet);
			rs = dao.search(sql.toString());
			if (rs.next())
				num = rs.getInt("I9999");
			
			Timestamp browDateT = new Timestamp(DateUtils.getDate(browDate, "yyyy-MM-dd").getTime());
			Timestamp retunDateT = new Timestamp(DateUtils.getDate(retunDate, "yyyy-MM-dd").getTime());
			StringBuffer insertSql = new StringBuffer("");
			insertSql.append("insert into ").append(nbase).append(recordFieldSet);
			insertSql.append(" (I9999,A0100," + recordFieldSet + "01,");
			insertSql.append(recordFieldSet + "03,");
			insertSql.append(recordFieldSet + "05,");
			insertSql.append(recordFieldSet + "07,");
			insertSql.append(recordFieldSet + "09,");
			insertSql.append(recordFieldSet + "11,");
			insertSql.append(recordFieldSet + "13,");
			insertSql.append(recordFieldSet + "19,");
			insertSql.append(recordFieldSet + "23,");
			insertSql.append("CreateTime,");
			insertSql.append("ModTime,");
			insertSql.append("CreateUserName,");
			insertSql.append("ModUserName");
//			insertSql.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			StringBuffer insertValueSql = new StringBuffer("");
    		insertValueSql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
    		
			// 处理其他指标集合
    		ArrayList fieldValues = new ArrayList();
    		for(int i=0;i<fieldsData.size();i++) {
    			MorphDynaBean map = (MorphDynaBean)fieldsData.get(i);
    			String itemid = (String)map.get("itemid");
    			String itemtype = (String)map.get("itemtype");
    			String codesetid = (String)map.get("codesetid");
    			String value = (String)map.get("value");
    			
    			if("A".equalsIgnoreCase(itemtype)) {
    				if(!"0".equals(codesetid))
    					value = value.split("`")[0];
    				fieldValues.add(value);
    			}else if("N".equalsIgnoreCase(itemtype)) {
    				fieldValues.add(Integer.parseInt(value));
    			}else if("D".equalsIgnoreCase(itemtype)) {
    				Timestamp date = null;
    				if(StringUtils.isNotBlank(value))
    					date = new Timestamp(DateUtils.getDate(value, "yyyy-MM-dd").getTime());
    				fieldValues.add(date);
    			}else
    				fieldValues.add(value);
    			
    			insertSql.append(","+itemid);
    			insertValueSql.append(",?");
    		}
    		insertSql.append(")");
    		insertValueSql.append(")");
    		sql.setLength(0);
    		sql.append(insertSql.toString()).append(" values ").append(insertValueSql.toString());
			
			ArrayList<String> sqlList = new ArrayList<String>();
			for(String dbname : userbaseList) {
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("update " + dbname + fieldSetId);
				updateSql.append(" set " + certBorrowState + "=?");
				updateSql.append(" where " + certCategoryItemId + "=?");
				updateSql.append(" and " + certNOItemId + "=?");
				sqlList.add(updateSql.toString());
			}
			// 优化变量标识
			Timestamp timestamp = new Timestamp(new Date().getTime());
    		String userFullName = this.userView.getUserFullName();
    		
			ArrayList<ArrayList<Object>> valuesList = new ArrayList<ArrayList<Object>>();
			ArrayList<ArrayList<String>> updateParamLsit = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < browStoreData.size(); i++) {
				num++;
				MorphDynaBean bean = (MorphDynaBean) browStoreData.get(i);
				ArrayList<Object> valueList = new ArrayList<Object>();
				valueList.add(num);
				valueList.add(personId);
				String cerType = (String) bean.get(certCategoryItemId);
				valueList.add(cerType.split("`")[0]);
				valueList.add((String) bean.get(certNOItemId));
				valueList.add((String) bean.get(certName));
				valueList.add((String) bean.get("a0101"));
				valueList.add(browDateT);
				valueList.add(retunDateT);
				valueList.add(borrowDesc);
				valueList.add("03");
				// 归还标识45号是否代码类（1：已归还；2：未归还）
				valueList.add("2");
				valueList.add(timestamp);
				valueList.add(timestamp);
				valueList.add(userFullName);
				valueList.add(userFullName);
				// 增加其他指标数据
				valueList.addAll(fieldValues);
				
				valuesList.add(valueList);
				
				ArrayList<String> updateValueList = new ArrayList<String>();
				updateValueList.add("1");
				updateValueList.add(cerType.split("`")[0]);
				updateValueList.add((String) bean.get(certNOItemId));
				
				updateParamLsit.add(updateValueList);
			}

			dao.batchInsert(sql.toString(), valuesList);
			
			for(ArrayList<String> valueList : updateParamLsit) {
				ArrayList<ArrayList<String>> updateValueList = new ArrayList<ArrayList<String>>();
				for(int m = 0; m < userbaseList.size(); m++) {
					updateValueList.add(valueList);
				}
				
				dao.batchUpdate(sqlList, updateValueList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
		} finally {
			PubFunc.closeDbObj(rs);
			this.getFormHM().put("errorMsg", errorMsg);
		}

	}

}
