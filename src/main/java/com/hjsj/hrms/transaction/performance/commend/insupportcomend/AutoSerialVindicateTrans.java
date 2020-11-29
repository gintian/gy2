package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutoSerialVindicateTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list=new ArrayList();
		try {
			String sql="select itemid,itemdesc,itemlength from t_hr_busifield where fieldsetid='p03' and itemtype='A' and codesetid='0' and itemid not in('a0100','a0101','nbase')";
			this.frecset=dao.search(sql);
			while(this.frecset.next()){
				String[] autos=new String[3];
				autos[0]=this.frecset.getString("itemid");
				autos[1]=this.frecset.getString("itemdesc");
				autos[2]=this.frecset.getString("itemlength");
				list.add(autos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("autolist", list);
	}
}
