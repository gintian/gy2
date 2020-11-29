package com.hjsj.hrms.transaction.general.chkformula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String tabid = (String)reqhm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		reqhm.remove("tabid");
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"0";
		reqhm.remove("flag");
		ArrayList sortlist = new ArrayList();
		if(tabid.length()>0){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search("select chkId,Name from hrpChkformula where tabid='"+tabid+"' and flag='"+flag+"' order by seq");
				while(this.frowset.next()){
					CommonData dataobj = new CommonData(this.frowset.getString("chkId"),
							this.frowset.getString("Name"));
					sortlist.add(dataobj);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("sortlist",sortlist);
		this.getFormHM().put("tabid",tabid);
	}

}
