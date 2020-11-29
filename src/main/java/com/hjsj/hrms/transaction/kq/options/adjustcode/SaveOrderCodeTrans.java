package com.hjsj.hrms.transaction.kq.options.adjustcode;

import com.hjsj.hrms.businessobject.kq.set.AdjustCode;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveOrderCodeTrans  extends IBusiness{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute()throws GeneralException
    {
       String table=(String)this.getFormHM().get("table");
       if(table==null||table.length()<=0)
    	   this.getFormHM().put("types","lost");
       ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");
       String types="0";
       String flag=(String)this.getFormHM().get("flag");
       String isSave=(String)this.getFormHM().get("isSave");
       if(flag==null||flag.length()<=0)
            flag="";
       if (isSave == null || !"no".equals(isSave)) {
			try {
				new AdjustCode().SaveOrderCode(code_fields, table,
						this.frameconn);
				DataDictionary.refresh();
				types = "ok";
			} catch (Exception e) {
				throw GeneralExceptionHandler
						.Handle(new GeneralException("", ResourceFactory
								.getProperty("kq.machine.error"), "", ""));
			}
       } else {
			ArrayList fieldlist = DataDictionary.getFieldList(table
					.toUpperCase(), Constant.USED_FIELD_SET);
			for (int i = 1; i <= code_fields.size(); i++) {
				String itemid = (String) code_fields.get(i - 1);
				if (itemid == null || itemid.length() <= 0) {
					continue;
				}
				for (int r = 0; r < fieldlist.size(); r++) {
					FieldItem fielditem = (FieldItem) fieldlist.get(r);
					if (fielditem.getItemid().equalsIgnoreCase(itemid))
						fielditem.setDisplayid(i);
				}
			}
			types = "ok";
       }
       this.getFormHM().put("types",types);
	}

}
