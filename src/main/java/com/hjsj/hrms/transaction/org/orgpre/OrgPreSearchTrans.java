package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OrgPreSearchTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		 
		 if(this.formHM.get("searchSet") != null){
		   
		     String searchSet = (String)this.formHM.get("searchSet");
			 this.formHM.remove("searchSet");
			 ArrayList itemlist = userView.getPrivFieldList(searchSet);
			 StringBuffer options = new StringBuffer();
			 for(int i=0;i<itemlist.size();i++){
				 FieldItem fi = (FieldItem)itemlist.get(i);
				 options.append(fi.getItemdesc()+":"+fi.getItemid()+":"+fi.getItemtype()+":"+fi.getCodesetid()+"|");
			 }
			 this.getFormHM().put("options", options.toString());
			 
		 }else if(this.formHM.get("itemid")!=null){
			 String itemid = (String)this.formHM.get("itemid");
			 
		 }else{
			 ArrayList  setlist = userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			 FieldSet fs = null;
			 for(int i=setlist.size()-1;i>=0;i--){
				 fs = (FieldSet)setlist.get(i);
				 if("B00".equals(fs.getFieldsetid())){
					 setlist.remove(i); break;
				 }
			 }
			 String privOrg = userView.getUnitIdByBusi("4");
			 privOrg = privOrg == null ?"":privOrg;
			 this.getFormHM().put("privOrg", privOrg);
			 this.getFormHM().put("setList", setlist);
			 this.getFormHM().put("querylike", "0");
			 
			 PosparameXML pos = new PosparameXML(this.frameconn); 
				String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
				ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
				ctrl_type= "1".equals(ctrl_type)?ctrl_type:"2";
				
				String levelctrl = pos.getValue(PosparameXML.AMOUNTS,"nextlevel");
				levelctrl=levelctrl!=null&&levelctrl.trim().length()>0?levelctrl:"0";
			this.getFormHM().put("ctrl_type", ctrl_type);
			this.getFormHM().put("levelctrl", levelctrl);
		 }
		 
	}
}
