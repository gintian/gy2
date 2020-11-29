package com.hjsj.hrms.transaction.kq.register;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ExportCustomTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tablename = (String) this.getFormHM().get("tablename");
		tablename=tablename!=null?tablename:"Q03";
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		ArrayList listid = new ArrayList();
		ArrayList showlist = new ArrayList();
		
		for(int i = 0;i<fieldlist.size();i++){
			FieldItem field = (FieldItem) fieldlist.get(i);
			if("nbase".equalsIgnoreCase(field.getItemid())|| "A0100".equalsIgnoreCase(field.getItemid())||
			   "Q03z5".equalsIgnoreCase(field.getItemid())|| "Q03Z0".equalsIgnoreCase(field.getItemid())||
			   "E01a1".equalsIgnoreCase(field.getItemid())|| "state".equalsIgnoreCase(field.getItemid())||
			   "Q03z3".equalsIgnoreCase(field.getItemid())|| "I9999".equalsIgnoreCase(field.getItemid())||
			   "A0101".equalsIgnoreCase(field.getItemid())|| "Q03z0".equalsIgnoreCase(field.getItemid())||
			   "Q0313".equalsIgnoreCase(field.getItemid())|| "Q0315".equalsIgnoreCase(field.getItemid())){
				continue;
			}
			listid.add(field.getItemid());
			showlist.add(field.getItemdesc());
		}
	    
		this.getFormHM().put("listid", listid);
		this.getFormHM().put("showlist", showlist);
		
	}
}
