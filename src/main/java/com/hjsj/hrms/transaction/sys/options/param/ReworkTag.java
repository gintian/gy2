package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ReworkTag extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String reworkname = (String)this.getFormHM().get("reworkname");
		boolean flag = true;
			ArrayList fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fielditemlist.size();i++){
				FieldSet fs = (FieldSet)fielditemlist.get(i);
				if(fs!=null&&reworkname.equalsIgnoreCase(fs.getCustomdesc())){
					flag = false;
					break;
				}
			}
		String errmes = null;
			if(flag){
				String reworkoldname = (String)this.getFormHM().get("reworkoldname");
				String reworktag = (String)this.getFormHM().get("reworktag");
				SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
				errmes = infoxml.updateTag(reworktag,reworkoldname,reworkname);
			}else{
				errmes="11";
			}
		this.getFormHM().put("errmes",errmes);

	}

}
