/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author Owner
 * 
 */
public class ChangeFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fieldsetid = (String) this.getFormHM().get("fieldsetid");

		ArrayList fielditemlist = new ArrayList();
		ArrayList fielditem = DataDictionary.getFieldList(fieldsetid,
				Constant.USED_FIELD_SET);
		for (int i = 0; i < fielditem.size(); i++) {
			FieldItem fielditems = (FieldItem) fielditem.get(i);
			if (!"0".equals(fielditems.getCodesetid())) {// 排除非代码型指标
				CommonData dataobj = new CommonData();
				dataobj = new CommonData(fielditems.getItemid(), fielditems
						.getItemdesc());
				fielditemlist.add(dataobj);
			}
		}
		this.getFormHM().put("fielditemlist", fielditemlist);
	}
}
