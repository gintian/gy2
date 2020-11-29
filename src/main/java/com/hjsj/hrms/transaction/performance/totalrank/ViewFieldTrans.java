package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ViewFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ConstantXml cx = new ConstantXml(this.getFrameconn(),"ZYXY_PARAM","Params");
		String  kh_set = cx.getTextValue("/Params/kh_set");
		kh_set=kh_set!=null?kh_set:"";
		
		String  kh_set_look  = cx.getTextValue("/Params/kh_set_look ");
		kh_set_look=kh_set_look!=null?kh_set_look:"";
		
		String kh_setdesc = "";
		String kh_set_lookdesc = "";
		String kh_setArr[] = kh_set.split(",");
		for(int i=0;i<kh_setArr.length;i++){
			String setid = kh_setArr[i];
			if(setid!=null&&setid.trim().length()>0){
				if(!this.userView.isSuper_admin()){
					String priv = this.getUserView().analyseTablePriv(setid);
					if("0".equals(priv))
						continue;
					ArrayList checklist=this.userView.getPrivFieldList(setid, Constant.USED_FIELD_SET);
					if(checklist.size()<1)
						continue;
				}
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset!=null)
					kh_setdesc+=fieldset.getFieldsetdesc()+",";
			}
		}
		String kh_set_lookArr[] = kh_set_look.split(",");
		for(int i=0;i<kh_set_lookArr.length;i++){
			String setid = kh_set_lookArr[i];
			if(setid!=null&&setid.trim().length()>0){
				if(!this.userView.isSuper_admin()){
					String priv = this.getUserView().analyseTablePriv(setid);
					if("0".equals(priv))
						continue;
					ArrayList checklist=this.userView.getPrivFieldList(setid, Constant.USED_FIELD_SET);
					if(checklist.size()<1)
						continue;
				}
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset!=null)
					kh_set_lookdesc+=fieldset.getFieldsetdesc()+",";
			}
		}
		this.getFormHM().put("kh_setdesc", kh_setdesc);
		this.getFormHM().put("kh_set_lookdesc", kh_set_lookdesc);
	}
}
