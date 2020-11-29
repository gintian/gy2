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
import java.util.Iterator;

public class SaveHideViewCodeTrans extends IBusiness{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");
		String table = (String) this.getFormHM().get("table");
		String state[] = (String[]) this.getFormHM().get("state");
		String flag = (String) this.getFormHM().get("flag");
		String isSave = (String) this.getFormHM().get("isSave");
		if (flag == null || flag.length() <= 0)
			flag = "";
		if (isSave == null || !"no".equals(isSave)) {
			try {
				new AdjustCode().SaveHideViewCode(fieldlist, state,
						this.frameconn);
				DataDictionary.refresh();
			} catch (Exception e) {
				throw GeneralExceptionHandler
						.Handle(new GeneralException("", ResourceFactory
								.getProperty("kq.machine.error"), "", ""));
			}
		} else {// 日明细 临时保存指标显示或隐藏
			ArrayList fielditemlist = DataDictionary.getFieldList(table,
					Constant.USED_FIELD_SET);
			for(Iterator it = fielditemlist.iterator();it.hasNext();){
				FieldItem fielditem = (FieldItem) it.next();
				if("a0100".equals(fielditem.getItemid())){
					fielditem.setState("0");
					it.remove();
				}
			}
			for (int i = 0; i < fielditemlist.size(); i++) {
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				fielditem.setState(state[i]);
			}
		}
		this.getFormHM().put("isSave", "");
	}
}
