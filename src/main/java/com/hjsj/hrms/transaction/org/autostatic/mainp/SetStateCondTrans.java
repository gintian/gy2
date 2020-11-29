package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetStateCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		 TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"4";
		reqhm.remove("type");

		String expresion = (String)reqhm.get("expresion");
		expresion=expresion!=null&&expresion.trim().length()>0?expresion:"";
		reqhm.remove("expresion");
		
		expresion=SafeCode.decode(expresion);
		expresion=PubFunc.keyWord_reback(expresion);
		
		ArrayList setlist = new ArrayList();
		if("1".equals(type)){
			setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		}else if("2".equals(type)){
			setlist=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}else if("3".equals(type)){
			setlist=this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
		}else if("4".equals(type)){
			setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			setlist.addAll(this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET));
			setlist.addAll(this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET));
		}
		ArrayList fieldsetlist = new ArrayList();
		for(int i=0;i<setlist.size();i++){
			FieldSet fieldset = (FieldSet)setlist.get(i);
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;	
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			fieldsetlist.add(obj);
		}
		
		this.getFormHM().put("setlist",fieldsetlist);
		tableStr(expresion);
	}
	private void tableStr(String expresion){
		StringBuffer buf = new StringBuffer();
		
		if(expresion!=null&&expresion.length()>0){
			String expresionarr[] = expresion.split("\\|");
			if(expresionarr!=null&&expresionarr.length==2){
				String factor = expresionarr[0];
				String lexpr = expresionarr[1];
				lexpr=lexpr.replaceAll("%20","+");
				buf.append(factorStr(factor));
				buf.append("||"+lexpr);
			}
			this.getFormHM().put("tablestr",SafeCode.encode(buf.toString()));
		}else{
			this.getFormHM().put("tablestr","");
		}
	}
	private String factorStr(String factor){
		StringBuffer buf = new StringBuffer();
		String arr[] = factor.split("`");
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				String eq = "";
				if(arr[i].indexOf("<>")!=-1){
					eq = "<>";
				}else if(arr[i].indexOf("<=")!=-1){
					eq = "<=";
				}else if(arr[i].indexOf(">=")!=-1){
					eq = ">=";
				}else if(arr[i].indexOf("=")!=-1){
					eq = "=";
				}else if(arr[i].indexOf("<")!=-1){
					eq = "<";
				}else if(arr[i].indexOf(">")!=-1){
					eq = ">";
				}
				String fa = arr[i].replaceAll("<>",":").replaceAll(">=",":").replaceAll("<=",":").replaceAll("=",":");
				fa=fa.replaceAll("<",":").replaceAll(">",":");
				String itemarr[] = fa.split(":");
				if(itemarr.length>0){
					String itemid = itemarr[0];
					if(itemid.length()>1){
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String desc = itemarr.length==2?itemarr[1]:"";
							if(fielditem.isCode()){
								String code = "";
								if(desc.indexOf("*")!=-1 || desc.indexOf("?")!=-1)
									code = desc+","+desc;
								else
									code = desc+","+AdminCode.getCodeName(fielditem.getCodesetid(),desc);
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+code);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}else{
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+desc);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}
						}
					}
				}
			}
		}
		return buf.toString();
	}
}
