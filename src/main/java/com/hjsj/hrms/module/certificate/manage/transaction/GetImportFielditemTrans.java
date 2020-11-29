package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 获取人员基本信息集和证书子集的有效的指标
 * @Title:        GetImportFielditemTrans.java
 * @Description:  获取人员基本信息集和证书子集的有效的权限内的指标
 * @Company:      hjsj     
 * @Create time:  2018年8月13日 下午2:37:46
 * @author        chenxg
 * @version       1.0
 */
public class GetImportFielditemTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String errorMessage = "";
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
		String certNOItemId = ccbo.getCertNOItemId();
		ArrayList<String> userbaseList = ccbo.getCertNbase();
		if (userbaseList == null || userbaseList.size() == 0) {
			errorMessage = "人员库不能为空！";
			return;
		}
		
		if (StringUtils.isEmpty(fieldSetId)) {
			errorMessage = "证书子集不能为空！";
			this.formHM.put("msg", errorMessage);
			return;
		}

		if (StringUtils.isEmpty(certNOItemId)) {
			errorMessage = "证书编号指标不能为空！";
			this.formHM.put("msg", errorMessage);
			return;
		}
		
		StringBuffer mainSetJson = new StringBuffer("[");
		ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET); 
		for(FieldItem fi : fieldItemList) {
			mainSetJson.append("{itemid:'" + fi.getItemid().toLowerCase() + "',");
			mainSetJson.append("itemdesc:'" + fi.getItemdesc() + "'},");
		}
		
		if(mainSetJson.toString().endsWith(","))
			mainSetJson.setLength(mainSetJson.length() - 1);
		
		mainSetJson.append("]");
		
		StringBuffer subSetJson = new StringBuffer("[");
		fieldItemList = DataDictionary.getFieldList(fieldSetId, Constant.USED_FIELD_SET); 
		for(FieldItem fi : fieldItemList) {
			subSetJson.append("{itemid:'" + fi.getItemid().toLowerCase() + "',");
			subSetJson.append("itemdesc:'" + fi.getItemdesc() + "'},");
		}
		
		if(subSetJson.toString().endsWith(","))
			subSetJson.setLength(subSetJson.length() - 1);
		
		subSetJson.append("]");
		
		this.formHM.put("msg", errorMessage);
		this.formHM.put("mainSetJson", mainSetJson.toString());
		this.formHM.put("subSetJson", subSetJson.toString());
		this.formHM.put("firstField", onlyField.toLowerCase());
		this.formHM.put("secondField", certNOItemId);
		
	}

}
