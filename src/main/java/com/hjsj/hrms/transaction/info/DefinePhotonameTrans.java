/*
 * Created on 2005-6-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefinePhotonameTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String b_getformula = (String)hm.get("b_getformula");
		hm.remove("b_getformula");
		//ArrayList unit_code_fieldlist=DataDictionary.getFieldList("A01",Constant.USED_FIELD_SET);
		ArrayList unit_code_fieldlist=this.userView.getPrivFieldList("A01",Constant.USED_FIELD_SET);
		if(b_getformula==null)
			unit_code_fieldlist = fieldfilter(unit_code_fieldlist);
		else
			unit_code_fieldlist = fieldfilter1(unit_code_fieldlist);
		this.getFormHM().put("stringfieldlist", unit_code_fieldlist);
    }
	private ArrayList fieldfilter(ArrayList list){
		ArrayList l = new ArrayList();
		if(list==null)
			return l;
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if(("0".equals(fielditem.getCodesetid())||fielditem.getCodesetid().length()==0)&& "A".equalsIgnoreCase(fielditem.getItemtype())){
				CommonData cdata = new CommonData(/*fielditem.getItemid()*/fielditem.getItemdesc(),fielditem.getItemdesc()+"["+fielditem.getItemid().toUpperCase()+"]");
				l.add(cdata);
			}
		}
		return l;
	}
	private ArrayList fieldfilter1(ArrayList list){
		ArrayList l = new ArrayList();
		if(list==null)
			return l;
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
				CommonData cdata = new CommonData(fielditem.getItemdesc(),fielditem.getItemdesc());
				l.add(cdata);
		}
		return l;
	}
}
