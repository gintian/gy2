package com.hjsj.hrms.transaction.train.request;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 培训班批量修改
 * 
 * @author chenxg 2014-05-12
 * 
 */
public class BatchEditDataTrans extends IBusiness {

	public void execute() throws GeneralException {

		try {
			HashMap hm = this.getFormHM();

			ArrayList fieldlist = new ArrayList();

			ArrayList list = DataDictionary.getFieldList("R31", Constant.USED_FIELD_SET);
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				if (item == null)
					continue;

				if ("r3101".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("r3130".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("B0110".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("E0122".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("r3117".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("r3118".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("r3127".equalsIgnoreCase(item.getItemid()))
					continue;

				if ("r3131".equalsIgnoreCase(item.getItemid()))
					continue;

				fieldlist.add(item);

			}
			hm.put("datelist", fieldlist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
