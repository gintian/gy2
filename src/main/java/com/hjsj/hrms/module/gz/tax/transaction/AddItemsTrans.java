package com.hjsj.hrms.module.gz.tax.transaction;

import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddItemsTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			String fields = (String) this.getFormHM().get("strData");
			String deptid = (String) this.getFormHM().get("isComputeDep");
			deptid= "1".equals(deptid)?"true":"false";

			fields = fields==null?"":fields;
//			JSONArray jsonArray = JSONArray.fromObject(jsonStr);
//			StringBuffer fields = new StringBuffer();
//			JSONObject obj = null;
//        	for (int i = 0; i < jsonArray.size(); i++) {
//        		fields.append(jsonArray.getJSONObject(i).getString("itemid")+",");
//    			obj = jsonArray.getJSONObject(i);
//        	}
			String[] field = fields.replaceFirst("/", "").trim().toString().split("/");//传入字符串开头会有一个/ 所以移除掉第一个/
			
			TaxMxBo bo = new TaxMxBo(this.frameconn, this.userView);
//			if(org.apache.commons.lang.StringUtils.isBlank(deptid))
//				deptid=bo.getDeptID();
			bo.updateTaxMxField(field, deptid);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
