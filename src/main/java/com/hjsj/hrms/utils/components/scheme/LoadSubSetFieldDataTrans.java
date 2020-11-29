package com.hjsj.hrms.utils.components.scheme;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadSubSetFieldDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String fieldsetid = (String) this.formHM.get("fieldsetid");
		fieldsetid = (fieldsetid==null||fieldsetid.trim().length()==0)? "":fieldsetid;
		ArrayList dataList = new ArrayList();
		String sql = "select itemid,itemdesc,itemtype,codesetid from fielditem where useflag=1 and fieldsetid=?";
		ArrayList list = new ArrayList();
		list.add(fieldsetid);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql, list);
			while(this.frowset.next()){
				HashMap map = new HashMap();
				map.put("valueitemid", this.frowset.getString("itemid"));
				map.put("valuedesc", this.frowset.getString("itemdesc"));
				map.put("itemtype", this.frowset.getString("itemtype"));
				map.put("codesetid", this.frowset.getString("codesetid"));
				dataList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		HashMap initDataMap = new HashMap();
		initDataMap.put("valueitemid","　");
		initDataMap.put("valuedesc","");
		initDataMap.put("itemtype","");
		initDataMap.put("codesetid","");
		dataList.add(0,initDataMap);
		this.formHM.put("objectivedata", dataList);
	}

}
