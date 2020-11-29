package com.hjsj.hrms.transaction.gz.tempvar;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
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
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		
		String[] fitem = sort.split(",");;
		if("undefined".equalsIgnoreCase(sort))
		{
			hm.put("info","ok");
			return;
		}
		String cState = (String)hm.get("cstate");
		cState=cState!=null&&cState.trim().length()>0?cState:"";
		if(cState.length()>0){
			int length = fitem.length;
			for(int i=0;i<length;i++){
				String sqlstr = "update midvariable set sorting="+(i+1)+" where nid="+fitem[i];
				try {
					dao.update(sqlstr);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		hm.put("info","ok");
	}

}
