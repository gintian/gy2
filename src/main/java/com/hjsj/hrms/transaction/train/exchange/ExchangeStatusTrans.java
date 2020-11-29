package com.hjsj.hrms.transaction.train.exchange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ExchangeStatusTrans extends IBusiness {

	public void execute() throws GeneralException {
		//HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5701 = this.getFormHM().get("sel").toString();
		//hm.remove("r5701");
		String r5713 = this.getFormHM().get("r5713").toString();
		//	hm.remove("status");
		
		String sql = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		if(r5701==null||r5701.length()<1||r5713==null||r5713.length()<1)
			return;
		
		try {
			String [] ids = r5701.split(",");
			if(ids.length > 0 ){				
				for(int i = 0 ; i < ids.length ; i ++){				
					if("09".equals(r5713)){//暂停操作
						sql = "update r57 set r5713='09' where r5701="+ids[i];
						dao.update(sql);
					}else{//发布操作
						sql = "update r57 set r5713='04' where r5701="+ids[i];
						dao.update(sql);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
