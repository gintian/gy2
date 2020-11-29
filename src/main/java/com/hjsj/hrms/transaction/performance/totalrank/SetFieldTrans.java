package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"kh_set";
		hm.remove("kh_set");
		
		ConstantXml cx = new ConstantXml(this.getFrameconn(),"ZYXY_PARAM","Params");
		String  kh_set = cx.getTextValue("/Params/kh_set");
		kh_set=kh_set!=null?kh_set:"";
		
		String  kh_set_look  = cx.getTextValue("/Params/kh_set_look ");
		kh_set_look=kh_set_look!=null?kh_set_look:"";
		
		ArrayList fielditemlist=this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		ArrayList fieldlist = new ArrayList();
		ArrayList setlist = new ArrayList();
		for(int i=0;i<fielditemlist.size();i++){
			FieldSet fieldset=(FieldSet)fielditemlist.get(i);
			if("A00".equals(fieldset.getFieldsetid())|| "A01".equals(fieldset.getFieldsetid()))
				continue;
			if("kh_set".equalsIgnoreCase(flag)){
				if(kh_set.toUpperCase().indexOf(fieldset.getFieldsetid())!=-1){
					CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
					setlist.add(dataobj);
					continue;
				}
			}
			if("kh_set_look".equalsIgnoreCase(flag)){
				if(kh_set_look.toUpperCase().indexOf(fieldset.getFieldsetid())!=-1){
					CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
					setlist.add(dataobj);
					continue;
				}
			}
			CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			fieldlist.add(dataobj);
		}
		this.getFormHM().put("fieldList", fieldlist);
		this.getFormHM().put("setlist", setlist);
		this.getFormHM().put("khtitle", flag);
	}
}
