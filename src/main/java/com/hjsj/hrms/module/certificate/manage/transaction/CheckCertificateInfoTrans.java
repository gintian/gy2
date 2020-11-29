package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckCertificateInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String fieldSetId = bo.getCertSubset();
			String certNOItemId = bo.getCertNOItemId();
			String certStatusitemId = bo.getCertStatus();
			// 证书信息集证书类别指标
			String certCategoryItemId = bo.getCertCategoryItemId();
			// 证书信息集证书名称
			String certNameItemId = bo.getCertName();
			String certOrgItemId = bo.getCertOrganization();
			String certBorrowStateItemId = bo.getCertBorrowState();
			String certCategoryCode = bo.getCertCategoryCode();
			ArrayList<String> certNbase = bo.getCertNbase();
			if (StringUtils.isBlank(fieldSetId)) {
				errorMsg = "证书子集不能为空！";
				return;
			}

			if (StringUtils.isBlank(certNOItemId)) {
				errorMsg = "请设置证书编号指标！";
				return;
			}

			if (StringUtils.isBlank(certStatusitemId)) {
				errorMsg = "请设置证书状态指标！";
				return;
			}

			if (StringUtils.isBlank(certCategoryItemId)) {
				errorMsg = "请设置证书类别指标！";
				return;
			}

			if (StringUtils.isBlank(certNameItemId)) {
				errorMsg = "请设置证书名称指标！";
				return;
			}
			
			if (StringUtils.isBlank(certOrgItemId)) {
				errorMsg = "请设置证书归属机构指标！";
				return;
			}
			
			if (StringUtils.isBlank(certBorrowStateItemId)) {
				errorMsg = "请设置证书是否借阅指标！";
				return;
			}
			
			if (StringUtils.isBlank(certCategoryCode)) {
				errorMsg = "请设置证书类别代码类！";
				return;
			}
			String certBorrowSubset = bo.getCertBorrowSubset();
			if (StringUtils.isBlank(certCategoryCode)) {
				errorMsg = "请设置证书借阅记录子集！";
				return;
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList<String> paramList = new ArrayList<String>();
			StringBuffer sql = new StringBuffer();
			/**
			 * 校验证书是否被借出
			 * 获取修改前的数据
			 */
			MorphDynaBean oldValueMap = (MorphDynaBean) this.getFormHM().get("oldValue");
			HashMap oldMap = PubFunc.DynaBean2Map(oldValueMap);
			String oldCertNO = (null==oldMap.get(certNOItemId)) ? "" : (String) oldMap.get(certNOItemId);
			String oldCertCategory = (null==oldMap.get(certCategoryItemId)) ? "" : (String) oldMap.get(certCategoryItemId);
			oldCertCategory = (oldCertCategory.indexOf("`")!=-1) ? oldCertCategory.split("`")[0] : "";
			// 如果编号与证书类别都不为空 则确定为修改证书，否则为新增证书
			if(StringUtils.isNotBlank(oldCertNO) && StringUtils.isNotBlank(oldCertCategory)) {
				for (String dbname : certNbase) {
					if(sql.length() > 0)
						sql.append(" union all ");
					sql.append("select '" + certBorrowSubset +"01' from " + dbname + certBorrowSubset);
					sql.append(" where  " + certBorrowSubset +"01=?");
					sql.append(" and  " + certBorrowSubset +"03=?");
					sql.append(" and  " + certBorrowSubset +"23=2");
					
					paramList.add(oldCertCategory);
					paramList.add(oldCertNO);
				}
				this.frowset = dao.search(sql.toString(), paramList);
				if(this.frowset.next()) {
					errorMsg = "该证书《"+(String) oldValueMap.get(certNameItemId) + "》已被借出不允许修改！";
					return;
				}
			}
			/**
			 * 维护后的数据
			 */
			MorphDynaBean valueMap = (MorphDynaBean) this.getFormHM().get("value");
			// 61879 保存时完善 证照必填项等校验信息
			String itemIds = ","+certCategoryItemId+","+certNameItemId+","+certStatusitemId+","+certOrgItemId+","
					+certBorrowStateItemId+","+certNOItemId+",";
			CertificateManageBo certificateManageBo = new CertificateManageBo(this.userView, this.frameconn);
			errorMsg = certificateManageBo.checkData(fieldSetId, itemIds, valueMap);
			if (StringUtils.isNotBlank(errorMsg)) {
				return;
			}
			
			String a0100 = (String) this.getFormHM().get("a0100");
			a0100 = PubFunc.decrypt(a0100);
			if(a0100.length() > 8)
				a0100 = a0100.substring(3);
			
			String nbase = (String) this.getFormHM().get("nbase");
			if(nbase.length() > 7)
				nbase = PubFunc.decrypt(nbase);
				
			if(nbase.length() > 3)
				nbase = nbase.substring(nbase.indexOf("_") + 1);
			
			String i9999 = (String) this.getFormHM().get("i9999");
			
			String certNO = (String) valueMap.get(certNOItemId);
			String certCategory = (String) valueMap.get(certCategoryItemId);
			certCategory = certCategory.split("`")[0];
			paramList = new ArrayList<String>();
			sql.setLength(0);
			for (String dbname : certNbase) {
				if(sql.length() > 0)
					sql.append(" union all ");
				
				sql.append("select '" + dbname +"' nbase,a0100,i9999 from " + dbname + fieldSetId);
				sql.append(" where  " + certCategoryItemId +"=?");
				sql.append(" and  " + certNOItemId +"=?");
				
				paramList.add(certCategory);
				paramList.add(certNO);
			}
			
			this.frowset = dao.search(sql.toString(), paramList);
			if(this.frowset.next()) {
				String nbasetemp = this.frowset.getString("nbase");
				String a0100temp = this.frowset.getString("a0100");
				String i9999temp = this.frowset.getString("i9999");
				
				if(!nbasetemp.equalsIgnoreCase(nbase) || !a0100temp.equalsIgnoreCase(a0100)
						|| !i9999temp.equalsIgnoreCase(i9999)) {
					String itemDesc = AdminCode.getCodeName(certCategoryCode, certCategory);
					FieldItem certNOItem = DataDictionary.getFieldItem(certNOItemId, fieldSetId);
					FieldItem certCategoryItem = DataDictionary.getFieldItem(certCategoryItemId, fieldSetId);
					errorMsg = "系统中已存在" + certCategoryItem.getItemdesc() + "为：" + itemDesc
							+ "、" + certNOItem.getItemdesc() + "为：" + certNO + "的证书！";
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("errorMsg", errorMsg);
		}
	}

}
