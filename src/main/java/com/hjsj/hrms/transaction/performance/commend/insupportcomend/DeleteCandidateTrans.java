package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteCandidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String selectId=(String)this.getFormHM().get("selectID");
			selectId=selectId.substring(1);
			String[] Arr = selectId.split(",");
			StringBuffer ids=new StringBuffer();
			for(int i=0;i<Arr.length;i++)
			{
				ids.append(",'");
				ids.append(Arr[i]);
				ids.append("'");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("delete from p03 where p0300 in (");
			sql.append(ids.toString().substring(1));
			sql.append(")");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(sql.toString(),new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
