package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 查询考勤是否调用了高级花名册
 * @author Owner
 *
 */
public class SelectMusterSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		String table = (String)this.getFormHM().get("table");
		table = table == null ? "#" : table;
		String tableId = table.substring(1);
		KqUtilsClass kqUtilsClass=new KqUtilsClass();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=kqUtilsClass.selectMuster("81", dao, tableId);
		String sortid="0";
		if(list!=null&&list.size()>0)
			sortid="1";
		this.getFormHM().put("sortid", sortid);
	}

}
