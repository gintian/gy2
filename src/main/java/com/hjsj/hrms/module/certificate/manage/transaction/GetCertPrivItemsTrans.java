package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCertPrivItemsTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String fieldset = (String) this.getFormHM().get("node");
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if("root".equalsIgnoreCase(fieldset)) {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String certSubset = bo.getCertSubset();
			String certBorrowSubset = bo.getCertBorrowSubset();
			String fieldSets = ",A01," + certSubset + "," + certBorrowSubset + ",";
			ArrayList<FieldSet> tableList = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			if(tableList != null && !tableList.isEmpty()) {
				for(int i = 0; i < tableList.size(); i++) {
					FieldSet fieldSet = (FieldSet) tableList.get(i);
					int privStatus = fieldSet.getPriv_status();
					if("0".equalsIgnoreCase(fieldSet.getUseflag()) || (!this.userView.isSuper_admin() && 1 == privStatus)
							|| !fieldSets.contains("," + fieldSet.getFieldsetid() + ","))
						continue;
					
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", fieldSet.getFieldsetid());
	                map.put("text", fieldSet.getFieldsetdesc());

	                list.add(map);
				}
			}
			
		} else {
			ArrayList<FieldItem> fielitemList = this.userView.getPrivFieldList(fieldset,
					Constant.USED_FIELD_SET);
			for(FieldItem fi : fielitemList) {
				int privStatus = fi.getPriv_status();
				if("0".equalsIgnoreCase(fi.getUseflag())
						|| (!this.userView.isSuper_admin() && 1 == privStatus))
					continue;
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", fi.getItemid());
                map.put("fieldItemId", fi.getItemid());
                map.put("text", fi.getItemdesc());
                map.put("fieldItemType", fi.getItemtype());
                map.put("fieldSetId", fi.getFieldsetid());
                map.put("checked", false);
                map.put("leaf", true);

                list.add(map);
			}
		}
        
		this.formHM.put("children", list);
	}

}
